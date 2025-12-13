package game;

import board.Cell;
import board.CellType;
import board.ValorBoard;
import common.InputValidator;
import entities.Hero;
import entities.Monster;
import entities.Party;
import items.Potion;
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

    // ANSI Colors for Console
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_WHITE_BOLD = "\033[1;37m";

    @Override
    protected void initializeGame(Scanner scanner) {
        System.out.println(ANSI_CYAN + "Initializing Legends of Valor..." + ANSI_RESET);

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

        System.out.println(ANSI_GREEN + "\nThe battle for the Nexus begins!" + ANSI_RESET);
    }

    private void setupParty(Scanner scanner) {
        this.party = new Party();

        List<Hero> availableHeroes = new ArrayList<>();
        availableHeroes.addAll(GameDataLoader.loadHeroes("Warriors.txt", Hero.HeroType.WARRIOR));
        availableHeroes.addAll(GameDataLoader.loadHeroes("Sorcerers.txt", Hero.HeroType.SORCERER));
        availableHeroes.addAll(GameDataLoader.loadHeroes("Paladins.txt", Hero.HeroType.PALADIN));

        System.out.println("\n" + ANSI_YELLOW + "=== RECRUIT YOUR TEAM ===" + ANSI_RESET);
        System.out.println("You must select 3 Heroes to defend the Nexus.");

        while (party.getHeroes().size() < 3) {
            System.out.println("\n" + ANSI_WHITE_BOLD + "Party Size: " + party.getHeroes().size() + "/3" + ANSI_RESET);

            // TABLE HEADER
            System.out.println(ANSI_CYAN + "+----+----------------------+-----------+-----+------+------+------+------+------+" + ANSI_RESET);
            System.out.printf(ANSI_CYAN + "| %-2s | %-20s | %-9s | %-3s | %-4s | %-4s | %-4s | %-4s | %-4s |%n" + ANSI_RESET,
                    "ID", "Name", "Class", "Lvl", "HP", "MP", "Str", "Dex", "Agi");
            System.out.println(ANSI_CYAN + "+----+----------------------+-----------+-----+------+------+------+------+------+" + ANSI_RESET);

            // TABLE ROWS
            for (int i = 0; i < availableHeroes.size(); i++) {
                Hero h = availableHeroes.get(i);
                System.out.printf("| %-2d | %-20s | %-9s | %-3d | %-4.0f | %-4.0f | %-4.0f | %-4.0f | %-4.0f |%n",
                        (i + 1), h.getName(), h.getType(), h.getLevel(), h.getHp(), h.getMana(),
                        h.getStrength(), h.getDexterity(), h.getAgility());
            }
            System.out.println(ANSI_CYAN + "+----+----------------------+-----------+-----+------+------+------+------+------+" + ANSI_RESET);

            int choice = InputValidator.getValidInt(scanner, "Select Hero ID: ", 1, availableHeroes.size());
            Hero selected = availableHeroes.remove(choice - 1);

            // Assign a unique lane to each hero as they are picked (0, 1, or 2)
            selected.setLane(party.getHeroes().size());
            party.addHero(selected);

            System.out.println(ANSI_GREEN + selected.getName() + " joined the party!" + ANSI_RESET);
        }
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

        System.out.println(ANSI_RED + "*** Reinforcements! New Monsters have entered the Nexus! ***" + ANSI_RESET);

        for (int i = 0; i < 3; i++) {
            Cell spawnCell = board.getCell(0, laneSpawns[i]);
            if (spawnCell.hasMonster()) {
                System.out.println(ANSI_YELLOW + "Lane " + (i + 1) + " spawn blocked!" + ANSI_RESET);
                continue;
            }

            Monster template = monsterCatalog.get((int) (Math.random() * monsterCatalog.size()));
            Monster m = new Monster(template.getName(), template.getType(), highestHeroLvl,
                    template.getBaseDamage(), template.getDefense(), template.getDodgeChance() * 100);

            m.setPosition(0, laneSpawns[i]);
            m.setLane(i);

            spawnCell.setMonster(m);
            activeMonsters.add(m);
        }
    }

    @Override
    protected void processTurn(Scanner scanner) {
        System.out.println("\n" + ANSI_YELLOW + "=== ROUND " + roundCount + " ===" + ANSI_RESET);
        board.printBoard();

        // 1. HEROES TURN
        for (Hero hero : party.getHeroes()) {
            if (hero.isFainted()) {
                System.out.println(ANSI_RED + hero.getName() + " is fainted (respawns at Nexus next round)." + ANSI_RESET);
                continue;
            }

            System.out.println("\nTurn: " + ANSI_CYAN + hero.getName() + " [H" + (hero.getLane() + 1) + "]" + ANSI_RESET + " (Lane " + hero.getLane() + ")");
            boolean actionTaken = false;

            while (!actionTaken && !quitGame) {
                printControls();

                String choice = InputValidator.getValidOption(scanner, "Action: ", "w", "a", "t", "r", "m", "p", "e", "i", "q");

                switch (choice) {
                    case "w": actionTaken = handleMove(scanner, hero); break;
                    case "a": actionTaken = handleAttack(scanner, hero); break;
                    case "t": actionTaken = handleTeleport(scanner, hero); break;
                    case "r": actionTaken = handleRecall(hero); break;
                    case "m": actionTaken = handleMarket(scanner, hero); break;
                    case "p": actionTaken = handlePotion(scanner, hero); break;
                    case "e": actionTaken = handleEquip(scanner, hero); break;
                    case "i": System.out.println(hero); break;
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
        System.out.print("[" + ANSI_YELLOW + "W" + ANSI_RESET + "]Move ");
        System.out.print("[" + ANSI_YELLOW + "A" + ANSI_RESET + "]ttack ");
        System.out.print("[" + ANSI_YELLOW + "T" + ANSI_RESET + "]eleport ");
        System.out.print("[" + ANSI_YELLOW + "R" + ANSI_RESET + "]ecall ");
        System.out.print("[" + ANSI_YELLOW + "M" + ANSI_RESET + "]arket ");
        System.out.print("[" + ANSI_YELLOW + "P" + ANSI_RESET + "]otion ");
        System.out.print("[" + ANSI_YELLOW + "E" + ANSI_RESET + "]quip ");
        System.out.print("[" + ANSI_YELLOW + "I" + ANSI_RESET + "]nfo ");
        System.out.println("[" + ANSI_YELLOW + "Q" + ANSI_RESET + "]uit");
        System.out.println(ANSI_CYAN + "----------------------------------------------------------------" + ANSI_RESET);
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
            System.out.println(ANSI_RED + "Blocked: Cannot move off the board." + ANSI_RESET);
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
                            System.out.println(ANSI_RED + "Blocked: You cannot move behind " + m.getName() + "!" + ANSI_RESET);
                            return false;
                        }
                    }
                }
            }
        }

        Cell target = board.getCell(newR, newC);

        // Obstacles
        if (target.getType() == CellType.OBSTACLE) {
            System.out.println(ANSI_YELLOW + "An OBSTACLE blocks your path." + ANSI_RESET);
            String choice = InputValidator.getValidOption(scanner, "Do you want to destroy it? (y/n): ", "y", "n");

            if (choice.equals("y")) {
                target.setType(CellType.COMMON); // Convert to plain cell
                System.out.println(ANSI_GREEN + "You destroyed the obstacle! (Turn Used)" + ANSI_RESET);
                return true; // Turn consumed, but hero doesn't move yet
            } else {
                return false; // Action cancelled
            }
        }

        // 1. Terrain Check
        if (!target.isAccessible()) {
            System.out.println(ANSI_RED + "Blocked: Inaccessible terrain." + ANSI_RESET);
            return false;
        }

        // 2. Occupancy Check
        if (target.hasHero()) {
            System.out.println(ANSI_RED + "Blocked: Another hero is standing there." + ANSI_RESET);
            return false;
        }
        if (target.hasMonster()) {
            System.out.println(ANSI_RED + "Blocked: You cannot walk through a monster!" + ANSI_RESET);
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
                System.out.println(ANSI_GREEN + "Terrain: Bush increases Dexterity!" + ANSI_RESET);
                break;
            case CAVE:
                System.out.println(ANSI_YELLOW + "Terrain: Cave increases Agility!" + ANSI_RESET);
                break;
            case KOULOU:
                System.out.println(ANSI_BLUE + "Terrain: Koulou increases Strength!" + ANSI_RESET);
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
            System.out.println(ANSI_YELLOW + "No monsters in range." + ANSI_RESET);
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
            System.out.println(hero.getName() + " dealt " + ANSI_RED + String.format("%.0f", actualDmg) + ANSI_RESET + " damage!");

            if (target.isFainted()) {
                System.out.println(ANSI_GREEN + target.getName() + " was DEFEATED!" + ANSI_RESET);
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

    private boolean handleTeleport(Scanner scanner, Hero hero) {
        List<Hero> targets = new ArrayList<>();
        for (Hero h : party.getHeroes()) {
            if (h != hero && !h.isFainted() && h.getLane() != hero.getLane()) {
                targets.add(h);
            }
        }

        if (targets.isEmpty()) {
            System.out.println(ANSI_YELLOW + "No valid heroes to teleport to (must be in a different lane)." + ANSI_RESET);
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
                    System.out.println(ANSI_PURPLE + "*WOOSH* " + hero.getName() + " teleported to " + destHero.getName() + "!" + ANSI_RESET);
                    return true;
                }
            }
        }
        System.out.println(ANSI_RED + "Teleport failed: No open space beside target." + ANSI_RESET);
        return false;
    }

    private boolean handleRecall(Hero hero) {
        int r = 7;
        int c = (hero.getLane() == 0) ? 0 : (hero.getLane() == 1) ? 3 : 6;

        Cell spawn = board.getCell(r, c);
        if (spawn.hasHero() && spawn.getHero() != hero) {
            System.out.println(ANSI_RED + "Recall failed: Your Nexus spawn is blocked." + ANSI_RESET);
            return false;
        }

        board.getCell(hero.getRow(), hero.getCol()).removeHero();
        hero.setPosition(r, c);
        spawn.setHero(hero);
        System.out.println(ANSI_CYAN + hero.getName() + " recalled to Nexus." + ANSI_RESET);
        return true;
    }

    private boolean handleMarket(Scanner scanner, Hero hero) {
        Cell currentCell = board.getCell(hero.getRow(), hero.getCol());
        
        // Check if hero is in a Nexus cell (row 7 is Hero Nexus)
        if (currentCell.getType() != CellType.NEXUS) {
            System.out.println(ANSI_RED + "Market unavailable: You must be in your Nexus to access the market!" + ANSI_RESET);
            return false;
        }
        
        System.out.println(ANSI_GREEN + hero.getName() + " enters the Nexus market..." + ANSI_RESET);
        
        // Use the overloaded single-hero market method
        marketController.enterMarket(scanner, hero);
        
        // Redisplay the board after exiting market
        board.printBoard();
        
        // Market visit doesn't consume a turn
        return false;
    }

    private boolean handlePotion(Scanner scanner, Hero hero) {
        List<Potion> potions = hero.getInventory().getPotions();
        if (potions.isEmpty()) {
            System.out.println("No potions!");
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
            if (weps.isEmpty()) { System.out.println("No weapons."); return false; }
            for (int i = 0; i < weps.size(); i++) System.out.println((i + 1) + ". " + weps.get(i).getName());
            int c = InputValidator.getValidInt(scanner, "Equip: ", 1, weps.size()) - 1;
            hero.equipWeapon(weps.get(c));
        } else {
            List<items.Armor> arms = hero.getInventory().getArmor();
            if (arms.isEmpty()) { System.out.println("No armor."); return false; }
            for (int i = 0; i < arms.size(); i++) System.out.println((i + 1) + ". " + arms.get(i).getName());
            int c = InputValidator.getValidInt(scanner, "Equip: ", 1, arms.size()) - 1;
            hero.equipArmor(arms.get(c));
        }
        return true;
    }

    private void processMonstersTurn() {
        System.out.println(ANSI_RED + "\n--- Monsters Turn ---" + ANSI_RESET);
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
                System.out.println(ANSI_GREEN + h.getName() + " has respawned at the Nexus!" + ANSI_RESET);
            }
        }
    }

    private void printDashboard() {
        System.out.println(ANSI_CYAN + "\n+------------------------------------------------------------+" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "|" + ANSI_RESET + ANSI_WHITE_BOLD + "                        PARTY STATUS                        " + ANSI_RESET + ANSI_CYAN + "|" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "+----------------------+-------+--------+--------+-----------+" + ANSI_RESET);
        System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-5s " + ANSI_CYAN + "|" + ANSI_RESET + " %-6s " + ANSI_CYAN + "|" + ANSI_RESET + " %-6s " + ANSI_CYAN + "|" + ANSI_RESET + " %-9s " + ANSI_CYAN + "|\n" + ANSI_RESET, "NAME", "LVL", "HP", "MP", "GOLD");
        System.out.println(ANSI_CYAN + "+----------------------+-------+--------+--------+-----------+" + ANSI_RESET);

        for (Hero h : party.getHeroes()) {
            System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-5d " + ANSI_CYAN + "|" + ANSI_RESET + " %-6.0f " + ANSI_CYAN + "|" + ANSI_RESET + " %-6.0f " + ANSI_CYAN + "|" + ANSI_RESET + " %-9.0f " + ANSI_CYAN + "|\n" + ANSI_RESET,
                    h.getName(), h.getLevel(), h.getHp(), h.getMana(), h.getMoney());
        }
        System.out.println(ANSI_CYAN + "+------------------------------------------------------------+" + ANSI_RESET);
    }

    @Override
    protected boolean isGameOver() {
        for (Hero h : party.getHeroes()) {
            if (h.getRow() == 0) {
                System.out.println(ANSI_GREEN + "\n*** VICTORY! ***" + ANSI_RESET);
                return true;
            }
        }
        for (Monster m : activeMonsters) {
            if (m.getRow() == 7) {
                System.out.println(ANSI_RED + "\n*** DEFEAT! ***" + ANSI_RESET);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean shouldQuit() { return quitGame; }

    @Override
    protected void endGame() {
        System.out.println(ANSI_RED + "\nGame Over. Thanks for playing Legends of Valor!" + ANSI_RESET);
        if (party != null) {
            System.out.println(ANSI_WHITE_BOLD + "Final Status:" + ANSI_RESET);
            printDashboard();
        }
    }
}