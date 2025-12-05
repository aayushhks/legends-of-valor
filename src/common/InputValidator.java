package common;

import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Utility class for handling user input robustly.
 * Follows the "Don't Repeat Yourself" (DRY) principle by centralizing input logic.
 * Ensures the application never crashes due to InputMismatchExceptions.
 */
public class InputValidator {

    /**
     * Prompts the user for an integer within a specific range [min, max].
     * Loops until valid input is received.
     *
     * @param scanner The input scanner.
     * @param prompt  The message to display to the user.
     * @param min     The minimum acceptable value (inclusive).
     * @param max     The maximum acceptable value (inclusive).
     * @return A valid integer within the range.
     */
    public static int getValidInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    /**
     * Prompts the user for a non-empty string.
     *
     * @param scanner The input scanner.
     * @param prompt  The message to display.
     * @return A non-empty, trimmed string.
     */
    public static String getValidString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Prompts the user for a string that matches specific valid options (case-insensitive).
     * Useful for commands like "w/a/s/d" or "yes/no".
     *
     * @param scanner      The input scanner.
     * @param prompt       The message to display.
     * @param validOptions An array of valid strings (e.g., {"w", "a", "s", "d"}).
     * @return The valid string entered by the user (normalized to lowercase).
     */
    public static String getValidOption(Scanner scanner, String prompt, String... validOptions) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            for (String option : validOptions) {
                if (input.equals(option.toLowerCase())) {
                    return input;
                }
            }
            System.out.println("Invalid command. Accepted options: " + String.join(", ", validOptions));
        }
    }
}