package common;

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
            System.out.println("==========================================");
            System.out.println("       WELCOME TO LEGENDS ARCHIVE         ");
            System.out.println("==========================================");
            System.out.println("1. Legends: Monsters and Heroes (RPG)");
            System.out.println("2. Legends of Valor (MOBA Strategy)");
            System.out.println("==========================================");

            int choice = InputValidator.getValidInt(scanner, "Choose Game Mode: ", 1, 2);

            if (choice == 1) {
                // Launch Original Game
                new LegendsGame().play(scanner);
            } else {
                // Launch New Game
                new ValorGame().play(scanner);
            }

        } catch (Exception e) {
            // Delegate critical failure handling to the dedicated ErrorHandler
            ErrorHandler.handleFatalError(e);
        }
    }
}