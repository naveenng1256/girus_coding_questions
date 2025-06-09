import java.util.*;

public class SudokuValidator {
    
    private static final int BOARD_SIZE = 9;
    private static final int BOX_SIZE = 3;
    private static final int VALID_MASK = 0b1111111110; // Bits 1-9 set
    
    /**
     * Represents a custom zone with 9 cell positions
     */
    public static class CustomZone {
        private final List<int[]> cells;
        
        public CustomZone() {
            this.cells = new ArrayList<>();
        }
        
        public void addCell(int row, int col) {
            if (cells.size() >= 9) {
                throw new IllegalArgumentException("Zone cannot have more than 9 cells");
            }
            if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                throw new IllegalArgumentException("Invalid cell position: (" + row + ", " + col + ")");
            }
            cells.add(new int[]{row, col});
        }
        
        public List<int[]> getCells() {
            return new ArrayList<>(cells);
        }
        
        public boolean isComplete() {
            return cells.size() == 9;
        }
    }
    
    /**
     * Validates a complete Sudoku board with custom zones
     * 
     * @param board 9x9 integer array representing the Sudoku board
     * @param customZones List of custom zones to validate
     * @return true if the board is valid, false otherwise
     */
    public static boolean isValidSudoku(int[][] board, List<CustomZone> customZones) {
        if (board == null || board.length != BOARD_SIZE) {
            return false;
        }
        
        // Validate board dimensions and cell values
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == null || board[i].length != BOARD_SIZE) {
                return false;
            }
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] < 1 || board[i][j] > 9) {
                    return false;
                }
            }
        }
        
        // Validate standard Sudoku rules
        if (!validateStandardRules(board)) {
            return false;
        }
        
        // Validate custom zones
        if (customZones != null && !validateCustomZones(board, customZones)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates standard Sudoku rules (rows, columns, 3x3 boxes)
     * Uses bit manipulation for O(1) duplicate detection
     */
    private static boolean validateStandardRules(int[][] board) {
        // Validate rows and columns simultaneously
        for (int i = 0; i < BOARD_SIZE; i++) {
            int rowMask = 0;
            int colMask = 0;
            
            for (int j = 0; j < BOARD_SIZE; j++) {
                // Check row
                int rowBit = 1 << board[i][j];
                if ((rowMask & rowBit) != 0) {
                    return false; // Duplicate in row
                }
                rowMask |= rowBit;
                
                // Check column
                int colBit = 1 << board[j][i];
                if ((colMask & colBit) != 0) {
                    return false; // Duplicate in column
                }
                colMask |= colBit;
            }
        }
        
        // Validate 3x3 boxes
        for (int boxRow = 0; boxRow < BOX_SIZE; boxRow++) {
            for (int boxCol = 0; boxCol < BOX_SIZE; boxCol++) {
                int boxMask = 0;
                
                for (int i = boxRow * BOX_SIZE; i < (boxRow + 1) * BOX_SIZE; i++) {
                    for (int j = boxCol * BOX_SIZE; j < (boxCol + 1) * BOX_SIZE; j++) {
                        int bit = 1 << board[i][j];
                        if ((boxMask & bit) != 0) {
                            return false; // Duplicate in box
                        }
                        boxMask |= bit;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Validates custom zones using bit manipulation
     */
    private static boolean validateCustomZones(int[][] board, List<CustomZone> customZones) {
        for (CustomZone zone : customZones) {
            if (!zone.isComplete()) {
                return false; // Zone must have exactly 9 cells
            }
            
            int zoneMask = 0;
            for (int[] cell : zone.getCells()) {
                int row = cell[0];
                int col = cell[1];
                int bit = 1 << board[row][col];
                
                if ((zoneMask & bit) != 0) {
                    return false; // Duplicate in custom zone
                }
                zoneMask |= bit;
            }
            
            // Check if zone contains all digits 1-9
            if (zoneMask != VALID_MASK) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates a partially filled Sudoku board (with 0s for empty cells)
     * Useful for validating boards during solving process
     */
    public static boolean isValidPartialSudoku(int[][] board, List<CustomZone> customZones) {
        if (board == null || board.length != BOARD_SIZE) {
            return false;
        }
        
        // Validate board dimensions and cell values
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == null || board[i].length != BOARD_SIZE) {
                return false;
            }
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] < 0 || board[i][j] > 9) {
                    return false;
                }
            }
        }
        
        return validatePartialStandardRules(board) && 
               validatePartialCustomZones(board, customZones);
    }
    
    /**
     * Validates standard rules for partial board (ignoring empty cells)
     */
    private static boolean validatePartialStandardRules(int[][] board) {
        // Validate rows and columns
        for (int i = 0; i < BOARD_SIZE; i++) {
            int rowMask = 0;
            int colMask = 0;
            
            for (int j = 0; j < BOARD_SIZE; j++) {
                // Check row (skip empty cells)
                if (board[i][j] != 0) {
                    int rowBit = 1 << board[i][j];
                    if ((rowMask & rowBit) != 0) {
                        return false;
                    }
                    rowMask |= rowBit;
                }
                
                // Check column (skip empty cells)
                if (board[j][i] != 0) {
                    int colBit = 1 << board[j][i];
                    if ((colMask & colBit) != 0) {
                        return false;
                    }
                    colMask |= colBit;
                }
            }
        }
        
        // Validate 3x3 boxes
        for (int boxRow = 0; boxRow < BOX_SIZE; boxRow++) {
            for (int boxCol = 0; boxCol < BOX_SIZE; boxCol++) {
                int boxMask = 0;
                
                for (int i = boxRow * BOX_SIZE; i < (boxRow + 1) * BOX_SIZE; i++) {
                    for (int j = boxCol * BOX_SIZE; j < (boxCol + 1) * BOX_SIZE; j++) {
                        if (board[i][j] != 0) {
                            int bit = 1 << board[i][j];
                            if ((boxMask & bit) != 0) {
                                return false;
                            }
                            boxMask |= bit;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Validates custom zones for partial board
     */
    private static boolean validatePartialCustomZones(int[][] board, List<CustomZone> customZones) {
        if (customZones == null) {
            return true;
        }
        
        for (CustomZone zone : customZones) {
            if (!zone.isComplete()) {
                return false;
            }
            
            int zoneMask = 0;
            for (int[] cell : zone.getCells()) {
                int row = cell[0];
                int col = cell[1];
                
                if (board[row][col] != 0) {
                    int bit = 1 << board[row][col];
                    if ((zoneMask & bit) != 0) {
                        return false; // Duplicate in custom zone
                    }
                    zoneMask |= bit;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Utility method to print the Sudoku board
     */
    public static void printBoard(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("------+-------+------");
            }
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    /**
     * Test cases demonstrating the validator functionality
     */
    public static void main(String[] args) {
        System.out.println("=== Sudoku Validator with Custom Zones ===\n");
        
        // Test Case 1: Valid complete Sudoku
        System.out.println("Test Case 1: Valid Complete Sudoku");
        int[][] validBoard = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        
        printBoard(validBoard);
        
        // Create custom zones
        List<CustomZone> customZones = new ArrayList<>();
        
        // Custom Zone 1: Main diagonal
        CustomZone zone1 = new CustomZone();
        for (int i = 0; i < 9; i++) {
            zone1.addCell(i, i);
        }
        customZones.add(zone1);
        
        // Custom Zone 2: Anti-diagonal
        CustomZone zone2 = new CustomZone();
        for (int i = 0; i < 9; i++) {
            zone2.addCell(i, 8 - i);
        }
        customZones.add(zone2);
        
        boolean result1 = isValidSudoku(validBoard, customZones);
        System.out.println("Result: " + (result1 ? "VALID" : "INVALID"));
        System.out.println("Expected: VALID\n");
        
        // Test Case 2: Invalid Sudoku (duplicate in row)
        System.out.println("Test Case 2: Invalid Sudoku (duplicate in row)");
        int[][] invalidBoard = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 7} // Duplicate 7
        };
        
        boolean result2 = isValidSudoku(invalidBoard, customZones);
        System.out.println("Result: " + (result2 ? "VALID" : "INVALID"));
        System.out.println("Expected: INVALID\n");
        
        // Test Case 3: Valid partial Sudoku
        System.out.println("Test Case 3: Valid Partial Sudoku");
        int[][] partialBoard = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        
        printBoard(partialBoard);
        boolean result3 = isValidPartialSudoku(partialBoard, null);
        System.out.println("Result: " + (result3 ? "VALID" : "INVALID"));
        System.out.println("Expected: VALID\n");
        
        // Test Case 4: Performance test
        System.out.println("Test Case 4: Performance Test");
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 10000; i++) {
            isValidSudoku(validBoard, customZones);
        }
        
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
        System.out.println("Validated 10,000 boards in " + String.format("%.2f", duration) + " ms");
        System.out.println("Average time per validation: " + String.format("%.4f", duration / 10000) + " ms\n");
        
        // Test Case 5: Custom zone validation
        System.out.println("Test Case 5: Custom Zone Validation");
        
        // Create a board where standard rules pass but custom zone fails
        int[][] customZoneTest = {
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 8, 9, 1, 2, 3, 4, 5, 6},
            {2, 3, 1, 5, 6, 4, 8, 9, 7},
            {5, 6, 4, 8, 9, 7, 2, 3, 1},
            {8, 9, 7, 2, 3, 1, 5, 6, 4},
            {3, 1, 2, 6, 4, 5, 9, 7, 8},
            {6, 4, 5, 9, 7, 8, 3, 1, 2},
            {9, 7, 8, 3, 1, 2, 6, 4, 5}
        };
        
        // Custom zone that will fail (top-left corner repeated)
        CustomZone failingZone = new CustomZone();
        failingZone.addCell(0, 0); // 1
        failingZone.addCell(0, 1); // 2
        failingZone.addCell(0, 2); // 3
        failingZone.addCell(1, 0); // 4
        failingZone.addCell(1, 1); // 5
        failingZone.addCell(1, 2); // 6
        failingZone.addCell(2, 0); // 7
        failingZone.addCell(2, 1); // 8
        failingZone.addCell(2, 2); // 9 - This creates a valid zone
        
        List<CustomZone> failingZones = Arrays.asList(failingZone);
        boolean result5 = isValidSudoku(customZoneTest, failingZones);
        System.out.println("Standard rules + Valid custom zone: " + (result5 ? "VALID" : "INVALID"));
        System.out.println("Expected: VALID");
    }
}
