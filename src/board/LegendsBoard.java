package board;

import entities.Party;
import java.util.Random;

/**
 * Concrete implementation of the Board for Legends: Monsters and Heroes.
 * Manages the grid of Cells, procedural generation, and rendering.
 */
public class LegendsBoard extends Board {
    private final Cell[][] grid;
    private final Random random;
    private Party party;

    // Visual styling
    private static final String HERO_SYMBOL = " P ";
    private static final String ANSI_HERO_COLOR = "\u001B[36m"; // Cyan (to contrast with Purple grid)
    private static final String ANSI_BORDER_COLOR = "\u001B[35m"; // Purple
    private static final String ANSI_RESET = "\u001B[0m";

    public LegendsBoard(int n) {
        super(n, n);
        if (n < 4 || n > 20) {
            throw new IllegalArgumentException("Board size must be between 4 and 20.");
        }
        this.grid = new Cell[n][n];
        this.random = new Random();
        initializeBoard();
    }

    public void setParty(Party party) {
        this.party = party;
    }

    private void initializeBoard() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c] = createCell(r, c);
            }
        }
    }

    private Cell createCell(int r, int c) {
        // 1. START POSITION (0,0) -> Always Common
        if (r == 0 && c == 0) return new Cell(CellType.COMMON);

        // 2. SAFE ZONE: Ensure (0,1) AND (1,0) are never blocked
        // This allows movement Right (0,1) and Down (1,0) from start
        if ((r == 0 && c == 1) || (r == 1 && c == 0)) {
            return new Cell(CellType.COMMON);
        }

        // 3. RANDOM GENERATION
        double roll = random.nextDouble();
        if (roll < 0.20) return new Cell(CellType.INACCESSIBLE);
        else if (roll < 0.50) return new Cell(CellType.MARKET);
        else return new Cell(CellType.COMMON);
    }

    public Cell getCell(int row, int col) {
        if (!isValidCoordinate(row, col)) {
            throw new IndexOutOfBoundsException("Invalid coordinate: " + row + "," + col);
        }
        return grid[row][col];
    }

    @Override
    public void printBoard() {
        // Top Border
        printHorizontalBorder();

        for (int r = 0; r < height; r++) {
            // Left Border for the row
            System.out.print(ANSI_BORDER_COLOR + "|" + ANSI_RESET);

            for (int c = 0; c < width; c++) {
                // Render Logic
                if (party != null && party.getRow() == r && party.getCol() == c) {
                    System.out.print(ANSI_HERO_COLOR + HERO_SYMBOL + ANSI_RESET);
                } else {
                    System.out.print(grid[r][c].toString());
                }

                System.out.print(ANSI_BORDER_COLOR + "|" + ANSI_RESET); // Column separator
            }
            System.out.println(); // New line after row

            printHorizontalBorder();
        }
    }

    private void printHorizontalBorder() {
        System.out.print(ANSI_BORDER_COLOR + "+");
        for (int c = 0; c < width; c++) {
            System.out.print("---+");
        }
        System.out.println(ANSI_RESET);
    }
}