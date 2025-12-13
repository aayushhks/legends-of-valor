package board;

/**
 * Enum defining the specific types of terrain found on the game board.
 * Encapsulates the visual symbol and ANSI color codes for each type.
 */
public enum CellType {
    // ANSI Color Codes
    COMMON(" - ", "\u001B[90m"),      // Dark Grey (Plain)
    INACCESSIBLE(" X ", "\u001B[31m"), // Red (Wall)
    MARKET(" M ", "\u001B[33;1m"),    // Bright Yellow/Gold (Market/Shop)

    // Legends of Valor Specific
    NEXUS(" N ", "\u001B[35m"),       // Purple (Base/Nexus)
    BUSH(" B ", "\u001B[32m"),        // Green (Dexterity Boost)
    CAVE(" C ", "\u001B[33m"),        // Yellow (Agility Boost)
    KOULOU(" K ", "\u001B[34m"),      // Blue (Strength Boost)
    OBSTACLE(" O ", "\u001B[37;1m");  // White Bold (Obstacle)

    private final String symbol;
    private final String colorCode;
    private static final String RESET = "\u001B[0m";

    CellType(String symbol, String colorCode) {
        this.symbol = symbol;
        this.colorCode = colorCode;
    }

    public String getSymbol() {
        return colorCode + symbol + RESET;
    }
}