package board;

/**
 * Represents a single tile on the game board.
 * Encapsulates the terrain type and logic for accessibility.
 */
public class Cell {
    private final CellType type;

    public Cell(CellType type) {
        this.type = type;
    }

    public CellType getType() {
        return type;
    }

    /**
     * Checks if the heroes can enter this cell.
     * @return true if the cell is not INACCESSIBLE.
     */
    public boolean isAccessible() {
        return type != CellType.INACCESSIBLE;
    }

    /**
     * Checks if this cell contains a market.
     * @return true if the cell is a MARKET.
     */
    public boolean isMarket() {
        return type == CellType.MARKET;
    }

    /**
     * Checks if this cell is a common battle area.
     * @return true if the cell is COMMON.
     */
    public boolean isCommon() {
        return type == CellType.COMMON;
    }

    @Override
    public String toString() {
        return type.getSymbol();
    }
}