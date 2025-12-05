package game;

import common.InputValidator;
import common.RandomGenerator;
import entities.Hero;
import entities.Monster;
import entities.Party;
import items.*;
import items.Spell.SpellType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing turn-based combat.
 * Handles monster spawning, turn order, damage calculation, and victory conditions.
 */
public class BattleController {

    private final List<Monster> monsterCatalog;
    private final RandomGenerator rng;

    // ANSI Colors
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_WHITE_BOLD = "\033[1;37m";

    public BattleController(List<Monster> monsterCatalog) {
        this.monsterCatalog = monsterCatalog;
        this.rng = RandomGenerator.getInstance();
    }

    public void startBattle(Scanner scanner, Party party) {
        List<Monster> enemies = spawnMonsters(party);
        System.out.println(ANSI_RED + "\n*** Battle Started! Enemies approaching: ***" + ANSI_RESET);
        for (Monster m : enemies) System.out.println("- " + m);

        int round = 1;
        boolean battleActive = true;

        while (battleActive) {
            System.out.println("\n" + ANSI_YELLOW + "=== Round " + round + " ===" + ANSI_RESET);

            if (!processHeroesTurn(scanner, party, enemies)) {
                battleActive = false;
                break;
            }

            if (enemies.stream().allMatch(Monster::isFainted)) {
                processVictory(party, enemies);
                battleActive = false;
                break;
            }

            processMonstersTurn(party, enemies);

            if (party.isPartyWipedOut()) {
                System.out.println(ANSI_RED + "The party has been defeated!" + ANSI_RESET);
                battleActive = false;
                break;
            }

            performRegeneration(party);
            round++;
        }
    }

    private List<Monster> spawnMonsters(Party party) {
        List<Monster> enemies = new ArrayList<>();
        int partySize = party.getSize();

        int targetLevel = party.getHeroes().stream()
                .mapToInt(Hero::getLevel)
                .max().orElse(1);

        for (int i = 0; i < partySize; i++) {
            Monster template = monsterCatalog.get(rng.nextInt(monsterCatalog.size()));

            Monster monster = new Monster(
                    template.getName(),
                    template.getType(),
                    targetLevel,
                    template.getBaseDamage() * (targetLevel / (double)Math.max(1, template.getLevel())),
                    template.getDefense() * (targetLevel / (double)Math.max(1, template.getLevel())),
                    template.getDodgeChance() * 100
            );
            enemies.add(monster);
        }
        return enemies;
    }

    private boolean processHeroesTurn(Scanner scanner, Party party, List<Monster> enemies) {
        for (Hero hero : party.getHeroes()) {
            if (hero.isFainted()) continue;
            if (enemies.stream().allMatch(Monster::isFainted)) break;

            System.out.println("\nIt is " + ANSI_PURPLE + hero.getName() + ANSI_RESET + "'s turn.");
            System.out.println(hero);

            boolean actionTaken = false;
            while (!actionTaken) {
                System.out.println("1. Attack");
                System.out.println("2. Cast Spell");
                System.out.println("3. Use Potion");
                System.out.println("4. Equip Gear");
                System.out.println("5. Info");
                System.out.println("6. Quit Game");

                int choice = InputValidator.getValidInt(scanner, ANSI_CYAN + "Action: " + ANSI_RESET, 1, 6);
                switch (choice) {
                    case 1: actionTaken = performAttack(scanner, hero, enemies); break;
                    case 2: actionTaken = performSpell(scanner, hero, enemies); break;
                    case 3: actionTaken = performPotion(scanner, hero); break;
                    case 4: performEquip(scanner, hero); break;
                    case 5: showBattleInfo(party, enemies); break;
                    case 6:
                        System.out.println(ANSI_RED + "Quitting Game..." + ANSI_RESET);
                        System.exit(0);
                        return false;
                }
            }
        }
        return true;
    }

    private boolean performAttack(Scanner scanner, Hero hero, List<Monster> enemies) {
        Monster target = selectMonster(scanner, enemies);
        if (target == null) return false;

        // CAP MONSTER DODGE AT 30% (Making it 70% chance to hit at worst)
        double monsterDodge = Math.min(0.30, target.getDodgeChance());

        if (rng.nextDouble() < monsterDodge) {
            System.out.println(target.getName() + " dodged the attack!");
            return true;
        }

        double weaponDmg = (hero.getEquippedWeapon() != null) ? hero.getEquippedWeapon().getDamage() : 0;
        double rawDamage = (hero.getStrength() + weaponDmg) * 0.05;

        double actualDamage = Math.max(0, rawDamage - (target.getDefense() * 0.05));

        target.setHp(target.getHp() - actualDamage);
        System.out.printf("%s attacks %s for " + ANSI_RED + "%.0f damage!" + ANSI_RESET + "\n", hero.getName(), target.getName(), actualDamage);

        if (target.isFainted()) System.out.println(ANSI_GREEN + target.getName() + " has been defeated!" + ANSI_RESET);

        return true;
    }

    private boolean performSpell(Scanner scanner, Hero hero, List<Monster> enemies) {
        List<Spell> spells = hero.getInventory().getSpells();
        if (spells.isEmpty()) {
            System.out.println(ANSI_YELLOW + "You have no spells!" + ANSI_RESET);
            return false;
        }

        System.out.println(ANSI_WHITE_BOLD + "--- Spellbook ---" + ANSI_RESET);
        for (int i = 0; i < spells.size(); i++) {
            System.out.println((i + 1) + ". " + spells.get(i));
        }
        System.out.println((spells.size() + 1) + ". Cancel");

        int choice = InputValidator.getValidInt(scanner, ANSI_CYAN + "Select Spell: " + ANSI_RESET, 1, spells.size() + 1);
        if (choice == spells.size() + 1) return false;

        Spell spell = spells.get(choice - 1);
        if (hero.getMana() < spell.getManaCost()) {
            System.out.println(ANSI_RED + "Not enough Mana!" + ANSI_RESET);
            return false;
        }

        Monster target = selectMonster(scanner, enemies);
        if (target == null) return false;

        hero.setMana(hero.getMana() - spell.getManaCost());

        double damage = spell.getDamage() + ((hero.getDexterity() / 10000.0) * spell.getDamage());
        target.setHp(target.getHp() - damage);

        if (!target.isFainted()) {
            if (spell.getType() == SpellType.ICE) {
                target.reduceDamage(target.getBaseDamage() * 0.1);
                System.out.println(target.getName() + "'s damage reduced by Ice!");
            } else if (spell.getType() == SpellType.FIRE) {
                target.reduceDefense(target.getDefense() * 0.1);
                System.out.println(target.getName() + "'s defense melted by Fire!");
            } else if (spell.getType() == SpellType.LIGHTNING) {
                target.reduceDodgeChance(target.getDodgeChance() * 0.1);
                System.out.println(target.getName() + "'s dodge reduced by Lightning!");
            }
        }

        System.out.printf("%s casts %s on %s for " + ANSI_RED + "%.0f damage!" + ANSI_RESET + "\n", hero.getName(), spell.getName(), target.getName(), damage);
        hero.getInventory().removeItem(spell);
        return true;
    }

    private boolean performPotion(Scanner scanner, Hero hero) {
        List<Potion> potions = hero.getInventory().getPotions();
        if (potions.isEmpty()) {
            System.out.println(ANSI_YELLOW + "No potions in inventory." + ANSI_RESET);
            return false;
        }

        System.out.println(ANSI_WHITE_BOLD + "--- Potions ---" + ANSI_RESET);
        for(int i=0; i<potions.size(); i++) System.out.println((i+1) + ". " + potions.get(i));

        int choice = InputValidator.getValidInt(scanner, ANSI_CYAN + "Use Potion: " + ANSI_RESET, 1, potions.size());
        Potion potion = potions.get(choice - 1);

        double val = potion.getAttributeIncrease();
        if (potion.affects("Health")) hero.setHp(hero.getHp() + val);
        if (potion.affects("Mana")) hero.setMana(hero.getMana() + val);
        if (potion.affects("Strength")) hero.setStrength(hero.getStrength() + val);
        if (potion.affects("Dexterity")) hero.setDexterity(hero.getDexterity() + val);
        if (potion.affects("Agility")) hero.setAgility(hero.getAgility() + val);

        System.out.println(ANSI_GREEN + hero.getName() + " used " + potion.getName() + "!" + ANSI_RESET);
        hero.getInventory().removeItem(potion);
        return true;
    }

    private void performEquip(Scanner scanner, Hero hero) {
        System.out.println("1. Weapons");
        System.out.println("2. Armor");
        int type = InputValidator.getValidInt(scanner, ANSI_CYAN + "Type: " + ANSI_RESET, 1, 2);

        if (type == 1) {
            List<Weapon> weps = hero.getInventory().getWeapons();
            if (weps.isEmpty()) { System.out.println(ANSI_YELLOW + "No weapons." + ANSI_RESET); return; }
            for(int i=0; i<weps.size(); i++) System.out.println((i+1) + ". " + weps.get(i));
            int sel = InputValidator.getValidInt(scanner, "Equip: ", 1, weps.size());
            hero.equipWeapon(weps.get(sel-1));
        } else {
            List<Armor> arms = hero.getInventory().getArmor();
            if (arms.isEmpty()) { System.out.println(ANSI_YELLOW + "No armor." + ANSI_RESET); return; }
            for(int i=0; i<arms.size(); i++) System.out.println((i+1) + ". " + arms.get(i));
            int sel = InputValidator.getValidInt(scanner, "Equip: ", 1, arms.size());
            hero.equipArmor(arms.get(sel-1));
        }
    }

    private void processMonstersTurn(Party party, List<Monster> enemies) {
        for (Monster monster : enemies) {
            if (monster.isFainted()) continue;

            List<Hero> aliveHeroes = party.getHeroes().stream()
                    .filter(h -> !h.isFainted())
                    .collect(Collectors.toList());

            if (aliveHeroes.isEmpty()) break;

            Hero target = aliveHeroes.get(rng.nextInt(aliveHeroes.size()));

            // CAP HERO DODGE AT 75% (Heroes can be very dodgy, but not invincible)
            double heroDodgeChance = target.getAgility() / (target.getAgility() + 1000.0);
            heroDodgeChance = Math.min(0.75, heroDodgeChance);

            if (rng.nextDouble() < heroDodgeChance) {
                System.out.println(target.getName() + " dodged " + monster.getName() + "'s attack!");
                continue;
            }

            double rawDmg = monster.getBaseDamage();
            double mitigation = (target.getEquippedArmor() != null) ? target.getEquippedArmor().getDamageReduction() : 0;
            double finalDmg = Math.max(0, rawDmg - (mitigation * 0.2));

            target.setHp(target.getHp() - finalDmg);
            System.out.printf("%s attacks %s for " + ANSI_RED + "%.0f damage!" + ANSI_RESET + "\n", monster.getName(), target.getName(), finalDmg);

            if (target.isFainted()) {
                System.out.println(ANSI_RED + target.getName() + " has fainted!" + ANSI_RESET);
            }
        }
    }

    private void performRegeneration(Party party) {
        for (Hero h : party.getHeroes()) {
            if (!h.isFainted()) {
                h.setHp(h.getHp() * 1.1);
                h.setMana(h.getMana() * 1.1);
            }
        }
        System.out.println(ANSI_CYAN + "Heroes regain some health and mana." + ANSI_RESET);
    }

    private void processVictory(Party party, List<Monster> enemies) {
        System.out.println(ANSI_GREEN + "\n*** VICTORY! ***" + ANSI_RESET);
        double goldReward = enemies.stream().mapToDouble(Monster::getLevel).sum() * 100;
        int xpReward = enemies.size() * 2;

        System.out.printf("Party gains %.0f Gold and %d XP!\n", goldReward, xpReward);

        for (Hero h : party.getHeroes()) {
            if (h.isFainted()) {
                System.out.println(h.getName() + " is revived.");
                h.revive();
            } else {
                h.addMoney(goldReward);
                h.gainExperience(xpReward);
            }
        }
    }

    private Monster selectMonster(Scanner scanner, List<Monster> enemies) {
        List<Monster> alive = enemies.stream().filter(m -> !m.isFainted()).collect(Collectors.toList());
        if (alive.isEmpty()) return null;

        System.out.println(ANSI_CYAN + "Select Target:" + ANSI_RESET);
        for(int i=0; i<alive.size(); i++) {
            System.out.println((i+1) + ". " + alive.get(i));
        }
        int choice = InputValidator.getValidInt(scanner, ANSI_CYAN + "Target: " + ANSI_RESET, 1, alive.size());
        return alive.get(choice - 1);
    }

    private void showBattleInfo(Party party, List<Monster> enemies) {
        System.out.println("\n" + ANSI_WHITE_BOLD + "--- Battle Status ---" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "HEROES:" + ANSI_RESET);
        party.getHeroes().forEach(System.out::println);
        System.out.println(ANSI_RED + "MONSTERS:" + ANSI_RESET);
        enemies.forEach(System.out::println);
        System.out.println("---------------------");
    }
}