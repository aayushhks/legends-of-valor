package common;

import utils.ConsoleColors;
import game.LegendsGame;
import game.ValorGame;
import java.util.Scanner;

/**
 * Specialized class responsible for bootstrapping the game application.
 * Encapsulates the execution logic and global error handling strategies.
 */
public class GameRunner {



    /**
     * Safely starts the game loop.
     * Any unhandled exceptions during the game's lifecycle will be caught here.
     */
    public static void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            printWelcomeBanner();

            System.out.println(ConsoleColors.CYAN + "      Select Your Destiny:" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE + "  ╔════════════════════════════════════════════╗" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE + "  ║ " + ConsoleColors.BLUE + "1." + ConsoleColors.WHITE_BOLD + " Legends: Monsters and Heroes (RPG)      " + ConsoleColors.PURPLE + "║" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE + "  ║ " + ConsoleColors.BLUE + "2." + ConsoleColors.WHITE_BOLD + " Legends of Valor (MOBA Strategy)        " + ConsoleColors.PURPLE + "║" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE + "  ╚════════════════════════════════════════════╝" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.WHITE_BOLD + "    3. Game Information & Instructions" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.WHITE_BOLD + "    4. Exit Game" + ConsoleColors.RESET);
            System.out.println();

            int choice = InputValidator.getValidInt(scanner, ConsoleColors.YELLOW + "Choose Option: " + ConsoleColors.RESET, 1, 4);

            switch (choice) {
                case 1:
                    printLegendsRules();
                    new LegendsGame().play(scanner);
                    break;
                case 2:
                    printValorRules();
                    new ValorGame().play(scanner);
                    break;
                case 3:
                    showGameInfo(scanner);
                    break;
                case 4:
                    System.out.println(ConsoleColors.CYAN + "Thank you for playing! Goodbye!" + ConsoleColors.RESET);
                    System.exit(0);
                    break;
            }

        } catch (Exception e) {
            ErrorHandler.handleFatalError(e);
        }
    }

    /**
     * Shows detailed game information using the Strategy pattern.
     * Allows users to learn about either game mode before playing.
     */
    private static void showGameInfo(Scanner scanner) {
        System.out.println(ConsoleColors.CYAN + "\n      Game Information:" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "  ╔════════════════════════════════════════════╗" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "  ║ " + ConsoleColors.WHITE_BOLD + "1. Legends: Monsters and Heroes Info       " + ConsoleColors.PURPLE + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "  ║ " + ConsoleColors.WHITE_BOLD + "2. Legends of Valor Info                   " + ConsoleColors.PURPLE + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "  ║ " + ConsoleColors.WHITE_BOLD + "3. Return to Main Menu                     " + ConsoleColors.PURPLE + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + "  ╚════════════════════════════════════════════╝" + ConsoleColors.RESET);
        System.out.println();

        int infoChoice = InputValidator.getValidInt(scanner, ConsoleColors.YELLOW + "Choose Info Topic: " + ConsoleColors.RESET, 1, 3);

        switch (infoChoice) {
            case 1:
                new LegendsGameInfo().displayInfo();
                run(); // Return to main menu
                break;
            case 2:
                new ValorGameInfo().displayInfo();
                run(); // Return to main menu
                break;
            case 3:
                run(); // Return to main menu
                break;
        }
    }

    private static void printWelcomeBanner() {
        System.out.println(ConsoleColors.BLUE + "=================================================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "   __                                 _     " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "  / /  ___  __ _  ___ _ __   __| |___  " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + " / /  / _ \\/ _` |/ _ \\ '_ \\ / _` / __| " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "/ /__|  __/ (_| |  __/ | | | (_| \\__ \\ " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "\\____/\\___|\\__, |\\___|_| |_|\\__,_|___/ " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "           |___/                       " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + "             A R C H I V E             " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + "=================================================" + ConsoleColors.RESET);
        System.out.println();
    }

    private static void printLegendsRules() {
        // Box Width: 63 Inner Characters
        System.out.println("\n" + ConsoleColors.GREEN + "╔═══════════════════════════════════════════════════════════════╗" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + "║" + ConsoleColors.WHITE_BOLD + "            LEGENDS: MONSTERS AND HEROES RULES                 " + ConsoleColors.GREEN + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + "╠═══════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + "║ " + ConsoleColors.YELLOW + "GOAL:    " + ConsoleColors.RESET + "Defeat all monsters to advance.                      " + ConsoleColors.GREEN + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + "║ " + ConsoleColors.YELLOW + "COMBAT:  " + ConsoleColors.RESET + "Turn-based battles with a party of heroes.           " + ConsoleColors.GREEN + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + "║ " + ConsoleColors.YELLOW + "MARKET:  " + ConsoleColors.RESET + "Buy weapons, armor, potions, and spells.             " + ConsoleColors.GREEN + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + "║ " + ConsoleColors.YELLOW + "GROWTH:  " + ConsoleColors.RESET + "Gain XP and Gold to level up stats.                  " + ConsoleColors.GREEN + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + "║ " + ConsoleColors.YELLOW + "DEFEAT:  " + ConsoleColors.RESET + "Game Over if all heroes faint.                       " + ConsoleColors.GREEN + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.GREEN + "╚═══════════════════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        System.out.println();
    }

    private static void printValorRules() {
        // Box Width: 63 Inner Characters
        System.out.println("\n" + ConsoleColors.RED + "╔═══════════════════════════════════════════════════════════════╗" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║" + ConsoleColors.WHITE_BOLD + "                  LEGENDS OF VALOR RULES                       " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "╠═══════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║ " + ConsoleColors.YELLOW + "WIN:     " + ConsoleColors.RESET + "Move a Hero to the Monsters' Nexus (Row 0).          " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║ " + ConsoleColors.YELLOW + "LOSE:    " + ConsoleColors.RESET + "If a Monster reaches your Nexus (Row 7).             " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║ " + ConsoleColors.YELLOW + "BOARD:   " + ConsoleColors.RESET + "8x8 Grid, 3 Lanes (Top, Mid, Bot).                   " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║ " + ConsoleColors.YELLOW + "MOVE:    " + ConsoleColors.RESET + "Adjacent tiles (N/S/E/W). No Diagonals.              " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║ " + ConsoleColors.YELLOW + "ATTACK:  " + ConsoleColors.RESET + "Range includes diagonals.                            " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║ " + ConsoleColors.YELLOW + "TERRAIN: " + ConsoleColors.RESET + "Bush(+Dex), Cave(+Agi), Koulou(+Str).                " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║ " + ConsoleColors.YELLOW + "SPAWN:   " + ConsoleColors.RESET + "New monsters spawn every 8 rounds.                   " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "║ " + ConsoleColors.YELLOW + "ACTIONS: " + ConsoleColors.RESET + "Move, Attack, Teleport, Recall.                      " + ConsoleColors.RED + "║" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "╚═══════════════════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        System.out.println();
    }
}
