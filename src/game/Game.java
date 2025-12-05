package game;

import java.util.Scanner;

/**
 * Abstract Game Controller.
 * Uses the Template Method Design Pattern to define the immutable structure of a game cycle.
 *
 * <p>Responsibility: Orchestrate the high-level game flow (Setup -> Loop -> Teardown).</p>
 */
public abstract class Game {

    /**
     * The Template Method. This defines the algorithm for playing the game.
     * It is 'final' to prevent subclasses from altering the structural flow.
     *
     * @param scanner Shared scanner resource for input.
     */
    public final void play(Scanner scanner) {
        System.out.println("Initializing Game Engine...");

        // Configuration & Setup
        initializeGame(scanner);

        // Main Game Loop
        boolean isRunning = true;
        while (isRunning) {
            // Check for game over conditions before the turn
            if (isGameOver()) {
                System.out.println("Game Over Condition Met.");
                break;
            }

            // Execute a single turn
            processTurn(scanner);

            // Check if user quit or game ended during the turn
            if (shouldQuit()) {
                break;
            }
        }

        // Cleanup & Final Stats
        endGame();
    }

    // --- Abstract Hooks (To be implemented by LegendsGame) ---

    /**
     * Handles initial setup: creating the board, selecting heroes, loading data.
     */
    protected abstract void initializeGame(Scanner scanner);

    /**
     * Executes the logic for a single turn (Hero move, combat, etc.).
     */
    protected abstract void processTurn(Scanner scanner);

    /**
     * Checks if the game has reached a natural conclusion (Win/Loss).
     * @return true if the game is over.
     */
    protected abstract boolean isGameOver();

    /**
     * Checks if the user has requested to exit the application.
     * @return true if the game should terminate early.
     */
    protected abstract boolean shouldQuit();

    /**
     * Displays final score, goodbye messages, or cleanup logic.
     */
    protected abstract void endGame();
}