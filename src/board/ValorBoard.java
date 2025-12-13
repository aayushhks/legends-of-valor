package board;

import java.util.Random;

public class ValorBoard extends Board {
    private final Cell[][] grid;
    private final Random random;

    public ValorBoard() {
        super(8, 8);
        this.grid = new Cell[8][8];
        this.random = new Random();
        initializeBoard();
    }

    private void initializeBoard() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (c == 2 || c == 5) {
                    grid[r][c] = new Cell(CellType.INACCESSIBLE);
                    continue;
                }
                if (r == 0 || r == 7) {
                    grid[r][c] = new Cell(CellType.NEXUS);
                    continue;
                }

                // Adjusted Probabilities for Obstacles
                double roll = random.nextDouble();
                if (roll < 0.20) grid[r][c] = new Cell(CellType.COMMON);
                else if (roll < 0.40) grid[r][c] = new Cell(CellType.BUSH);
                else if (roll < 0.60) grid[r][c] = new Cell(CellType.CAVE);
                else if (roll < 0.80) grid[r][c] = new Cell(CellType.KOULOU);
                else grid[r][c] = new Cell(CellType.OBSTACLE); // 20% Chance
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (!isValidCoordinate(row, col)) return null;
        return grid[row][col];
    }

    @Override
    public void printBoard() {
        System.out.println("\n   L-0     L-0     W-1     L-1     L-1     W-2     L-2     L-2   ");
        printHorizontalDivider();

        for (int r = 0; r < height; r++) {
            System.out.print("|"); // Start row
            for (int c = 0; c < width; c++) {
                // Uniform padding for everyone
                // "  " + 3-char-symbol + "  |" = 7 chars + border
                System.out.print("  " + grid[r][c].toString() + "  |");
            }
            System.out.println(); // End row
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