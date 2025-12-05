package game;

import board.Cell;
import board.LegendsBoard;
import common.InputValidator;
import entities.Hero;
import entities.Hero.HeroType;
import entities.Monster;
import entities.Monster.MonsterType;
import entities.Party;
import items.Item;
import items.Weapon;
import items.Armor;
import items.Potion;
import items.Spell;
import utils.GameDataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class LegendsGame extends Game {

    private LegendsBoard board;
    private Party party;
    private final Random random = new Random();
    private boolean quitGame = false;

    private boolean skipNextRender = false;

    private MarketController marketController;
    private BattleController battleController;

    private List<Hero> availableWarriors;
    private List<Hero> availableSorcerers;
    private List<Hero> availablePaladins;
    private List<Monster> allMonsters;

    // ANSI Color Constants
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_WHITE_BOLD = "\033[1;37m";

    @Override
    protected void initializeGame(Scanner scanner) {
        System.out.println(ANSI_CYAN + "Loading Game Data..." + ANSI_RESET);
        loadAssets();

        this.marketController = new MarketController();
        this.battleController = new BattleController(allMonsters);

        setupNewSession(scanner);
    }

    private void setupNewSession(Scanner scanner) {
        quitGame = false; // Reset quit flag for new session

        System.out.println("\n" + ANSI_YELLOW + "--- World Generation ---" + ANSI_RESET);
        int boardSize = InputValidator.getValidInt(scanner, "Enter board size (4-20): ", 4, 20);
        this.board = new LegendsBoard(boardSize);

        System.out.println("\n" + ANSI_YELLOW + "--- Hero Selection ---" + ANSI_RESET);
        int partySize = InputValidator.getValidInt(scanner, "Enter party size (1-3): ", 1, 3);

        this.party = new Party();
        for (int i = 0; i < partySize; i++) {
            System.out.println("\nSelect Hero #" + (i + 1) + ":");
            Hero selectedHero = selectHero(scanner);
            if (selectedHero == null) {
                quitGame = true;
                return;
            }
            party.addHero(selectedHero);
        }

        this.board.setParty(party);
        System.out.println(ANSI_GREEN + "\nThe party enters the world..." + ANSI_RESET);
    }

    private void loadAssets() {
        availableWarriors = GameDataLoader.loadHeroes("Warriors.txt", HeroType.WARRIOR);
        availableSorcerers = GameDataLoader.loadHeroes("Sorcerers.txt", HeroType.SORCERER);
        availablePaladins = GameDataLoader.loadHeroes("Paladins.txt", HeroType.PALADIN);

        allMonsters = new ArrayList<>();
        allMonsters.addAll(GameDataLoader.loadMonsters("Dragons.txt", MonsterType.DRAGON));
        allMonsters.addAll(GameDataLoader.loadMonsters("Exoskeletons.txt", MonsterType.EXOSKELETON));
        allMonsters.addAll(GameDataLoader.loadMonsters("Spirits.txt", MonsterType.SPIRIT));

        if (availableWarriors.isEmpty() && availableSorcerers.isEmpty() && availablePaladins.isEmpty()) {
            throw new RuntimeException("CRITICAL ERROR: No heroes could be loaded. Check data/ directory.");
        }
    }

    private Hero selectHero(Scanner scanner) {
        System.out.println("1. " + ANSI_RED + "Warrior" + ANSI_RESET + " (Favors Strength/Agility)");
        System.out.println("2. " + ANSI_BLUE + "Sorcerer" + ANSI_RESET + " (Favors Dexterity/Agility)");
        System.out.println("3. " + ANSI_GREEN + "Paladin" + ANSI_RESET + " (Favors Strength/Dexterity)");

        int typeChoice = InputValidator.getValidInt(scanner, "Choose class: ", 1, 3);
        List<Hero> choiceList = (typeChoice == 1) ? availableWarriors :
                (typeChoice == 2) ? availableSorcerers : availablePaladins;

        System.out.println("\n" + ANSI_WHITE_BOLD + "Available Heroes:" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+------+------+------+------+------+" + ANSI_RESET);
        System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-2s " + ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-3s " + ANSI_CYAN + "|" + ANSI_RESET + " %-4s " + ANSI_CYAN + "|" + ANSI_RESET + " %-4s " + ANSI_CYAN + "|" + ANSI_RESET + " %-4s " + ANSI_CYAN + "|" + ANSI_RESET + " %-4s " + ANSI_CYAN + "|" + ANSI_RESET + " %-4s " + ANSI_CYAN + "|\n" + ANSI_RESET,
                "ID", "NAME", "LVL", "HP", "MP", "STR", "DEX", "AGI");
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+------+------+------+------+------+" + ANSI_RESET);

        for (int i = 0; i < choiceList.size(); i++) {
            Hero h = choiceList.get(i);
            System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-2d " + ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-3d " + ANSI_CYAN + "|" + ANSI_RESET + " %-4.0f " + ANSI_CYAN + "|" + ANSI_RESET + " %-4.0f " + ANSI_CYAN + "|" + ANSI_RESET + " %-4.0f " + ANSI_CYAN + "|" + ANSI_RESET + " %-4.0f " + ANSI_CYAN + "|" + ANSI_RESET + " %-4.0f " + ANSI_CYAN + "|\n" + ANSI_RESET,
                    (i + 1), h.getName(), h.getLevel(), h.getHp(), h.getMana(), h.getStrength(), h.getDexterity(), h.getAgility());
        }
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+------+------+------+------+------+" + ANSI_RESET);
        System.out.println((choiceList.size() + 1) + ". Quit Game");

        int heroIndex = InputValidator.getValidInt(scanner, "Select hero ID: ", 1, choiceList.size() + 1) - 1;

        if (heroIndex == choiceList.size()) {
            return null;
        }

        return choiceList.remove(heroIndex);
    }

    @Override
    protected void processTurn(Scanner scanner) {
        if (quitGame) return;

        if (!skipNextRender) {
            board.printBoard();
            printDashboard();
            printControls();
        }
        skipNextRender = false;

        String input = InputValidator.getValidOption(scanner, "Action: ", "w", "a", "s", "d", "m", "i", "q");

        switch (input) {
            case "w": moveParty(scanner, -1, 0); break;
            case "a": moveParty(scanner, 0, -1); break;
            case "s": moveParty(scanner, 1, 0); break;
            case "d": moveParty(scanner, 0, 1); break;
            case "m": handleMarketInteraction(scanner); break;
            case "i":
                showDetailedInfo();
                skipNextRender = true;
                break;
            case "q": quitGame = true; break;
        }
    }

    private void printDashboard() {
        System.out.println(ANSI_CYAN + "\n+------------------------------------------------------------+" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "|" + ANSI_RESET + ANSI_WHITE_BOLD + "                        PARTY STATUS                        " + ANSI_RESET + ANSI_CYAN + "|" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "+----------------------+-------+--------+--------+-----------+" + ANSI_RESET);
        System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-5s " + ANSI_CYAN + "|" + ANSI_RESET + " %-6s " + ANSI_CYAN + "|" + ANSI_RESET + " %-6s " + ANSI_CYAN + "|" + ANSI_RESET + " %-9s " + ANSI_CYAN + "|\n" + ANSI_RESET, "NAME", "LVL", "HP", "MP", "GOLD");
        System.out.println(ANSI_CYAN + "+----------------------+-------+--------+--------+-----------+" + ANSI_RESET);

        for (Hero h : party.getHeroes()) {
            String hp = String.format("%.0f", h.getHp());
            String mp = String.format("%.0f", h.getMana());

            System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-5d " + ANSI_CYAN + "|" + ANSI_RESET + " %-6.0f " + ANSI_CYAN + "|" + ANSI_RESET + " %-6.0f " + ANSI_CYAN + "|" + ANSI_RESET + " %-9.0f " + ANSI_CYAN + "|\n" + ANSI_RESET,
                    h.getName(), h.getLevel(), h.getHp(), h.getMana(), h.getMoney());
        }
        System.out.println(ANSI_CYAN + "+------------------------------------------------------------+" + ANSI_RESET);
    }

    private void printControls() {
        System.out.println(" CONTROLS: [" + ANSI_YELLOW + "W" + ANSI_RESET + "]Up [" + ANSI_YELLOW + "A" + ANSI_RESET + "]Left [" + ANSI_YELLOW + "S" + ANSI_RESET + "]Down [" + ANSI_YELLOW + "D" + ANSI_RESET + "]Right  [" + ANSI_YELLOW + "M" + ANSI_RESET + "]Market [" + ANSI_YELLOW + "I" + ANSI_RESET + "]Info [" + ANSI_YELLOW + "Q" + ANSI_RESET + "]Quit");
        System.out.println("--------------------------------------------------------------");
    }

    private void moveParty(Scanner scanner, int dRow, int dCol) {
        int newRow = party.getRow() + dRow;
        int newCol = party.getCol() + dCol;

        if (!board.isValidCoordinate(newRow, newCol)) {
            System.out.println(ANSI_RED + "You cannot move off the edge of the world!" + ANSI_RESET);
            return;
        }

        Cell targetCell = board.getCell(newRow, newCol);
        if (!targetCell.isAccessible()) {
            System.out.println(ANSI_RED + "That path is blocked (Inaccessible)." + ANSI_RESET);
            return;
        }

        party.setLocation(newRow, newCol);

        if (targetCell.isCommon()) {
            checkForBattle(scanner);
        }
    }

    private void checkForBattle(Scanner scanner) {
        if (random.nextDouble() < 0.50) {
            System.out.println(ANSI_RED + "\n*** AMBUSH! You have encountered monsters! ***" + ANSI_RESET);
            battleController.startBattle(scanner, party);
        }
    }

    private void handleMarketInteraction(Scanner scanner) {
        Cell currentCell = board.getCell(party.getRow(), party.getCol());
        if (!currentCell.isMarket()) {
            System.out.println(ANSI_YELLOW + "There is no market here." + ANSI_RESET);
            return;
        }
        marketController.enterMarket(scanner, party);
    }

    private void showDetailedInfo() {
        System.out.println(ANSI_WHITE_BOLD + "\n=== DETAILED HERO INFORMATION ===" + ANSI_RESET);

        for (Hero h : party.getHeroes()) {
            System.out.println("\n" + ANSI_PURPLE + "+ " + String.format("[%s] %s (Lvl %d)", h.getType(), h.getName(), h.getLevel()) + ANSI_RESET);

            System.out.println(ANSI_CYAN + "+----------+----------+----------+----------+----------+------------+------------+" + ANSI_RESET);
            System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " HP: " + ANSI_GREEN + "%-5.0f" + ANSI_RESET + ANSI_CYAN + "|" + ANSI_RESET + " MP: " + ANSI_BLUE + "%-5.0f" + ANSI_RESET + ANSI_CYAN + "|" + ANSI_RESET + " STR: %-4.0f" + ANSI_CYAN + "|" + ANSI_RESET + " DEX: %-4.0f" + ANSI_CYAN + "|" + ANSI_RESET + " AGI: %-4.0f" + ANSI_CYAN + "|" + ANSI_RESET + " GOLD: " + ANSI_YELLOW + "%-5.0f" + ANSI_RESET + ANSI_CYAN + "|" + ANSI_RESET + " XP: %-5d " + ANSI_CYAN + "|\n" + ANSI_RESET,
                    h.getHp(), h.getMana(), h.getStrength(), h.getDexterity(), h.getAgility(), h.getMoney(), h.getExperience());
            System.out.println(ANSI_CYAN + "+----------+----------+----------+----------+----------+------------+------------+" + ANSI_RESET);

            System.out.println(ANSI_CYAN + "|" + ANSI_RESET + " " + ANSI_WHITE_BOLD + "INVENTORY" + ANSI_RESET + "                                                                    " + ANSI_CYAN + "|" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "+----------------------+--------+----------+--------------------------------------+" + ANSI_RESET);

            List<Item> items = h.getInventory().getItems();
            if (items.isEmpty()) {
                System.out.println(ANSI_CYAN + "|" + ANSI_RESET + " (Empty)              " + ANSI_CYAN + "|" + ANSI_RESET + "        " + ANSI_CYAN + "|" + ANSI_RESET + "          " + ANSI_CYAN + "|" + ANSI_RESET + "                                      " + ANSI_CYAN + "|" + ANSI_RESET);
            } else {
                for (Item item : items) {
                    String stats = extractItemStats(item);
                    if (stats.length() > 40) stats = stats.substring(0, 37) + "...";

                    System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " Lv%-4d " + ANSI_CYAN + "|" + ANSI_RESET + " " + ANSI_YELLOW + "%-8.0f" + ANSI_RESET + " " + ANSI_CYAN + "|" + ANSI_RESET + " %-36s " + ANSI_CYAN + "|\n" + ANSI_RESET,
                            item.getName(), item.getMinLevel(), item.getPrice(), stats);
                }
            }
            System.out.println(ANSI_CYAN + "+----------------------+--------+----------+--------------------------------------+" + ANSI_RESET);
        }
        System.out.println("Press Enter to continue...");
    }

    private String extractItemStats(Item item) {
        if (item instanceof Weapon) {
            return String.format("Dmg: %.0f", ((Weapon) item).getDamage());
        } else if (item instanceof Armor) {
            return String.format("Def: %.0f", ((Armor) item).getDamageReduction());
        } else if (item instanceof Spell) {
            return String.format("%s Dmg: %.0f", ((Spell) item).getType(), ((Spell) item).getDamage());
        } else if (item instanceof Potion) {
            return String.format("+%.0f", ((Potion) item).getAttributeIncrease());
        }
        return "";
    }

    @Override
    protected boolean isGameOver() {
        return (party != null && party.isPartyWipedOut()) || quitGame;
    }

    @Override
    protected boolean shouldQuit() {
        return quitGame;
    }

    @Override
    protected void endGame() {
        System.out.println(ANSI_RED + "\nGame Over. Thanks for playing Legends: Monsters and Heroes!" + ANSI_RESET);
        if (party != null) {
            System.out.println(ANSI_WHITE_BOLD + "Final Status:" + ANSI_RESET);
            printDashboard();
        }

        // --- NEW: Restart Logic ---
        Scanner scanner = new Scanner(System.in); // Use a fresh scanner or pass it down if possible
        // Note: System.in should not be closed if we want to read again, but here we are at end of app lifecycle usually.

        String input = InputValidator.getValidOption(scanner, "\n" + ANSI_YELLOW + "Do you want to play again? (y/n): " + ANSI_RESET, "y", "n");

        if (input.equals("y")) {
            // Restart the game
            System.out.println(ANSI_GREEN + "Starting a new game..." + ANSI_RESET);

            quitGame = false;
            skipNextRender = false;

            play(scanner);
        } else {
            System.out.println(ANSI_CYAN + "Goodbye!" + ANSI_RESET);
            System.exit(0);
        }
    }
}