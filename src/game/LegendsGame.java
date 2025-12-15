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

import utils.ConsoleColors;

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



    @Override
    protected void initializeGame(Scanner scanner) {
        System.out.println(ConsoleColors.CYAN + "Loading Game Data..." + ConsoleColors.RESET);
        loadAssets();

        this.marketController = new MarketController();
        this.battleController = new BattleController(allMonsters);

        setupNewSession(scanner);
    }

    private void setupNewSession(Scanner scanner) {
        quitGame = false; // Reset quit flag for new session

        System.out.println("\n" + ConsoleColors.YELLOW + "--- World Generation ---" + ConsoleColors.RESET);
        int boardSize = InputValidator.getValidInt(scanner, "Enter board size (4-20): ", 4, 20);
        this.board = new LegendsBoard(boardSize);

        System.out.println("\n" + ConsoleColors.YELLOW + "--- Hero Selection ---" + ConsoleColors.RESET);
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
        System.out.println(ConsoleColors.GREEN + "\nThe party enters the world..." + ConsoleColors.RESET);
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
        System.out.println("1. " + ConsoleColors.RED + "Warrior" + ConsoleColors.RESET + " (Favors Strength/Agility)");
        System.out.println("2. " + ConsoleColors.BLUE + "Sorcerer" + ConsoleColors.RESET + " (Favors Dexterity/Agility)");
        System.out.println("3. " + ConsoleColors.GREEN + "Paladin" + ConsoleColors.RESET + " (Favors Strength/Dexterity)");

        int typeChoice = InputValidator.getValidInt(scanner, "Choose class: ", 1, 3);
        List<Hero> choiceList = (typeChoice == 1) ? availableWarriors :
                (typeChoice == 2) ? availableSorcerers : availablePaladins;

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
        System.out.println(ConsoleColors.CYAN + "\n+------------------------------------------------------------+" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + ConsoleColors.WHITE_BOLD + "                        PARTY STATUS                        " + ConsoleColors.RESET + ConsoleColors.CYAN + "|" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "+----------------------+-------+--------+--------+-----------+" + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-5s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-6s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-6s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-9s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET, "NAME", "LVL", "HP", "MP", "GOLD");
        System.out.println(ConsoleColors.CYAN + "+----------------------+-------+--------+--------+-----------+" + ConsoleColors.RESET);

        for (Hero h : party.getHeroes()) {
            String hp = String.format("%.0f", h.getHp());
            String mp = String.format("%.0f", h.getMana());

            System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-5d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-6.0f " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-6.0f " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-9.0f " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                    h.getName(), h.getLevel(), h.getHp(), h.getMana(), h.getMoney());
        }
        System.out.println(ConsoleColors.CYAN + "+------------------------------------------------------------+" + ConsoleColors.RESET);
    }

    private void printControls() {
        System.out.println(" CONTROLS: [" + ConsoleColors.YELLOW + "W" + ConsoleColors.RESET + "]Up [" + ConsoleColors.YELLOW + "A" + ConsoleColors.RESET + "]Left [" + ConsoleColors.YELLOW + "S" + ConsoleColors.RESET + "]Down [" + ConsoleColors.YELLOW + "D" + ConsoleColors.RESET + "]Right  [" + ConsoleColors.YELLOW + "M" + ConsoleColors.RESET + "]Market [" + ConsoleColors.YELLOW + "I" + ConsoleColors.RESET + "]Info [" + ConsoleColors.YELLOW + "Q" + ConsoleColors.RESET + "]Quit");
        System.out.println("--------------------------------------------------------------");
    }

    private void moveParty(Scanner scanner, int dRow, int dCol) {
        int newRow = party.getRow() + dRow;
        int newCol = party.getCol() + dCol;

        if (!board.isValidCoordinate(newRow, newCol)) {
            System.out.println(ConsoleColors.RED + "You cannot move off the edge of the world!" + ConsoleColors.RESET);
            return;
        }

        Cell targetCell = board.getCell(newRow, newCol);
        if (!targetCell.isAccessible()) {
            System.out.println(ConsoleColors.RED + "That path is blocked (Inaccessible)." + ConsoleColors.RESET);
            return;
        }

        party.setLocation(newRow, newCol);

        if (targetCell.isCommon()) {
            checkForBattle(scanner);
        }
    }

    private void checkForBattle(Scanner scanner) {
        if (random.nextDouble() < 0.50) {
            System.out.println(ConsoleColors.RED + "\n*** AMBUSH! You have encountered monsters! ***" + ConsoleColors.RESET);
            battleController.startBattle(scanner, party);
        }
    }

    private void handleMarketInteraction(Scanner scanner) {
        Cell currentCell = board.getCell(party.getRow(), party.getCol());
        if (!currentCell.isMarket()) {
            System.out.println(ConsoleColors.YELLOW + "There is no market here." + ConsoleColors.RESET);
            return;
        }
        marketController.enterMarket(scanner, party);
    }

    private void showDetailedInfo() {
        System.out.println(ConsoleColors.WHITE_BOLD + "\n=== DETAILED HERO INFORMATION ===" + ConsoleColors.RESET);

        for (Hero h : party.getHeroes()) {
            System.out.println("\n" + ConsoleColors.PURPLE + "+ " + String.format("[%s] %s (Lvl %d)", h.getType(), h.getName(), h.getLevel()) + ConsoleColors.RESET);

            System.out.println(ConsoleColors.CYAN + "+----------+----------+----------+----------+----------+------------+------------+" + ConsoleColors.RESET);
            System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " HP: " + ConsoleColors.GREEN + "%-5.0f" + ConsoleColors.RESET + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " MP: " + ConsoleColors.BLUE + "%-5.0f" + ConsoleColors.RESET + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " STR: %-4.0f" + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " DEX: %-4.0f" + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " AGI: %-4.0f" + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " GOLD: " + ConsoleColors.YELLOW + "%-5.0f" + ConsoleColors.RESET + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " XP: %-5d " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                    h.getHp(), h.getMana(), h.getStrength(), h.getDexterity(), h.getAgility(), h.getMoney(), h.getExperience());
            System.out.println(ConsoleColors.CYAN + "+----------+----------+----------+----------+----------+------------+------------+" + ConsoleColors.RESET);

            System.out.println(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " " + ConsoleColors.WHITE_BOLD + "INVENTORY" + ConsoleColors.RESET + "                                                                    " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "+----------------------+--------+----------+--------------------------------------+" + ConsoleColors.RESET);

            List<Item> items = h.getInventory().getItems();
            if (items.isEmpty()) {
                System.out.println(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " (Empty)              " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + "        " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + "          " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + "                                      " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET);
            } else {
                for (Item item : items) {
                    String stats = extractItemStats(item);
                    if (stats.length() > 40) stats = stats.substring(0, 37) + "...";

                    System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " Lv%-4d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " " + ConsoleColors.YELLOW + "%-8.0f" + ConsoleColors.RESET + " " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-36s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                            item.getName(), item.getMinLevel(), item.getPrice(), stats);
                }
            }
            System.out.println(ConsoleColors.CYAN + "+----------------------+--------+----------+--------------------------------------+" + ConsoleColors.RESET);
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
        if (party != null && party.isPartyWipedOut()) {
            System.out.println(ConsoleColors.RED + "\n*** DEFEAT! ***" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED + "You lost!" + ConsoleColors.RESET);
            return true;
        }
        return quitGame;
    }

    @Override
    protected boolean shouldQuit() {
        return quitGame;
    }

    @Override
    protected void endGame() {
        System.out.println(ConsoleColors.RED + "\nGame Over. Thanks for playing Legends: Monsters and Heroes!" + ConsoleColors.RESET);
        if (party != null) {
            System.out.println(ConsoleColors.WHITE_BOLD + "Final Status:" + ConsoleColors.RESET);
            printDashboard();
        }

        // --- NEW: Restart Logic ---
        Scanner scanner = new Scanner(System.in); // Use a fresh scanner or pass it down if possible
        // Note: System.in should not be closed if we want to read again, but here we are at end of app lifecycle usually.

        String input = InputValidator.getValidOption(scanner, "\n" + ConsoleColors.YELLOW + "Do you want to play again? (yes/no): " + ConsoleColors.RESET, "y", "yes", "n", "no");

        if (input.equals("y") || input.equals("yes")) {
            // Restart the game
            System.out.println(ConsoleColors.GREEN + "Starting a new game..." + ConsoleColors.RESET);

            quitGame = false;
            skipNextRender = false;

            play(scanner);
        } else {
            System.out.println(ConsoleColors.CYAN + "Goodbye!" + ConsoleColors.RESET);
            System.exit(0);
        }
    }
}
