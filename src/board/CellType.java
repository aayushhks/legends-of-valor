package board;

/**
 * Enum defining the specific types of terrain found on the game board.
 * Encapsulates the visual symbol and ANSI color codes for each type.
 */
public enum CellType {
    // ANSI Color Codes
    COMMON(" . ", "\u001B[90m"),      // Dark Grey Dot
    MARKET(" M ", "\u001B[33;1m"),    // Bright Yellow/Gold
    INACCESSIBLE(" X ", "\u001B[31m");  // Red Hash blocks

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