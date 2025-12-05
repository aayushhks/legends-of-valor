package common;

import game.LegendsGame;
import java.util.Scanner;

/**
 * specialized class responsible for bootstrapping the game application.
 * Encapsulates the execution logic and global error handling strategies.
 */
public class GameRunner {

    /**
     * Safely starts the game loop.
     * Any unhandled exceptions during the game's lifecycle will be caught here.
     */
    public static void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            // Instantiate and play the specific game implementation
            new LegendsGame().play(scanner);
        } catch (Exception e) {
            // Delegate critical failure handling to the dedicated ErrorHandler
            ErrorHandler.handleFatalError(e);
        }
    }
}