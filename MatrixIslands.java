import java.util.*;

public class MatrixIslands {

    public static int countIslands(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }
        
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int islandCount = 0;
        
        // Traverse each cell in the matrix
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // If we find an unvisited land cell (1), start DFS
                if (matrix[i][j] == 1 && !visited[i][j]) {
                    dfsExplore(matrix, visited, i, j, rows, cols);
                    islandCount++;
                }
            }
        }
        
        return islandCount;
    }
    
    /**
     * DFS helper method to explore all connected land cells
     * Explores in 8 directions: horizontal, vertical, and diagonal
     */
    private static void dfsExplore(int[][] matrix, boolean[][] visited, 
                                   int row, int col, int rows, int cols) {
        // Mark current cell as visited
        visited[row][col] = true;
        
        // 8 directions: up, down, left, right, and 4 diagonals
        int[] deltaRow = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] deltaCol = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        // Explore all 8 directions
        for (int i = 0; i < 8; i++) {
            int newRow = row + deltaRow[i];
            int newCol = col + deltaCol[i];
            
            // Check if the new position is valid and unvisited land
            if (isValidCell(newRow, newCol, rows, cols) && 
                matrix[newRow][newCol] == 1 && !visited[newRow][newCol]) {
                dfsExplore(matrix, visited, newRow, newCol, rows, cols);
            }
        }
    }
    
    /**
     * Check if a cell position is valid within matrix bounds
     */
    private static boolean isValidCell(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    
    /**
     * Alternative BFS implementation for counting islands
     * Uses iterative approach with queue instead of recursion
     */
    public static int countIslandsBFS(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }
        
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int islandCount = 0;
        
        // 8 directions for diagonal connections
        int[] deltaRow = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] deltaCol = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 1 && !visited[i][j]) {
                    // Start BFS from this cell
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j});
                    visited[i][j] = true;
                    
                    while (!queue.isEmpty()) {
                        int[] current = queue.poll();
                        int currentRow = current[0];
                        int currentCol = current[1];
                        
                        // Explore all 8 directions
                        for (int k = 0; k < 8; k++) {
                            int newRow = currentRow + deltaRow[k];
                            int newCol = currentCol + deltaCol[k];
                            
                            if (isValidCell(newRow, newCol, rows, cols) && 
                                matrix[newRow][newCol] == 1 && !visited[newRow][newCol]) {
                                visited[newRow][newCol] = true;
                                queue.offer(new int[]{newRow, newCol});
                            }
                        }
                    }
                    islandCount++;
                }
            }
        }
        
        return islandCount;
    }
    
    /**
     * Helper method to print matrix in a readable format
     */
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
    
    /**
     * Helper method to print matrix with island numbering for visualization
     */
    public static void printMatrixWithIslands(int[][] matrix) {
        if (matrix == null || matrix.length == 0) return;
        
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] islandMap = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        int islandNumber = 1;
        
        // Mark each island with a unique number
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 1 && !visited[i][j]) {
                    markIsland(matrix, visited, islandMap, i, j, rows, cols, islandNumber);
                    islandNumber++;
                }
            }
        }
        
        // Print the island map
        System.out.println("Island Map (0 = water, numbers = different islands):");
        for (int[] row : islandMap) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
    
    /**
     * Helper method to mark all cells of an island with the same number
     */
    private static void markIsland(int[][] matrix, boolean[][] visited, int[][] islandMap,
                                   int row, int col, int rows, int cols, int islandNumber) {
        visited[row][col] = true;
        islandMap[row][col] = islandNumber;
        
        int[] deltaRow = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] deltaCol = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        for (int i = 0; i < 8; i++) {
            int newRow = row + deltaRow[i];
            int newCol = col + deltaCol[i];
            
            if (isValidCell(newRow, newCol, rows, cols) && 
                matrix[newRow][newCol] == 1 && !visited[newRow][newCol]) {
                markIsland(matrix, visited, islandMap, newRow, newCol, rows, cols, islandNumber);
            }
        }
    }
    
    /**
     * Comprehensive test suite with various test cases
     */
    public static void runTests() {
        System.out.println("=== Matrix Islands with Diagonal Connections Tests ===\n");
        
        // Test Case 1: Simple 3x3 matrix
        System.out.println("Test Case 1: Simple 3x3 matrix");
        int[][] matrix1 = {
            {1, 1, 0},
            {0, 1, 0},
            {0, 0, 1}
        };
        testMatrix(matrix1);
        
        // Test Case 2: Diagonal connections
        System.out.println("\nTest Case 2: Diagonal connections");
        int[][] matrix2 = {
            {1, 0, 0, 1},
            {0, 1, 1, 0},
            {0, 1, 1, 0},
            {1, 0, 0, 1}
        };
        testMatrix(matrix2);
        
        // Test Case 3: All water
        System.out.println("\nTest Case 3: All water (no islands)");
        int[][] matrix3 = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };
        testMatrix(matrix3);
        
        // Test Case 4: All land (one big island)
        System.out.println("\nTest Case 4: All land (one big island)");
        int[][] matrix4 = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        };
        testMatrix(matrix4);
        
        // Test Case 5: Multiple separate islands
        System.out.println("\nTest Case 5: Multiple separate islands");
        int[][] matrix5 = {
            {1, 0, 1, 0, 1},
            {0, 0, 0, 0, 0},
            {1, 0, 1, 0, 1},
            {0, 0, 0, 0, 0},
            {1, 0, 1, 0, 1}
        };
        testMatrix(matrix5);
        
        // Test Case 6: Complex pattern with diagonal connections
        System.out.println("\nTest Case 6: Complex pattern with diagonal connections");
        int[][] matrix6 = {
            {1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1},
            {0, 0, 0, 1, 1}
        };
        testMatrix(matrix6);
        
        // Test Case 7: Single cell islands
        System.out.println("\nTest Case 7: Single cell islands");
        int[][] matrix7 = {
            {1, 0, 1},
            {0, 1, 0},
            {1, 0, 1}
        };
        testMatrix(matrix7);
        
        // Edge Cases
        System.out.println("\n=== Edge Cases ===");
        
        // Empty matrix
        System.out.println("Empty matrix: " + countIslands(new int[0][0]));
        
        // Single cell with land
        int[][] singleLand = {{1}};
        System.out.println("Single land cell: " + countIslands(singleLand));
        
        // Single cell with water
        int[][] singleWater = {{0}};
        System.out.println("Single water cell: " + countIslands(singleWater));
    }
    
    /**
     * Helper method to test a matrix and display results
     */
    private static void testMatrix(int[][] matrix) {
        System.out.println("Matrix:");
        printMatrix(matrix);
        
        int dfsResult = countIslands(matrix);
        int bfsResult = countIslandsBFS(matrix);
        
        System.out.println("Islands count (DFS): " + dfsResult);
        System.out.println("Islands count (BFS): " + bfsResult);
        System.out.println("Results match: " + (dfsResult == bfsResult));
        
        printMatrixWithIslands(matrix);
        System.out.println("-".repeat(50));
    }
    
    /**
     * Main method to demonstrate the solution
     */
    public static void main(String[] args) {
        runTests();
        
        System.out.println("\n=== Sample Input/Output Examples ===");
        
        // Example 1
        System.out.println("Example 1:");
        int[][] example1 = {
            {1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1},
            {0, 0, 0, 1, 1}
        };
        System.out.println("Input matrix:");
        printMatrix(example1);
        System.out.println("Output: " + countIslands(example1) + " islands");
        
        // Example 2
        System.out.println("\nExample 2:");
        int[][] example2 = {
            {1, 0, 1},
            {0, 1, 0},
            {1, 0, 1}
        };
        System.out.println("Input matrix:");
        printMatrix(example2);
        System.out.println("Output: " + countIslands(example2) + " islands");
        System.out.println("Note: Center cell connects all corner cells diagonally, forming 1 island");
    }
}
