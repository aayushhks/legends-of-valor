package board;

import java.util.Random;

/**
 * The specialized board for "Legends of Valor".
 * Creates a fixed 8x8 grid with 3 lanes, non-traversable barriers, and special terrain.
 */
public class ValorBoard extends Board {
    private final Cell[][] grid;
    private final Random random;

    // 3 Lanes configuration
    // Lane 1: Cols 0, 1
    // Wall 1: Col 2
    // Lane 2: Cols 3, 4
    // Wall 2: Col 5
    // Lane 3: Cols 6, 7

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
                // Monsters spawn at Top (Row 0), Heroes at Bottom (Row 7)
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
        // Pretty print the board with borders
        System.out.println("\n+-------+-------+-------+-------+-------+-------+-------+-------+");
        for (int r = 0; r < height; r++) {
            // Empty Padding Row
            System.out.print("|");
            for (int c = 0; c < width; c++) System.out.print("       |");
            System.out.println();

            // Content Row
            System.out.print("|");
            for (int c = 0; c < width; c++) {
                System.out.print("  " + grid[r][c].toString() + "  |");
            }
            System.out.println();

            // Empty Padding Row
            System.out.print("|");
            for (int c = 0; c < width; c++) System.out.print("       |");
            System.out.println();

            System.out.println("+-------+-------+-------+-------+-------+-------+-------+-------+");
        }
    }
}