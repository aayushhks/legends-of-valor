package board;

import java.util.Random;

/**
 * The specialized board for "Legends of Valor".
 * Creates a fixed 8x8 grid with 3 lanes, non-traversable barriers, and special terrain.
 */
public class ValorBoard extends Board {
    private final Cell[][] grid;
    private final Random random;

    // ANSI Colors
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    public ValorBoard() {
        super(8, 8); // Fixed 8x8 size
        this.grid = new Cell[8][8];
        this.random = new Random();
        initializeBoard();
    }

    private void initializeBoard() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                // 1. Create Walls (Cols 2 and 5)
                if (c == 2 || c == 5) {
                    grid[r][c] = new Cell(CellType.INACCESSIBLE);
                    continue;
                }

                // 2. Create Nexuses (Row 0 and Row 7)
                if (r == 0 || r == 7) {
                    grid[r][c] = new Cell(CellType.NEXUS);
                    continue;
                }

                // 3. Create Random Terrain for the rest
                // 40% Plain, 20% Bush, 20% Cave, 20% Koulou
                double roll = random.nextDouble();
                if (roll < 0.40) {
                    grid[r][c] = new Cell(CellType.COMMON);
                } else if (roll < 0.60) {
                    grid[r][c] = new Cell(CellType.BUSH);
                } else if (roll < 0.80) {
                    grid[r][c] = new Cell(CellType.CAVE);
                } else {
                    grid[r][c] = new Cell(CellType.KOULOU);
                }
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (!isValidCoordinate(row, col)) return null;
        return grid[row][col];
    }

    @Override
    public void printBoard() {
        // Standard width for a cell to align nicely
        // Using +-------+ format (7 chars wide)

        System.out.println("\n   L-0     L-0     W-1     L-1     L-1     W-2     L-2     L-2   ");
        printHorizontalDivider();

        for (int r = 0; r < height; r++) {
            // Row Content
            System.out.print("|");
            for (int c = 0; c < width; c++) {
                String cellStr = grid[r][c].toString();

                // Formatting: The Cell.toString() returns specific widths based on content
                // We strip ANSI codes for length calculation if necessary, but here we assume standard formatting.
                // If Cell returns "| H1 |" (6 chars), we pad it.
                // If Cell returns " - " (ANSI + 3 chars), we pad it differently.

                // Simple centering logic:
                if (cellStr.contains("|")) {
                    // It's an occupant string like | H1 |
                    System.out.print(cellStr + " |");
                } else {
                    // It's a terrain string like " - "
                    System.out.print("  " + cellStr + "  |");
                }
            }
            System.out.println(); // End of row content

            printHorizontalDivider();
        }
    }

    private void printHorizontalDivider() {
        System.out.print("+");
        for (int c = 0; c < width; c++) {
            System.out.print("-------+");
        }
        System.out.println();
    }
}