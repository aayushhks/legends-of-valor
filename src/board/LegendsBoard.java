package board;

import utils.ConsoleColors;
import entities.Party;
import java.util.*;

/**
 * Concrete implementation of the Board for Legends: Monsters and Heroes.
 * Manages the grid of Cells, procedural generation, and rendering.
 */
public class LegendsBoard extends Board {
    private final Cell[][] grid;
    private final Random random;
    private Party party;

    // Visual styling
    private static final String HERO_SYMBOL = " P  "; // 4 chars to match Cell.toString()

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
        int attempts = 0;
        boolean isConnected = false;
        
        // Keep generating until we get a connected map (max 10 attempts)
        while (!isConnected && attempts < 10) {
            // Generate board with exact percentages
            generateBoardWithExactPercentages();
            
            // Check if all accessible cells are reachable from start position
            isConnected = verifyConnectivity();
            attempts++;
        }
        
        // If still not connected after 10 attempts, make a simple connected board
        if (!isConnected) {
            createSimpleConnectedBoard();
        }
    }
    
    private void generateBoardWithExactPercentages() {
        int totalCells = width * height;
        int targetInaccessible = Math.max(1, (int) Math.round(totalCells * 0.20)); // At least 1
        int targetMarket = (int) Math.round(totalCells * 0.30);
        int targetCommon = totalCells - targetInaccessible - targetMarket;
        
        // Create array of cell types with exact counts
        CellType[] cellTypes = new CellType[totalCells];
        int index = 0;
        
        // Fill with exact percentages
        for (int i = 0; i < targetInaccessible; i++) cellTypes[index++] = CellType.INACCESSIBLE;
        for (int i = 0; i < targetMarket; i++) cellTypes[index++] = CellType.MARKET;
        for (int i = 0; i < targetCommon; i++) cellTypes[index++] = CellType.COMMON;
        
        // Shuffle the array to randomize placement
        for (int i = cellTypes.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            CellType temp = cellTypes[i];
            cellTypes[i] = cellTypes[j];
            cellTypes[j] = temp;
        }
        
        // Place cells ensuring safe zones
        index = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (r == 0 && c == 0) {
                    // Starting position must be common
                    grid[r][c] = new Cell(CellType.COMMON);
                } else if ((r == 0 && c == 1) || (r == 1 && c == 0)) {
                    // Safe zones must be accessible (common or market)
                    CellType type = cellTypes[index];
                    if (type == CellType.INACCESSIBLE) {
                        grid[r][c] = new Cell(CellType.COMMON); // Force accessible
                    } else {
                        grid[r][c] = new Cell(type);
                    }
                    index++;
                } else {
                    grid[r][c] = new Cell(cellTypes[index++]);
                }
            }
        }
    }
    
    private Cell createCell(int r, int c) {
        // This method is now only used by the fallback
        if (r == 0 && c == 0) return new Cell(CellType.COMMON);
        if ((r == 0 && c == 1) || (r == 1 && c == 0)) {
            return new Cell(CellType.COMMON);
        }
        
        double roll = random.nextDouble();
        if (roll < 0.20) return new Cell(CellType.INACCESSIBLE);
        else if (roll < 0.50) return new Cell(CellType.MARKET);
        else return new Cell(CellType.COMMON);
    }
    
    private boolean verifyConnectivity() {
        boolean[][] visited = new boolean[height][width];
        int accessibleCount = 0;
        int reachableCount = 0;
        
        // Count total accessible cells
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (grid[r][c].isAccessible()) {
                    accessibleCount++;
                }
            }
        }
        
        // Flood fill from start position to count reachable cells
        reachableCount = floodFill(0, 0, visited);
        
        // Board is connected if all accessible cells are reachable
        return reachableCount == accessibleCount;
    }
    
    private int floodFill(int row, int col, boolean[][] visited) {
        // Boundary checks
        if (row < 0 || row >= height || col < 0 || col >= width) return 0;
        if (visited[row][col]) return 0;
        if (!grid[row][col].isAccessible()) return 0;
        
        // Mark as visited
        visited[row][col] = true;
        int count = 1;
        
        // Explore 4 directions (up, down, left, right)
        count += floodFill(row - 1, col, visited); // Up
        count += floodFill(row + 1, col, visited); // Down
        count += floodFill(row, col - 1, visited); // Left
        count += floodFill(row, col + 1, visited); // Right
        
        return count;
    }
    
    private void createSimpleConnectedBoard() {
        // Fallback: Create a simple connected board if random generation fails
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                // Create a simple pattern that ensures connectivity
                if (r == 0 || c == 0 || r == height - 1 || c == width - 1) {
                    // Border areas - mix of common and market
                    grid[r][c] = (r + c) % 3 == 0 ? new Cell(CellType.MARKET) : new Cell(CellType.COMMON);
                } else {
                    // Interior - mostly accessible with some strategic inaccessible tiles
                    if ((r + c) % 7 == 0) {
                        grid[r][c] = new Cell(CellType.INACCESSIBLE);
                    } else if ((r + c) % 4 == 0) {
                        grid[r][c] = new Cell(CellType.MARKET);
                    } else {
                        grid[r][c] = new Cell(CellType.COMMON);
                    }
                }
            }
        }
        // Ensure start position is always accessible
        grid[0][0] = new Cell(CellType.COMMON);
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
            System.out.print(ConsoleColors.PURPLE + "|" + ConsoleColors.RESET);

            for (int c = 0; c < width; c++) {
                // Render Logic
                if (party != null && party.getRow() == r && party.getCol() == c) {
                    System.out.print(ConsoleColors.CYAN + HERO_SYMBOL + ConsoleColors.RESET);
                } else {
                    System.out.print(grid[r][c].toString());
                }

                System.out.print(ConsoleColors.PURPLE + "|" + ConsoleColors.RESET); // Column separator
            }
            System.out.println();

            printHorizontalBorder();
        }
    }

    private void printHorizontalBorder() {
        System.out.print(ConsoleColors.PURPLE + "+");
        for (int c = 0; c < width; c++) {
            System.out.print("----+"); // 4 dashes to match 4-character cell content
        }
        System.out.println(ConsoleColors.RESET);
    }
}
