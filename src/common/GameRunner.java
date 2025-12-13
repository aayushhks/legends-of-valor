package common;

import game.LegendsGame;
import game.ValorGame;
import java.util.Scanner;

/**
 * Specialized class responsible for bootstrapping the game application.
 * Encapsulates the execution logic and global error handling strategies.
 */
public class GameRunner {

    // ANSI Colors for Console
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE_BOLD = "\033[1;37m";

    /**
     * Safely starts the game loop.
     * Any unhandled exceptions during the game's lifecycle will be caught here.
     */
    public static void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            printWelcomeBanner();

            System.out.println(ANSI_CYAN + "      Select Your Destiny:" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "  ╔════════════════════════════════════════════╗" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "  ║ " + ANSI_WHITE_BOLD + "1. Legends: Monsters and Heroes (RPG)      " + ANSI_PURPLE + "║" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "  ║ " + ANSI_WHITE_BOLD + "2. Legends of Valor (MOBA Strategy)        " + ANSI_PURPLE + "║" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "  ╚════════════════════════════════════════════╝" + ANSI_RESET);
            System.out.println();

            int choice = InputValidator.getValidInt(scanner, ANSI_YELLOW + "Choose Game Mode: " + ANSI_RESET, 1, 2);

            if (choice == 1) {
                printLegendsRules();
                new LegendsGame().play(scanner);
            } else {
                printValorRules();
                new ValorGame().play(scanner);
            }

        } catch (Exception e) {
            ErrorHandler.handleFatalError(e);
        }
    }

    private static void printWelcomeBanner() {
        System.out.println(ANSI_BLUE + "=================================================" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "   __                                 _     " + ANSI_RESET);
        System.out.println(ANSI_CYAN + "  / /  ___  __ _  ___ _ __   __| |___  " + ANSI_RESET);
        System.out.println(ANSI_CYAN + " / /  / _ \\/ _` |/ _ \\ '_ \\ / _` / __| " + ANSI_RESET);
        System.out.println(ANSI_CYAN + "/ /__|  __/ (_| |  __/ | | | (_| \\__ \\ " + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\\____/\\___|\\__, |\\___|_| |_|\\__,_|___/ " + ANSI_RESET);
        System.out.println(ANSI_CYAN + "           |___/                       " + ANSI_RESET);
        System.out.println(ANSI_BLUE + "             A R C H I V E             " + ANSI_RESET);
        System.out.println(ANSI_BLUE + "=================================================" + ANSI_RESET);
        System.out.println();
    }

    private static void printLegendsRules() {
        // Box Width: 63 Inner Characters
        System.out.println("\n" + ANSI_GREEN + "╔═══════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "║" + ANSI_WHITE_BOLD + "            LEGENDS: MONSTERS AND HEROES RULES                 " + ANSI_GREEN + "║" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "╠═══════════════════════════════════════════════════════════════╣" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "║ " + ANSI_YELLOW + "GOAL:    " + ANSI_RESET + "Defeat all monsters to advance.                      " + ANSI_GREEN + "║" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "║ " + ANSI_YELLOW + "COMBAT:  " + ANSI_RESET + "Turn-based battles with a party of heroes.           " + ANSI_GREEN + "║" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "║ " + ANSI_YELLOW + "MARKET:  " + ANSI_RESET + "Buy weapons, armor, potions, and spells.             " + ANSI_GREEN + "║" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "║ " + ANSI_YELLOW + "GROWTH:  " + ANSI_RESET + "Gain XP and Gold to level up stats.                  " + ANSI_GREEN + "║" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "║ " + ANSI_YELLOW + "DEFEAT:  " + ANSI_RESET + "Game Over if all heroes faint.                       " + ANSI_GREEN + "║" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "╚═══════════════════════════════════════════════════════════════╝" + ANSI_RESET);
        System.out.println();
    }

    private static void printValorRules() {
        // Box Width: 63 Inner Characters
        System.out.println("\n" + ANSI_RED + "╔═══════════════════════════════════════════════════════════════╗" + ANSI_RESET);
        System.out.println(ANSI_RED + "║" + ANSI_WHITE_BOLD + "                  LEGENDS OF VALOR RULES                       " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "╠═══════════════════════════════════════════════════════════════╣" + ANSI_RESET);
        System.out.println(ANSI_RED + "║ " + ANSI_YELLOW + "WIN:     " + ANSI_RESET + "Move a Hero to the Monsters' Nexus (Row 0).          " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "║ " + ANSI_YELLOW + "LOSE:    " + ANSI_RESET + "If a Monster reaches your Nexus (Row 7).             " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "║ " + ANSI_YELLOW + "BOARD:   " + ANSI_RESET + "8x8 Grid, 3 Lanes (Top, Mid, Bot).                   " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "║ " + ANSI_YELLOW + "MOVE:    " + ANSI_RESET + "Adjacent tiles (N/S/E/W). No Diagonals.              " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "║ " + ANSI_YELLOW + "ATTACK:  " + ANSI_RESET + "Range includes diagonals.                            " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "║ " + ANSI_YELLOW + "TERRAIN: " + ANSI_RESET + "Bush(+Dex), Cave(+Agi), Koulou(+Str).                " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "║ " + ANSI_YELLOW + "SPAWN:   " + ANSI_RESET + "New monsters spawn every 8 rounds.                   " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "║ " + ANSI_YELLOW + "ACTIONS: " + ANSI_RESET + "Move, Attack, Teleport, Recall.                      " + ANSI_RED + "║" + ANSI_RESET);
        System.out.println(ANSI_RED + "╚═══════════════════════════════════════════════════════════════╝" + ANSI_RESET);
        System.out.println();
    }
}