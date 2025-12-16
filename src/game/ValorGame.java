package game;

import board.Cell;
import board.CellType;
import board.ValorBoard;
import utils.ConsoleColors;
import common.InputValidator;
import entities.Hero;
import entities.Monster;
import entities.Party;
import items.Potion;
import items.Spell;
import items.Spell.SpellType;
import utils.GameDataLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * The main game engine for "Legends of Valor".
 * Rules:
 * - 8x8 Grid, 3 Lanes.
 * - Heroes win by reaching Row 0 (Monster Nexus).
 * - Monsters win by reaching Row 7 (Hero Nexus).
 * - Heroes spawn at Row 7; Monsters spawn at Row 0.
 * - A round consists of Hero Turns -> Monster Turns -> Regeneration.
 */
public class ValorGame extends Game {

    private ValorBoard board;
    private Party party;
    private List<Monster> activeMonsters;
    private List<Monster> monsterCatalog;
    private MarketController marketController;

    private int roundCount;
    private boolean quitGame;

    @Override
    protected void initializeGame(Scanner scanner) {
        System.out.println(ConsoleColors.CYAN + "Initializing Legends of Valor..." + ConsoleColors.RESET);

        // 1. Load Assets
        this.monsterCatalog = new ArrayList<>();
        this.monsterCatalog.addAll(GameDataLoader.loadMonsters("Dragons.txt", Monster.MonsterType.DRAGON));
        this.monsterCatalog.addAll(GameDataLoader.loadMonsters("Exoskeletons.txt", Monster.MonsterType.EXOSKELETON));
        this.monsterCatalog.addAll(GameDataLoader.loadMonsters("Spirits.txt", Monster.MonsterType.SPIRIT));

        // 2. Setup Board
        this.board = new ValorBoard();
        this.activeMonsters = new ArrayList<>();
        this.roundCount = 1;
        this.quitGame = false;
        this.marketController = new MarketController();

        // 3. Setup Party
        setupParty(scanner);

        // 4. Initial Spawn
        spawnHeroes();
        spawnMonsters();

        System.out.println(ConsoleColors.GREEN + "\nThe battle for the Nexus begins!" + ConsoleColors.RESET);
    }

    private void setupParty(Scanner scanner) {
        this.party = new Party();

        // Load heroes by class
        List<Hero> availableWarriors = GameDataLoader.loadHeroes("Warriors.txt", Hero.HeroType.WARRIOR);
        List<Hero> availableSorcerers = GameDataLoader.loadHeroes("Sorcerers.txt", Hero.HeroType.SORCERER);
        List<Hero> availablePaladins = GameDataLoader.loadHeroes("Paladins.txt", Hero.HeroType.PALADIN);

        System.out.println("\n" + ConsoleColors.YELLOW + "=== RECRUIT YOUR TEAM ===" + ConsoleColors.RESET);
        System.out.println("You must select 3 Heroes to defend the Nexus.");

        while (party.getHeroes().size() < 3) {
            System.out.println("\n" + ConsoleColors.WHITE_BOLD + "Party Size: " + party.getHeroes().size() + "/3" + ConsoleColors.RESET);
            
            System.out.println("\nSelect Hero #" + (party.getHeroes().size() + 1) + ":");
            Hero selectedHero = selectHeroByClass(scanner, availableWarriors, availableSorcerers, availablePaladins);
            if (selectedHero == null) {
                return; // User quit
            }

            // Assign a unique lane to each hero as they are picked (0, 1, or 2)
            selectedHero.setLane(party.getHeroes().size());
            party.addHero(selectedHero);

            System.out.println(ConsoleColors.GREEN + selectedHero.getName() + " joined the party!" + ConsoleColors.RESET);
        }
    }

    private Hero selectHeroByClass(Scanner scanner, List<Hero> warriors, List<Hero> sorcerers, List<Hero> paladins) {
        System.out.println("1. " + ConsoleColors.RED + "Warrior" + ConsoleColors.RESET + " (Favors Strength/Agility)");
        System.out.println("2. " + ConsoleColors.BLUE + "Sorcerer" + ConsoleColors.RESET + " (Favors Dexterity/Agility)");
        System.out.println("3. " + ConsoleColors.GREEN + "Paladin" + ConsoleColors.RESET + " (Favors Strength/Dexterity)");

        int typeChoice = InputValidator.getValidInt(scanner, "Choose class: ", 1, 3);
        List<Hero> choiceList = (typeChoice == 1) ? warriors :
                (typeChoice == 2) ? sorcerers : paladins;

        if (choiceList.isEmpty()) {
            System.out.println(ConsoleColors.RED + "No heroes available for that class!" + ConsoleColors.RESET);
            return selectHeroByClass(scanner, warriors, sorcerers, paladins);
        }

        System.out.println("\n" + ConsoleColors.WHITE_BOLD + "Available Heroes:" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+------+------+------+------+------+" + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-2s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-3s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                "ID", "NAME", "LVL", "HP", "MP", "STR", "DEX", "AGI");
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+------+------+------+------+------+" + ConsoleColors.RESET);

        for (int i = 0; i < choiceList.size(); i++) {
            Hero h = choiceList.get(i);
            System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-2d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-3d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4.0f " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4.0f " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4.0f " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4.0f " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-4.0f " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                    (i + 1), h.getName(), h.getLevel(), h.getHp(), h.getMana(), h.getStrength(), h.getDexterity(), h.getAgility());
        }
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+------+------+------+------+------+" + ConsoleColors.RESET);
        System.out.println((choiceList.size() + 1) + ". " + ConsoleColors.YELLOW + "Back to Class Selection" + ConsoleColors.RESET);

        int heroChoice = InputValidator.getValidInt(scanner, "Select hero ID: ", 1, choiceList.size() + 1);
        if (heroChoice == choiceList.size() + 1) {
            return null; // Go back to class selection
        }
        return choiceList.remove(heroChoice - 1);
    }

    private void spawnHeroes() {
        List<Hero> heroes = party.getHeroes();
        int[] laneSpawns = {0, 3, 6}; // Left side of Top, Mid, Bot lanes

        for (int i = 0; i < heroes.size(); i++) {
            if (i >= 3) break;
            Hero h = heroes.get(i);
            int r = 7;
            int c = laneSpawns[i];

            h.setPosition(r, c);
            h.setLane(i); // Ensure lane ID matches column
            board.getCell(r, c).setHero(h);
        }
    }

    private void spawnMonsters() {
        int highestHeroLvl = party.getHeroes().stream().mapToInt(Hero::getLevel).max().orElse(1);
        int[] laneSpawns = {1, 4, 7}; // Right side of Top, Mid, Bot lanes

        System.out.println(ConsoleColors.RED + "*** Reinforcements! New Monsters have entered the Nexus! ***" + ConsoleColors.RESET);

        for (int i = 0; i < 3; i++) {
            Cell spawnCell = board.getCell(0, laneSpawns[i]);
            if (spawnCell.hasMonster()) {
                System.out.println(ConsoleColors.YELLOW + "Lane " + (i + 1) + " spawn blocked!" + ConsoleColors.RESET);
                continue;
            }

            Monster template = monsterCatalog.get((int) (Math.random() * monsterCatalog.size()));
            Monster m = GameDataLoader.createMonsterFromTemplate(template, highestHeroLvl);

            m.setPosition(0, laneSpawns[i]);
            m.setLane(i);

            spawnCell.setMonster(m);
            activeMonsters.add(m);
        }
    }

    @Override
    protected void processTurn(Scanner scanner) {
        System.out.println("\n" + ConsoleColors.YELLOW + "=== ROUND " + roundCount + " ===" + ConsoleColors.RESET);
        board.printBoard();

        // 1. HEROES TURN
        for (Hero hero : party.getHeroes()) {
            if (hero.isFainted()) {
                System.out.println(ConsoleColors.RED + hero.getName() + " is fainted (respawns at Nexus next round)." + ConsoleColors.RESET);
                continue;
            }

            System.out.println("\nTurn: " + ConsoleColors.CYAN + hero.getName() + " [H" + (hero.getLane() + 1) + "]" + ConsoleColors.RESET + " (Lane " + hero.getLane() + ")");
            boolean actionTaken = false;

            while (!actionTaken && !quitGame) {
                printControls();

                String choice = InputValidator.getValidOption(scanner, "Action: ", "w", "a", "c", "t", "r", "m", "p", "e", "i", "q");

                switch (choice) {
                    case "w": actionTaken = handleMove(scanner, hero); break;
                    case "a": actionTaken = handleAttack(scanner, hero); break;
                    case "c": actionTaken = handleCastSpell(scanner, hero); break;
                    case "t": actionTaken = handleTeleport(scanner, hero); break;
                    case "r": actionTaken = handleRecall(hero); break;
                    case "m": actionTaken = handleMarket(scanner, hero); break;
                    case "p": actionTaken = handlePotion(scanner, hero); break;
                    case "e": actionTaken = handleEquip(scanner, hero); break;
                    case "i": showDetailedHeroInfo(hero); break;
                    case "q": quitGame = true; return;
                }
            }
            if (quitGame) return;
            board.printBoard();
        }

        // 2. MONSTERS TURN
        processMonstersTurn();

        // 3. END ROUND / REGEN
        performRegeneration();

        if (roundCount % 8 == 0) spawnMonsters();

        roundCount++;
    }

    private void printControls() {
        System.out.print("CONTROLS: ");
        System.out.print("[" + ConsoleColors.YELLOW + "W" + ConsoleColors.RESET + "]Move ");
        System.out.print("[" + ConsoleColors.YELLOW + "A" + ConsoleColors.RESET + "]ttack ");
        System.out.print("[" + ConsoleColors.YELLOW + "C" + ConsoleColors.RESET + "]ast ");
        System.out.print("[" + ConsoleColors.YELLOW + "T" + ConsoleColors.RESET + "]eleport ");
        System.out.print("[" + ConsoleColors.YELLOW + "R" + ConsoleColors.RESET + "]ecall ");
        System.out.print("[" + ConsoleColors.YELLOW + "M" + ConsoleColors.RESET + "]arket ");
        System.out.print("[" + ConsoleColors.YELLOW + "P" + ConsoleColors.RESET + "]otion ");
        System.out.print("[" + ConsoleColors.YELLOW + "E" + ConsoleColors.RESET + "]quip ");
        System.out.print("[" + ConsoleColors.YELLOW + "I" + ConsoleColors.RESET + "]nfo ");
        System.out.println("[" + ConsoleColors.YELLOW + "Q" + ConsoleColors.RESET + "]uit");
        System.out.println(ConsoleColors.CYAN + "----------------------------------------------------------------" + ConsoleColors.RESET);
    }

    // HERO ACTIONS

    private boolean handleMove(Scanner scanner, Hero hero) {
        System.out.println("Move: [W]Up [A]Left [S]Down [D]Right");
        String dir = InputValidator.getValidOption(scanner, "Dir: ", "w", "a", "s", "d");
        int dR = 0, dC = 0;

        switch (dir) {
            case "w": dR = -1; break; // Up
            case "s": dR = 1; break; // Down
            case "a": dC = -1; break; // Left
            case "d": dC = 1; break; // Right
        }

        int newR = hero.getRow() + dR;
        int newC = hero.getCol() + dC;

        if (!board.isValidCoordinate(newR, newC)) {
            System.out.println(ConsoleColors.RED + "Blocked: Cannot move off the board." + ConsoleColors.RESET);
            return false;
        }

        // No Passing Logic (Zone of Control)
        // If moving North (forward), check if any monster is in this lane at current row or North of it
        if (dR < 0) { // Moving UP
            for (Monster m : activeMonsters) {
                if (m.getLane() == hero.getLane()) {
                    // If monster is 'ahead' or on same row, you cannot bypass it
                    // 'Ahead' means closer to Row 0.
                    if (m.getRow() <= hero.getRow()) {
                        if (newR < m.getRow()) {
                            System.out.println(ConsoleColors.RED + "Blocked: You cannot move behind " + m.getName() + "!" + ConsoleColors.RESET);
                            return false;
                        }
                    }
                }
            }
        }

        Cell target = board.getCell(newR, newC);

        // Obstacles
        if (target.getType() == CellType.OBSTACLE) {
            System.out.println(ConsoleColors.YELLOW + "An OBSTACLE blocks your path." + ConsoleColors.RESET);
            String choice = InputValidator.getValidOption(scanner, "Do you want to destroy it? (y/n): ", "y", "n");

            if (choice.equals("y")) {
                target.setType(CellType.COMMON); // Convert to plain cell
                System.out.println(ConsoleColors.GREEN + "You destroyed the obstacle! (Turn Used)" + ConsoleColors.RESET);
                return true; // Turn consumed, but hero doesn't move yet
            } else {
                return false; // Action cancelled
            }
        }

        // 1. Terrain Check
        if (!target.isAccessible()) {
            System.out.println(ConsoleColors.RED + "Blocked: Inaccessible terrain." + ConsoleColors.RESET);
            return false;
        }

        // 2. Occupancy Check
        if (target.hasHero()) {
            System.out.println(ConsoleColors.RED + "Blocked: Another hero is standing there." + ConsoleColors.RESET);
            return false;
        }
        if (target.hasMonster()) {
            System.out.println(ConsoleColors.RED + "Blocked: You cannot walk through a monster!" + ConsoleColors.RESET);
            return false;
        }

        // EXECUTE MOVE
        board.getCell(hero.getRow(), hero.getCol()).removeHero();
        hero.setPosition(newR, newC);
        target.setHero(hero);

        System.out.println(hero.getName() + " moved to (" + newR + "," + newC + ")");
        applyTerrainBonus(hero, target);

        return true;
    }

    private void applyTerrainBonus(Hero hero, Cell cell) {
        switch (cell.getType()) {
            case BUSH:
                System.out.println(ConsoleColors.GREEN + "Terrain: Bush increases Dexterity!" + ConsoleColors.RESET);
                break;
            case CAVE:
                System.out.println(ConsoleColors.YELLOW + "Terrain: Cave increases Agility!" + ConsoleColors.RESET);
                break;
            case KOULOU:
                System.out.println(ConsoleColors.BLUE + "Terrain: Koulou increases Strength!" + ConsoleColors.RESET);
                break;
            default: break;
        }
    }

    private boolean handleAttack(Scanner scanner, Hero hero) {
        List<Monster> targets = new ArrayList<>();
        // Check 3x3 grid around hero
        for (int r = hero.getRow() - 1; r <= hero.getRow() + 1; r++) {
            for (int c = hero.getCol() - 1; c <= hero.getCol() + 1; c++) {
                if (board.isValidCoordinate(r, c) && board.getCell(r, c).hasMonster()) {
                    targets.add(board.getCell(r, c).getMonster());
                }
            }
        }

        if (targets.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "No monsters in range." + ConsoleColors.RESET);
            return false;
        }

        System.out.println("Select Target:");
        for (int i = 0; i < targets.size(); i++) {
            System.out.println((i + 1) + ". " + targets.get(i));
        }

        int idx = InputValidator.getValidInt(scanner, "Target: ", 1, targets.size()) - 1;
        Monster target = targets.get(idx);

        double rawDmg = hero.attack(target);

        if (Math.random() < target.getDodgeChance()) {
            System.out.println(target.getName() + " DODGED the attack!");
        } else {
            double actualDmg = Math.max(0, rawDmg - (target.getDefense() * 0.02));
            target.setHp(target.getHp() - actualDmg);
            System.out.println(hero.getName() + " dealt " + ConsoleColors.RED + String.format("%.0f", actualDmg) + ConsoleColors.RESET + " damage!");

            if (target.isFainted()) {
                System.out.println(ConsoleColors.GREEN + target.getName() + " was DEFEATED!" + ConsoleColors.RESET);
                board.getCell(target.getRow(), target.getCol()).removeMonster();
                activeMonsters.remove(target);

                double gold = 500 * target.getLevel();
                int xp = 2 * target.getLevel();
                hero.addMoney(gold);
                hero.gainExperience(xp);
                System.out.println("Gained " + gold + " gold and " + xp + " XP.");
            }
        }
        return true;
    }

    private boolean handleCastSpell(Scanner scanner, Hero hero) {
        List<Spell> spells = hero.getInventory().getSpells();
        if (spells.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "You have no spells!" + ConsoleColors.RESET);
            return false;
        }

        // Find targets in same range as attack (3x3 grid)
        List<Monster> targets = new ArrayList<>();
        for (int r = hero.getRow() - 1; r <= hero.getRow() + 1; r++) {
            for (int c = hero.getCol() - 1; c <= hero.getCol() + 1; c++) {
                if (board.isValidCoordinate(r, c) && board.getCell(r, c).hasMonster()) {
                    targets.add(board.getCell(r, c).getMonster());
                }
            }
        }

        if (targets.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "No monsters in range." + ConsoleColors.RESET);
            return false;
        }

        // Display spellbook
        System.out.println(ConsoleColors.PURPLE + "--- Spellbook ---" + ConsoleColors.RESET);
        for (int i = 0; i < spells.size(); i++) {
            System.out.println((i + 1) + ". " + spells.get(i));
        }
        System.out.println((spells.size() + 1) + ". Cancel");

        int spellChoice = InputValidator.getValidInt(scanner, ConsoleColors.CYAN + "Select Spell: " + ConsoleColors.RESET, 1, spells.size() + 1);
        if (spellChoice == spells.size() + 1) return false;

        Spell spell = spells.get(spellChoice - 1);
        if (hero.getMana() < spell.getManaCost()) {
            System.out.println(ConsoleColors.RED + "Not enough Mana! Cost: " + spell.getManaCost() + " | Current: " + hero.getMana() + ConsoleColors.RESET);
            return false;
        }

        // Select target
        System.out.println("Select Target:");
        for (int i = 0; i < targets.size(); i++) {
            System.out.println((i + 1) + ". " + targets.get(i));
        }

        int targetIdx = InputValidator.getValidInt(scanner, "Target: ", 1, targets.size()) - 1;
        Monster target = targets.get(targetIdx);

        // Deduct mana
        hero.setMana(hero.getMana() - spell.getManaCost());

        // Calculate damage with dexterity bonus
        double damage = spell.getDamage() + ((hero.getDexterity() / 10000.0) * spell.getDamage());
        
        // Check dodge
        if (Math.random() < target.getDodgeChance()) {
            System.out.println(target.getName() + " DODGED the spell!");
        } else {
            target.setHp(target.getHp() - damage);
            System.out.printf("%s casts %s on %s for " + ConsoleColors.RED + "%.0f damage!" + ConsoleColors.RESET + "\n", 
                    hero.getName(), spell.getName(), target.getName(), damage);

            // Apply spell effects if target survives
            if (!target.isFainted()) {
                if (spell.getType() == SpellType.ICE) {
                    target.reduceDamage(target.getBaseDamage() * 0.1);
                    System.out.println(ConsoleColors.CYAN + target.getName() + "'s damage reduced by Ice!" + ConsoleColors.RESET);
                } else if (spell.getType() == SpellType.FIRE) {
                    target.reduceDefense(target.getDefense() * 0.1);
                    System.out.println(ConsoleColors.RED + target.getName() + "'s defense melted by Fire!" + ConsoleColors.RESET);
                } else if (spell.getType() == SpellType.LIGHTNING) {
                    target.reduceDodgeChance(target.getDodgeChance() * 0.1);
                    System.out.println(ConsoleColors.YELLOW + target.getName() + "'s dodge reduced by Lightning!" + ConsoleColors.RESET);
                }
            }

            // Check if target defeated
            if (target.isFainted()) {
                System.out.println(ConsoleColors.GREEN + target.getName() + " was DEFEATED!" + ConsoleColors.RESET);
                board.getCell(target.getRow(), target.getCol()).removeMonster();
                activeMonsters.remove(target);

                double gold = 500 * target.getLevel();
                int xp = 2 * target.getLevel();
                hero.addMoney(gold);
                hero.gainExperience(xp);
                System.out.println("Gained " + gold + " gold and " + xp + " XP.");
            }
        }

        // Remove spell from inventory after use
        hero.getInventory().removeItem(spell);
        return true;
    }

    private boolean handleTeleport(Scanner scanner, Hero hero) {
        List<Hero> targets = new ArrayList<>();
        for (Hero h : party.getHeroes()) {
            if (h != hero && !h.isFainted() && h.getLane() != hero.getLane()) {
                targets.add(h);
            }
        }

        if (targets.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "No valid heroes to teleport to (must be in a different lane)." + ConsoleColors.RESET);
            return false;
        }

        System.out.println("Teleport to lane of:");
        for (int i = 0; i < targets.size(); i++) System.out.println((i + 1) + ". " + targets.get(i).getName());
        int idx = InputValidator.getValidInt(scanner, "Choice: ", 1, targets.size()) - 1;
        Hero destHero = targets.get(idx);

        int r = destHero.getRow();
        int c = destHero.getCol();
        int[][] spots = {{r, c - 1}, {r, c + 1}, {r + 1, c}};

        for (int[] s : spots) {
            if (board.isValidCoordinate(s[0], s[1])) {
                Cell cell = board.getCell(s[0], s[1]);
                if (cell.isAccessible() && !cell.hasHero() && !cell.hasMonster()) {
                    board.getCell(hero.getRow(), hero.getCol()).removeHero();
                    hero.setPosition(s[0], s[1]);
                    hero.setLane(destHero.getLane());
                    cell.setHero(hero);
                    System.out.println(ConsoleColors.PURPLE + "*WOOSH* " + hero.getName() + " teleported to " + destHero.getName() + "!" + ConsoleColors.RESET);
                    return true;
                }
            }
        }
        System.out.println(ConsoleColors.RED + "Teleport failed: No open space beside target." + ConsoleColors.RESET);
        return false;
    }

    private boolean handleRecall(Hero hero) {
        int r = 7;
        int c = (hero.getLane() == 0) ? 0 : (hero.getLane() == 1) ? 3 : 6;

        Cell spawn = board.getCell(r, c);
        if (spawn.hasHero() && spawn.getHero() != hero) {
            System.out.println(ConsoleColors.RED + "Recall failed: Your Nexus spawn is blocked." + ConsoleColors.RESET);
            return false;
        }

        board.getCell(hero.getRow(), hero.getCol()).removeHero();
        hero.setPosition(r, c);
        spawn.setHero(hero);
        System.out.println(ConsoleColors.CYAN + hero.getName() + " recalled to Nexus." + ConsoleColors.RESET);
        return true;
    }

    private boolean handleMarket(Scanner scanner, Hero hero) {
        Cell currentCell = board.getCell(hero.getRow(), hero.getCol());
        
        // Check if hero is in a Nexus cell (row 7 is Hero Nexus)
        if (currentCell.getType() != CellType.NEXUS) {
            System.out.println(ConsoleColors.RED + "Market unavailable: You must be in your Nexus to access the market!" + ConsoleColors.RESET);
            return false;
        }
        
        System.out.println(ConsoleColors.GREEN + hero.getName() + " enters the Nexus market..." + ConsoleColors.RESET);
        
        // Use the overloaded single-hero market method
        marketController.enterMarket(scanner, hero);
        
        // Redisplay the board and hero turn info after exiting market
        board.printBoard();
        System.out.println("\nTurn: " + ConsoleColors.CYAN + hero.getName() + " [H" + (hero.getLane() + 1) + "]" + ConsoleColors.RESET + " (Lane " + hero.getLane() + ")");
        
        // Market visit doesn't consume a turn
        return false;
    }

    private boolean handlePotion(Scanner scanner, Hero hero) {
        List<Potion> potions = hero.getInventory().getPotions();
        if (potions.isEmpty()) {
            System.out.println(ConsoleColors.RED + "No potions!\n" + ConsoleColors.RESET);
            return false;
        }
        System.out.println("Select Potion:");
        for (int i = 0; i < potions.size(); i++) System.out.println((i + 1) + ". " + potions.get(i).getName());
        int choice = InputValidator.getValidInt(scanner, "Use: ", 1, potions.size()) - 1;
        Potion p = potions.get(choice);

        p.apply(hero);

        hero.getInventory().removeItem(p);
        return true;
    }

    private boolean handleEquip(Scanner scanner, Hero hero) {
        System.out.println("1. Weapon\n2. Armor");
        int type = InputValidator.getValidInt(scanner, "Type: ", 1, 2);
        if (type == 1) {
            List<items.Weapon> weps = hero.getInventory().getWeapons();
            if (weps.isEmpty()) { System.out.println(ConsoleColors.RED + "No weapons.\n" + ConsoleColors.RESET); return false; }
            for (int i = 0; i < weps.size(); i++) System.out.println((i + 1) + ". " + weps.get(i).getName());
            int c = InputValidator.getValidInt(scanner, "Equip: ", 1, weps.size()) - 1;
            hero.equipWeapon(weps.get(c));
        } else {
            List<items.Armor> arms = hero.getInventory().getArmor();
            if (arms.isEmpty()) { System.out.println(ConsoleColors.RED + "No armor.\n" + ConsoleColors.RESET); return false; }
            for (int i = 0; i < arms.size(); i++) System.out.println((i + 1) + ". " + arms.get(i).getName());
            int c = InputValidator.getValidInt(scanner, "Equip: ", 1, arms.size()) - 1;
            hero.equipArmor(arms.get(c));
        }
        return true;
    }

    private void processMonstersTurn() {
        System.out.println(ConsoleColors.RED + "\n--- Monsters Turn ---" + ConsoleColors.RESET);
        Iterator<Monster> it = activeMonsters.iterator();
        while (it.hasNext()) {
            Monster m = it.next();
            if (m.isFainted()) {
                board.getCell(m.getRow(), m.getCol()).removeMonster();
                it.remove();
                continue;
            }

            int newR = m.getRow() + 1;
            if (newR < 8) {
                Cell t = board.getCell(newR, m.getCol());
                if (!t.hasMonster() && !t.hasHero() && t.isAccessible()) {
                    board.getCell(m.getRow(), m.getCol()).removeMonster();
                    m.setPosition(newR, m.getCol());
                    t.setMonster(m);
                    System.out.println(m.getName() + " moved South.");
                }
            }
        }
    }

    private void performRegeneration() {
        for (Hero h : party.getHeroes()) {
            if (!h.isFainted()) {
                h.setHp(h.getHp() * 1.1);
                h.setMana(h.getMana() * 1.1);
            } else {
                h.revive();
                handleRecall(h);
                System.out.println(ConsoleColors.GREEN + h.getName() + " has respawned at the Nexus!" + ConsoleColors.RESET);
            }
        }
    }

    private void printDashboard() {
        System.out.println(ConsoleColors.CYAN + "\n+------------------------------------------------------------+" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + ConsoleColors.WHITE_BOLD + "                        PARTY STATUS                        " + ConsoleColors.RESET + ConsoleColors.CYAN + "|" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "+----------------------+-------+--------+--------+-----------+" + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-5s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-6s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-6s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-9s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET, "NAME", "LVL", "HP", "MP", "GOLD");
        System.out.println(ConsoleColors.CYAN + "+----------------------+-------+--------+--------+-----------+" + ConsoleColors.RESET);

        for (Hero h : party.getHeroes()) {
            System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-5d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-6.0f " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-6.0f " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-9.0f " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                    h.getName(), h.getLevel(), h.getHp(), h.getMana(), h.getMoney());
        }
        System.out.println(ConsoleColors.CYAN + "+------------------------------------------------------------+" + ConsoleColors.RESET);
    }

    @Override
    protected boolean isGameOver() {
        for (Hero h : party.getHeroes()) {
            if (h.getRow() == 0) {
                System.out.println(ConsoleColors.GREEN + "\n*** VICTORY! ***" + ConsoleColors.RESET);
                return true;
            }
        }
        for (Monster m : activeMonsters) {
            if (m.getRow() == 7) {
                System.out.println(ConsoleColors.RED + "\n*** DEFEAT! ***" + ConsoleColors.RESET);
                System.out.println(ConsoleColors.RED + "You lost!" + ConsoleColors.RESET);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean shouldQuit() { return quitGame; }

    private void showDetailedHeroInfo(Hero hero) {
        System.out.println("\n" + ConsoleColors.WHITE_BOLD + "=== HERO INFORMATION ===" + ConsoleColors.RESET);
        
        System.out.println("\n" + ConsoleColors.PURPLE + String.format("[%s] %s (Lvl %d) - Lane %d", 
                hero.getType(), hero.getName(), hero.getLevel(), hero.getLane() + 1) + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "Position: (" + hero.getRow() + ", " + hero.getCol() + ")" + ConsoleColors.RESET);

        System.out.println(ConsoleColors.CYAN + "\n+----------+----------+----------+----------+----------+------------+------------+" + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " HP: " + ConsoleColors.GREEN + "%-5.0f" + ConsoleColors.RESET + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " MP: " + ConsoleColors.BLUE + "%-5.0f" + ConsoleColors.RESET + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " STR: %-4.0f" + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " DEX: %-4.0f" + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " AGI: %-4.0f" + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " GOLD: " + ConsoleColors.YELLOW + "%-5.0f" + ConsoleColors.RESET + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " XP: %-5d " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                hero.getHp(), hero.getMana(), hero.getStrength(), hero.getDexterity(), hero.getAgility(), hero.getMoney(), hero.getExperience());
        System.out.println(ConsoleColors.CYAN + "+----------+----------+----------+----------+----------+------------+------------+" + ConsoleColors.RESET);

        // Equipment section
        System.out.println(ConsoleColors.CYAN + "\n" + ConsoleColors.WHITE_BOLD + "EQUIPPED:" + ConsoleColors.RESET);
        if (hero.getEquippedWeapon() != null) {
            System.out.println("> Weapon: " + ConsoleColors.RED + hero.getEquippedWeapon().getName() + ConsoleColors.RESET + 
                    " (Dmg: +" + hero.getEquippedWeapon().getDamage() + ")");
        } else {
            System.out.println("> Weapon: " + ConsoleColors.YELLOW + "None" + ConsoleColors.RESET);
        }
        
        if (hero.getEquippedArmor() != null) {
            System.out.println("> Armor: " + ConsoleColors.BLUE + hero.getEquippedArmor().getName() + ConsoleColors.RESET + 
                    " (Def: +" + hero.getEquippedArmor().getDamageReduction() + ")");
        } else {
            System.out.println("> Armor: " + ConsoleColors.YELLOW + "None" + ConsoleColors.RESET);
        }

        // Inventory section
        System.out.println(ConsoleColors.CYAN + "\n" + ConsoleColors.WHITE_BOLD + "INVENTORY:" + ConsoleColors.RESET);
        
        List<items.Item> items = hero.getInventory().getItems();
        if (items.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  (Empty)" + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.CYAN + "+----------------------+--------+----------+--------------------------------------+" + ConsoleColors.RESET);
            for (items.Item item : items) {
                String stats = extractItemStats(item);
                if (stats.length() > 36) stats = stats.substring(0, 33) + "...";

                System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " Lv%-4d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " " + ConsoleColors.YELLOW + "%-8.0f" + ConsoleColors.RESET + " " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-36s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                        item.getName(), item.getMinLevel(), item.getPrice(), stats);
            }
            System.out.println(ConsoleColors.CYAN + "+----------------------+--------+----------+--------------------------------------+" + ConsoleColors.RESET);
        }
        
        System.out.println("\n" + ConsoleColors.YELLOW + "Press Enter to continue..." + ConsoleColors.RESET);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    private String extractItemStats(items.Item item) {
        if (item instanceof items.Weapon) {
            return String.format("Dmg: %.0f", ((items.Weapon) item).getDamage());
        } else if (item instanceof items.Armor) {
            return String.format("Def: %.0f", ((items.Armor) item).getDamageReduction());
        } else if (item instanceof items.Spell) {
            items.Spell s = (items.Spell) item;
            return String.format("%s Spell (Dmg: %.0f, Cost: %.0f)", s.getType(), s.getDamage(), s.getManaCost());
        } else if (item instanceof items.Potion) {
            items.Potion p = (items.Potion) item;
            return String.format("Potion (+%.0f)", p.getAttributeIncrease());
        }
        return "Item";
    }

    @Override
    protected void endGame() {
        System.out.println(ConsoleColors.RED + "\nGame Over. Thanks for playing Legends of Valor!" + ConsoleColors.RESET);
        if (party != null) {
            System.out.println(ConsoleColors.WHITE_BOLD + "Final Status:" + ConsoleColors.RESET);
            printDashboard();
        }
        
        Scanner scanner = new Scanner(System.in);
        String input = InputValidator.getValidOption(scanner, "\n" + ConsoleColors.YELLOW + "Do you want to play again? (yes/no): " + ConsoleColors.RESET, "y", "yes", "n", "no");
        
        if (input.equals("y") || input.equals("yes")) {
            // Restart the entire application to go back to game selection
            System.out.println(ConsoleColors.GREEN + "Returning to main menu..." + ConsoleColors.RESET);
            common.GameRunner.run();
        } else {
            System.out.println(ConsoleColors.CYAN + "Goodbye!" + ConsoleColors.RESET);
            System.exit(0);
        }
    }
}
