package game;

import utils.ConsoleColors;
import utils.GameDataLoader;
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

    public BattleController(List<Monster> monsterCatalog) {
        this.monsterCatalog = monsterCatalog;
        this.rng = RandomGenerator.getInstance();
    }

    public void startBattle(Scanner scanner, Party party) {
        // Reset battle state for all heroes at the start of each battle
        for (Hero hero : party.getHeroes()) {
            hero.resetBattleState();
        }
        
        List<Monster> enemies = spawnMonsters(party);
        System.out.println(ConsoleColors.RED + "\n*** Battle Started! Enemies approaching: ***" + ConsoleColors.RESET);
        for (Monster m : enemies) System.out.println("- " + m);

        int round = 1;
        boolean battleActive = true;

        while (battleActive) {
            System.out.println("\n" + ConsoleColors.YELLOW + "=== Round " + round + " ===" + ConsoleColors.RESET);

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
                System.out.println(ConsoleColors.RED + "The party has been defeated!" + ConsoleColors.RESET);
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

            Monster monster = GameDataLoader.createMonsterFromTemplate(template, targetLevel);
            enemies.add(monster);
        }
        return enemies;
    }

    private boolean processHeroesTurn(Scanner scanner, Party party, List<Monster> enemies) {
        for (Hero hero : party.getHeroes()) {
            if (hero.isFainted()) continue;
            if (enemies.stream().allMatch(Monster::isFainted)) break;

            System.out.println("\nIt is " + ConsoleColors.PURPLE + hero.getName() + ConsoleColors.RESET + "'s turn.");
            displayHeroInfo(hero);

            boolean actionTaken = false;
            while (!actionTaken) {
                System.out.println("1. Attack");
                System.out.println("2. Cast Spell");
                System.out.println("3. Use Potion");
                System.out.println("4. Equip Gear");
                System.out.println("5. Info");
                System.out.println("6. Quit Game");

                int choice = InputValidator.getValidInt(scanner, ConsoleColors.CYAN + "Action: " + ConsoleColors.RESET, 1, 6);
                switch (choice) {
                    case 1: actionTaken = performAttack(scanner, hero, enemies); break;
                    case 2: actionTaken = performSpell(scanner, hero, enemies); break;
                    case 3: actionTaken = performPotion(scanner, hero); break;
                    case 4: 
                        boolean equipmentChanged = performEquip(scanner, hero); 
                        // Only refresh hero display if equipment actually changed
                        if (equipmentChanged) {
                            System.out.println("\nIt is " + ConsoleColors.PURPLE + hero.getName() + ConsoleColors.RESET + "'s turn.");
                            displayHeroInfo(hero);
                        }
                        break;
                    case 5: showBattleInfo(party, enemies, hero); break;
                    case 6:
                        if (promptRestart(scanner)) {
                            System.out.println(ConsoleColors.GREEN + "Returning to main menu..." + ConsoleColors.RESET);
                            common.GameRunner.run();
                        } else {
                            System.out.println(ConsoleColors.CYAN + "Goodbye!" + ConsoleColors.RESET);
                            System.exit(0);
                        }
                        return false;
                }
            }
        }
        return true;
    }

    private boolean performAttack(Scanner scanner, Hero hero, List<Monster> enemies) {
        Monster target = selectMonster(scanner, enemies);
        if (target == null) return false;

        double monsterDodge = Math.min(0.20, target.getDodgeChance());

        if (rng.nextDouble() < monsterDodge) {
            System.out.println(target.getName() + " dodged the attack!");
            return true;
        }

        double weaponDmg = (hero.getEquippedWeapon() != null) ? hero.getEquippedWeapon().getDamage() : 0;
        double rawDamage = (hero.getStrength() + weaponDmg) * 0.05;

        double actualDamage = Math.max(0, rawDamage - (target.getDefense() * 0.05));

        target.setHp(target.getHp() - actualDamage);
        System.out.printf("%s attacks %s for " + ConsoleColors.RED + "%.0f damage!" + ConsoleColors.RESET + "\n", hero.getName(), target.getName(), actualDamage);

        if (target.isFainted()) System.out.println(ConsoleColors.GREEN + target.getName() + " has been defeated!" + ConsoleColors.RESET);

        return true;
    }

    private boolean performSpell(Scanner scanner, Hero hero, List<Monster> enemies) {
        List<Spell> spells = hero.getInventory().getSpells();
        if (spells.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "You have no spells!" + ConsoleColors.RESET);
            System.out.println();
            displayHeroInfo(hero);
            return false;
        }

        System.out.println(ConsoleColors.WHITE_BOLD + "--- Spellbook ---" + ConsoleColors.RESET);
        for (int i = 0; i < spells.size(); i++) {
            System.out.println((i + 1) + ". " + spells.get(i));
        }
        System.out.println((spells.size() + 1) + ". Cancel");

        int choice = InputValidator.getValidInt(scanner, ConsoleColors.CYAN + "Select Spell: " + ConsoleColors.RESET, 1, spells.size() + 1);
        if (choice == spells.size() + 1) return false;

        Spell spell = spells.get(choice - 1);
        if (hero.getMana() < spell.getManaCost()) {
            System.out.println(ConsoleColors.RED + "Not enough Mana!" + ConsoleColors.RESET);
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

        System.out.printf("%s casts %s on %s for " + ConsoleColors.RED + "%.0f damage!" + ConsoleColors.RESET + "\n", hero.getName(), spell.getName(), target.getName(), damage);
        hero.getInventory().removeItem(spell);
        return true;
    }

    private boolean performPotion(Scanner scanner, Hero hero) {
        List<Potion> potions = hero.getInventory().getPotions();
        if (potions.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "No potions in inventory." + ConsoleColors.RESET);
            System.out.println();
            displayHeroInfo(hero);
            return false;
        }

        System.out.println(ConsoleColors.WHITE_BOLD + "--- Potions ---" + ConsoleColors.RESET);
        for(int i=0; i<potions.size(); i++) System.out.println((i+1) + ". " + potions.get(i));
        System.out.println((potions.size() + 1) + ". " + ConsoleColors.YELLOW + "Back" + ConsoleColors.RESET);

        int choice = InputValidator.getValidInt(scanner, ConsoleColors.CYAN + "Use Potion: " + ConsoleColors.RESET, 1, potions.size() + 1);
        if (choice > potions.size()) {
            return false; // Back option selected
        }
        Potion potion = potions.get(choice - 1);

        double val = potion.getAttributeIncrease();
        StringBuilder boostMessage = new StringBuilder();
        
        if (potion.affects("Health")) {
            hero.setHp(hero.getHp() + val); // setHp automatically caps at max
        }
        if (potion.affects("Mana")) {
            hero.setMana(hero.getMana() + val); // setMana automatically caps at max
        }
        if (potion.affects("Strength")) {
            hero.addStrengthBoost(val);
            boostMessage.append(" ").append(ConsoleColors.RED).append("Strength Boost!!").append(ConsoleColors.RESET);
        }
        if (potion.affects("Dexterity")) {
            hero.addDexterityBoost(val);
            boostMessage.append(" ").append(ConsoleColors.PURPLE).append("Dexterity Boost!!").append(ConsoleColors.RESET);
        }
        if (potion.affects("Agility")) {
            hero.addAgilityBoost(val);
            boostMessage.append(" ").append(ConsoleColors.CYAN).append("Agility Boost!!").append(ConsoleColors.RESET);
        }

        System.out.println(ConsoleColors.GREEN + hero.getName() + " used " + potion.getName() + "!" + ConsoleColors.RESET + boostMessage.toString());
        hero.getInventory().removeItem(potion);
        
        // Redisplay hero info after potion use
        System.out.println();
        displayHeroInfo(hero);
        
        return false; // Potion use doesn't end turn - can still take another action
    }

    private boolean performEquip(Scanner scanner, Hero hero) {
        boolean equipmentChanged = false;
        System.out.println("1. Weapons");
        System.out.println("2. Armor");
        int type = InputValidator.getValidInt(scanner, ConsoleColors.CYAN + "Type: " + ConsoleColors.RESET, 1, 2);

        if (type == 1) {
            List<Weapon> weps = hero.getInventory().getWeapons();
            if (weps.isEmpty() && hero.getEquippedWeapon() == null) { 
                System.out.println(ConsoleColors.YELLOW + "No weapons." + ConsoleColors.RESET); 
                return false; 
            }
            
            System.out.println(ConsoleColors.WHITE_BOLD + "Weapons:" + ConsoleColors.RESET);
            int optionNum = 1;
            for(int i=0; i<weps.size(); i++) {
                String equippedTag = "";
                // Use object reference equality instead of name equality to avoid duplicates
                if (hero.getEquippedWeapon() == weps.get(i)) {
                    equippedTag = ConsoleColors.GREEN + " [equipped]" + ConsoleColors.RESET;
                }
                System.out.println(optionNum + ". " + weps.get(i).getName() + " (Dmg: " + (int)weps.get(i).getDamage() + ")" + equippedTag);
                optionNum++;
            }
            if (hero.getEquippedWeapon() != null) {
                System.out.println(optionNum + ". " + ConsoleColors.RED + "Unequip " + hero.getEquippedWeapon().getName() + ConsoleColors.RESET);
                optionNum++;
            }
            System.out.println(optionNum + ". " + ConsoleColors.YELLOW + "Back" + ConsoleColors.RESET);
            
            int sel = InputValidator.getValidInt(scanner, "Choose: ", 1, optionNum);
            if (sel <= weps.size()) {
                Weapon selectedWeapon = weps.get(sel-1);
                // Use object reference equality instead of name equality
                boolean alreadyEquipped = hero.getEquippedWeapon() == selectedWeapon;
                if (alreadyEquipped) {
                    System.out.println(ConsoleColors.YELLOW + selectedWeapon.getName() + " is already equipped." + ConsoleColors.RESET);
                    System.out.println();
                    displayHeroInfo(hero);
                } else {
                    hero.equipWeapon(selectedWeapon);
                    System.out.println(ConsoleColors.GREEN + selectedWeapon.getName() + " equipped!" + ConsoleColors.RESET);
                    equipmentChanged = true;
                }
            } else if (hero.getEquippedWeapon() != null && sel == weps.size() + 1) {
                // This is the unequip option (comes right after the weapon list)
                String weaponName = hero.getEquippedWeapon().getName();
                hero.unequipWeapon();
                System.out.println(ConsoleColors.YELLOW + weaponName + " unequipped!" + ConsoleColors.RESET);
                equipmentChanged = true;
            }
            // If sel == optionNum and no weapon equipped, or if it's the back option, just return
        } else {
            List<Armor> arms = hero.getInventory().getArmor();
            if (arms.isEmpty() && hero.getEquippedArmor() == null) { 
                System.out.println(ConsoleColors.YELLOW + "No armor." + ConsoleColors.RESET); 
                System.out.println();
                displayHeroInfo(hero);
                return false; 
            }
            
            System.out.println(ConsoleColors.WHITE_BOLD + "Armor:" + ConsoleColors.RESET);
            int optionNum = 1;
            for(int i=0; i<arms.size(); i++) {
                String equippedTag = "";
                // Use object reference equality instead of name equality to avoid duplicates
                if (hero.getEquippedArmor() == arms.get(i)) {
                    equippedTag = ConsoleColors.GREEN + " [equipped]" + ConsoleColors.RESET;
                }
                System.out.println(optionNum + ". " + arms.get(i).getName() + " (Def: " + (int)arms.get(i).getDamageReduction() + ")" + equippedTag);
                optionNum++;
            }
            if (hero.getEquippedArmor() != null) {
                System.out.println(optionNum + ". " + ConsoleColors.BLUE + "Unequip " + hero.getEquippedArmor().getName() + ConsoleColors.RESET);
                optionNum++;
            }
            System.out.println(optionNum + ". " + ConsoleColors.YELLOW + "Back" + ConsoleColors.RESET);
            
            int sel = InputValidator.getValidInt(scanner, "Choose: ", 1, optionNum);
            if (sel <= arms.size()) {
                Armor selectedArmor = arms.get(sel-1);
                // Use object reference equality instead of name equality
                boolean alreadyEquipped = hero.getEquippedArmor() == selectedArmor;
                if (alreadyEquipped) {
                    System.out.println(ConsoleColors.YELLOW + selectedArmor.getName() + " is already equipped." + ConsoleColors.RESET);
                    System.out.println();
                    displayHeroInfo(hero);
                } else {
                    hero.equipArmor(selectedArmor);
                    System.out.println(ConsoleColors.GREEN + selectedArmor.getName() + " equipped!" + ConsoleColors.RESET);
                    equipmentChanged = true;
                }
            } else if (hero.getEquippedArmor() != null && sel == arms.size() + 1) {
                // This is the unequip option (comes right after the armor list)
                String armorName = hero.getEquippedArmor().getName();
                hero.unequipArmor();
                System.out.println(ConsoleColors.YELLOW + armorName + " unequipped!" + ConsoleColors.RESET);
                equipmentChanged = true;
            }
            // If sel == optionNum and no armor equipped, or if it's the back option, just return
        }
        
        return equipmentChanged;
    }

    private void processMonstersTurn(Party party, List<Monster> enemies) {
        for (Monster monster : enemies) {
            if (monster.isFainted()) continue;

            List<Hero> aliveHeroes = party.getHeroes().stream()
                    .filter(h -> !h.isFainted())
                    .collect(Collectors.toList());

            if (aliveHeroes.isEmpty()) break;

            Hero target = aliveHeroes.get(rng.nextInt(aliveHeroes.size()));

            //  Hero Dodge Cap increased to 70% 
            double heroDodgeChance = target.getAgility() / (target.getAgility() + 1000.0);
            heroDodgeChance = Math.min(0.70, heroDodgeChance);

            if (rng.nextDouble() < heroDodgeChance) {
                System.out.println(target.getName() + " dodged " + monster.getName() + "'s attack!");
                continue;
            }

            double rawDmg = monster.getBaseDamage();
            double mitigation = (target.getEquippedArmor() != null) ? target.getEquippedArmor().getDamageReduction() : 0;
            double finalDmg = Math.max(0, rawDmg - (mitigation * 0.2));

            // Silently degrade equipped armor when attacked (even if damage is 0)
            if (target.getEquippedArmor() != null) {
                target.getEquippedArmor().degrade();
            }

            target.setHp(target.getHp() - finalDmg);
            System.out.printf("%s attacks %s for " + ConsoleColors.RED + "%.0f damage!" + ConsoleColors.RESET + "\n", monster.getName(), target.getName(), finalDmg);

            if (target.isFainted()) {
                System.out.println(ConsoleColors.RED + target.getName() + " has fainted!" + ConsoleColors.RESET);
                target.markFaintedInBattle();
            }
        }
    }

    private void performRegeneration(Party party) {
        System.out.println(ConsoleColors.GREEN + "\n=== End of Round Regeneration ===" + ConsoleColors.RESET);
        
        boolean anyRegeneration = false;
        for (Hero h : party.getHeroes()) {
            if (!h.isFainted()) {
                double oldHp = h.getHp();
                double oldMana = h.getMana();
                
                h.setHp(h.getHp() * 1.1);
                h.setMana(h.getMana() * 1.1);
                
                double hpGain = h.getHp() - oldHp;
                double manaGain = h.getMana() - oldMana;
                
                System.out.printf(ConsoleColors.CYAN + "%s" + ConsoleColors.RESET + " regains " + 
                        ConsoleColors.RED + "%.1f HP" + ConsoleColors.RESET + " and " + 
                        ConsoleColors.BLUE + "%.1f MP" + ConsoleColors.RESET + "\n",
                        h.getName(), hpGain, manaGain);
                anyRegeneration = true;
                
                // Degrade equipped weapon durability
                if (h.getEquippedWeapon() != null) {
                    h.getEquippedWeapon().degrade();
                    if (h.getEquippedWeapon().isBroken()) {
                        System.out.println(ConsoleColors.RED + h.getName() + "'s " + h.getEquippedWeapon().getName() + " has broken!" + ConsoleColors.RESET);
                        h.unequipWeapon(); // Auto-unequip broken weapon
                    }
                }
                
                // Check for broken armor at end of round
                if (h.getEquippedArmor() != null && h.getEquippedArmor().isBroken()) {
                    System.out.println(ConsoleColors.RED + h.getName() + "'s " + h.getEquippedArmor().getName() + " has broken!" + ConsoleColors.RESET);
                    h.unequipArmor(); // Auto-unequip broken armor
                }
            }
        }
        
        if (!anyRegeneration) {
            System.out.println(ConsoleColors.YELLOW + "No heroes available for regeneration." + ConsoleColors.RESET);
        }
        System.out.println();
    }

    private void processVictory(Party party, List<Monster> enemies) {
        System.out.println(ConsoleColors.GREEN + "\n*** VICTORY! ***" + ConsoleColors.RESET);
        
        // Calculate per-hero rewards based on total enemy levels
        double totalLevels = enemies.stream().mapToDouble(Monster::getLevel).sum();
        double goldPerHero = totalLevels * 100;
        // More generous XP: 2 XP per enemy level (so level 1 monster = 2 XP, level 2 = 4 XP, etc.)
        int xpPerHero = (int)(totalLevels * 2);

        // First, revive all fainted heroes
        for (Hero h : party.getHeroes()) {
            if (h.isFainted()) {
                System.out.println(h.getName() + " is revived.");
                h.revive();
            }
        }
        
        // Then, give rewards and display individual results
        System.out.println("\n" + ConsoleColors.YELLOW + "Battle Rewards:" + ConsoleColors.RESET);
        for (Hero h : party.getHeroes()) {
            if (!h.wasFaintedInBattle()) {
                h.addMoney(goldPerHero);
                h.gainExperience(xpPerHero);
                System.out.printf("%s gains " + ConsoleColors.YELLOW + "%.0f gold" + ConsoleColors.RESET + " and " + ConsoleColors.CYAN + "%d XP" + ConsoleColors.RESET + "\n", 
                    h.getName(), goldPerHero, xpPerHero);
            } else {
                System.out.printf("%s receives " + ConsoleColors.RED + "no rewards" + ConsoleColors.RESET + " (was fainted during battle)\n", 
                    h.getName());
            }
        }
    }

    private Monster selectMonster(Scanner scanner, List<Monster> enemies) {
        List<Monster> alive = enemies.stream().filter(m -> !m.isFainted()).collect(Collectors.toList());
        if (alive.isEmpty()) return null;

        System.out.println(ConsoleColors.CYAN + "Select Target:" + ConsoleColors.RESET);
        for(int i=0; i<alive.size(); i++) {
            System.out.println((i+1) + ". " + alive.get(i));
        }
        System.out.println((alive.size() + 1) + ". " + ConsoleColors.YELLOW + "Back" + ConsoleColors.RESET);
        
        int choice = InputValidator.getValidInt(scanner, ConsoleColors.CYAN + "Target: " + ConsoleColors.RESET, 1, alive.size() + 1);
        if (choice <= alive.size()) {
            return alive.get(choice - 1);
        } else {
            return null; // Back option selected
        }
    }

    private void showBattleInfo(Party party, List<Monster> enemies, Hero currentHero) {
        System.out.println("\n" + ConsoleColors.WHITE_BOLD + "--- Battle Status ---" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "HEROES:" + ConsoleColors.RESET);
        party.getHeroes().forEach(System.out::println);
        System.out.println();
        System.out.println(ConsoleColors.RED + "MONSTERS:" + ConsoleColors.RESET);
        enemies.forEach(System.out::println);
        System.out.println("---------------------");
        
        // Redisplay current hero info without turn announcement
        System.out.println();
        displayHeroInfo(currentHero);
    }
    
    private boolean promptRestart(Scanner scanner) {
        String input = common.InputValidator.getValidOption(scanner, 
            "\n" + utils.ConsoleColors.YELLOW + "Do you want to play again? (yes/no): " + utils.ConsoleColors.RESET, 
            "y", "yes", "n", "no");
        return input.equals("y") || input.equals("yes");
    }
    
    /**
     * Helper method to display hero information with HP, MP, and equipped items.
     * Shows boosted stats with highlighting when active.
     */
    private void displayHeroInfo(Hero hero) {
        System.out.printf("%s [%s] | HP: " + ConsoleColors.GREEN + "%.0f" + ConsoleColors.RESET + " | MP: " + ConsoleColors.BLUE + "%.0f" + ConsoleColors.RESET + "\n", 
            hero.getName(), hero.getType(), hero.getHp(), hero.getMana());
        
        // Show boosted stats if any are active
        if (hero.hasStrengthBoost() || hero.hasDexterityBoost() || hero.hasAgilityBoost()) {
            System.out.print("Boosts: ");
            boolean first = true;
            
            if (hero.hasStrengthBoost()) {
                if (!first) System.out.print(" | ");
                System.out.printf("Str: %.0f->" + ConsoleColors.RED + "%.0f" + ConsoleColors.RESET, 
                    hero.getBaseStrength(), hero.getStrength());
                first = false;
            }
            if (hero.hasDexterityBoost()) {
                if (!first) System.out.print(" | ");
                System.out.printf("Dex: %.0f->" + ConsoleColors.PURPLE + "%.0f" + ConsoleColors.RESET, 
                    hero.getBaseDexterity(), hero.getDexterity());
                first = false;
            }
            if (hero.hasAgilityBoost()) {
                if (!first) System.out.print(" | ");
                System.out.printf("Agi: %.0f->" + ConsoleColors.CYAN + "%.0f" + ConsoleColors.RESET, 
                    hero.getBaseAgility(), hero.getAgility());
            }
            System.out.println();
        }
        
        // Show equipped items if any
        if (hero.getEquippedWeapon() != null || hero.getEquippedArmor() != null) {
            System.out.print("Equipped: ");
            if (hero.getEquippedWeapon() != null) {
                System.out.print(ConsoleColors.RED + hero.getEquippedWeapon().getName() + ConsoleColors.RESET);
                if (hero.getEquippedArmor() != null) System.out.print(" | ");
            }
            if (hero.getEquippedArmor() != null) {
                System.out.print(ConsoleColors.BLUE + hero.getEquippedArmor().getName() + ConsoleColors.RESET);
            }
            System.out.println();
        }
    }
}
