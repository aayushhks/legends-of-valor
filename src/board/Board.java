package board;

/**
 * Abstract base class for a rectangular game board.
 * Encapsulates dimensions and boundary checking logic to prevent code duplication.
 */
public abstract class Board {
    protected final int width;
    protected final int height;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Validates if a specific coordinate exists within the board boundaries.
     * This is a utility method used by all subclasses to prevent IndexOutOfBounds exceptions.
     *
     * @param row The row index.
     * @param col The column index.
     * @return true if the coordinate is strictly within bounds.
     */
    public boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    /**
     * Forces subclasses to define how the board is visualized.
     */
    public abstract void printBoard();
}