package utils;

/**
 * Utility class containing ANSI color codes for console output.
 * Centralizes color definitions to eliminate code duplication across the codebase.
 */
public class ConsoleColors {
    
    // Reset
    public static final String RESET = "\u001B[0m";
    
    // Regular Colors
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    
    // Bold Colors
    public static final String WHITE_BOLD = "\033[1;37m";
    
    // Private constructor to prevent instantiation
    private ConsoleColors() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }
}