package common;

import utils.ConsoleColors;
import java.util.Scanner;

/**
 * Abstract base class for providing game information and instructions.
 * Uses the Strategy pattern to allow different games to provide their own info.
 */
public abstract class GameInfo {
    
    /**
     * Template method that displays complete game information.
     * Follows a consistent format while allowing customization.
     */
    public final void displayInfo() {
        printHeader();
        printObjective();
        printGameplay();
        printControls();
        printTips();
        printFooter();
    }
    
    protected abstract String getGameTitle();
    protected abstract String getHeaderColor();
    protected abstract void printObjective();
    protected abstract void printGameplay();
    protected abstract void printControls();
    protected abstract void printTips();
    
    // Helper method to ensure consistent line width
    protected void printBoxLine(String content) {
        String color = getHeaderColor();
        int boxWidth = 70; // Total content width between borders
        
        // Remove ALL ANSI color codes to calculate actual visible text length
        String visibleContent = content.replaceAll("\u001B\\[[0-9;]*[a-zA-Z]", "");
        
        // Calculate how many spaces we need to add
        int visibleLength = visibleContent.length();
        int spacesToAdd = boxWidth - visibleLength;
        
        // If content is too long, truncate it
        if (visibleLength > boxWidth) {
            // Find where to cut while preserving color codes at start
            content = content.substring(0, boxWidth);
            spacesToAdd = 0;
        }
        
        // Add exact number of spaces needed (Java 8 compatible)
        StringBuilder paddingBuilder = new StringBuilder();
        for (int i = 0; i < Math.max(0, spacesToAdd); i++) {
            paddingBuilder.append(" ");
        }
        String padding = paddingBuilder.toString();
        
        System.out.println(color + "║ " + content + padding + " " + color + "║" + ConsoleColors.RESET);
    }
    
    private void printHeader() {
        String color = getHeaderColor();
        String title = getGameTitle();
        int boxWidth = 70;
        
        // Calculate centering for the title
        int visibleTitleLength = title.length(); // No color codes in title itself
        int leftPadding = (boxWidth - visibleTitleLength) / 2;
        int rightPadding = boxWidth - visibleTitleLength - leftPadding;
        
        // Build centered title line
        StringBuilder centeredTitle = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) {
            centeredTitle.append(" ");
        }
        centeredTitle.append(ConsoleColors.WHITE_BOLD).append(title).append(ConsoleColors.RESET);
        for (int i = 0; i < rightPadding; i++) {
            centeredTitle.append(" ");
        }
        
        System.out.println("\n" + color + "╔════════════════════════════════════════════════════════════════════════╗" + ConsoleColors.RESET);
        System.out.println(color + "║ " + centeredTitle.toString() + " " + color + "║" + ConsoleColors.RESET);
        System.out.println(color + "╠════════════════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
    }
    
    private void printFooter() {
        String color = getHeaderColor();
        System.out.println(color + "╚════════════════════════════════════════════════════════════════════════╝" + ConsoleColors.RESET);
        Scanner scanner = new Scanner(System.in);
        System.out.print(ConsoleColors.YELLOW + "Press Enter to return to main menu..." + ConsoleColors.RESET);
        scanner.nextLine();
    }
}