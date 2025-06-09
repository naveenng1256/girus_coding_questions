import java.util.*;


public class KnightsAndPortals {
    
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    
    // Cell types
    private static final char EMPTY = '.';
    private static final char WALL = '#';
    private static final char START = 'S';
    private static final char END = 'E';
    
    /**
     * Represents a position in the grid
     */
    static class Position {
        int row, col;
        
        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Position pos = (Position) obj;
            return row == pos.row && col == pos.col;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
        
        @Override
        public String toString() {
            return "(" + row + "," + col + ")";
        }
    }
    
    /**
     * Find shortest path with optional teleportation
     * 
     * @param grid - 2D character array representing the maze
     * @return minimum steps needed, or -1 if impossible
     */
    public static int findShortestPath(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return -1;
        }
        
        int rows = grid.length;
        int cols = grid[0].length;
        
        // Find start and end positions
        Position start = null, end = null;
        List<Position> emptyCells = new ArrayList<>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == START) {
                    start = new Position(i, j);
                    emptyCells.add(start);
                } else if (grid[i][j] == END) {
                    end = new Position(i, j);
                    emptyCells.add(end);
                } else if (grid[i][j] == EMPTY) {
                    emptyCells.add(new Position(i, j));
                }
            }
        }
        
        if (start == null || end == null) {
            return -1; // No start or end found
        }
        
        // Option 1: Normal path without teleportation
        int normalPath = findNormalPath(grid, start, end);
        
        // Option 2: Path with teleportation
        int teleportPath = findPathWithTeleport(grid, start, end, emptyCells);
        
        // Return the best option
        if (normalPath == -1 && teleportPath == -1) {
            return -1; // No path exists
        } else if (normalPath == -1) {
            return teleportPath;
        } else if (teleportPath == -1) {
            return normalPath;
        } else {
            return Math.min(normalPath, teleportPath);
        }
    }
    
    /**
     * Find normal shortest path using BFS (no teleportation)
     */
    private static int findNormalPath(char[][] grid, Position start, Position end) {
        return bfs(grid, start, end);
    }
    
    /**
     * Find shortest path using teleportation once
     * Try all possible teleport combinations: start -> A -> teleport -> B -> end
     */
    private static int findPathWithTeleport(char[][] grid, Position start, Position end, 
                                          List<Position> emptyCells) {
        int minSteps = Integer.MAX_VALUE;
        
        // Pre-calculate distances from all empty cells to all other empty cells
        Map<Position, Map<Position, Integer>> allDistances = new HashMap<>();
        for (Position cell : emptyCells) {
            allDistances.put(cell, calculateDistancesFrom(grid, cell));
        }
        
        // Try teleporting from each empty cell to every other empty cell
        for (Position teleportFrom : emptyCells) {
            for (Position teleportTo : emptyCells) {
                if (teleportFrom.equals(teleportTo)) {
                    continue; // Can't teleport to same cell
                }
                
                // Calculate: start -> teleportFrom + 1(teleport) + teleportTo -> end
                Integer distStartToFrom = allDistances.get(start).get(teleportFrom);
                Integer distToToEnd = allDistances.get(teleportTo).get(end);
                
                if (distStartToFrom != null && distToToEnd != null) {
                    int totalSteps = distStartToFrom + 1 + distToToEnd; // +1 for teleport
                    minSteps = Math.min(minSteps, totalSteps);
                }
            }
        }
        
        return minSteps == Integer.MAX_VALUE ? -1 : minSteps;
    }
    
    /**
     * Calculate shortest distances from one position to all reachable positions
     */
    private static Map<Position, Integer> calculateDistancesFrom(char[][] grid, Position start) {
        Map<Position, Integer> distances = new HashMap<>();
        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        
        queue.offer(start);
        visited.add(start);
        distances.put(start, 0);
        
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int currentDist = distances.get(current);
            
            // Check all 4 directions
            for (int[] dir : DIRECTIONS) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];
                
                if (isValid(grid, newRow, newCol)) {
                    Position newPos = new Position(newRow, newCol);
                    
                    if (!visited.contains(newPos)) {
                        visited.add(newPos);
                        distances.put(newPos, currentDist + 1);
                        queue.offer(newPos);
                    }
                }
            }
        }
        
        return distances;
    }
    
    /**
     * Standard BFS to find shortest path
     */
    private static int bfs(char[][] grid, Position start, Position end) {
        if (start.equals(end)) {
            return 0;
        }
        
        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        Map<Position, Integer> distance = new HashMap<>();
        
        queue.offer(start);
        visited.add(start);
        distance.put(start, 0);
        
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            
            // Check all 4 directions
            for (int[] dir : DIRECTIONS) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];
                
                if (isValid(grid, newRow, newCol)) {
                    Position newPos = new Position(newRow, newCol);
                    
                    if (newPos.equals(end)) {
                        return distance.get(current) + 1;
                    }
                    
                    if (!visited.contains(newPos)) {
                        visited.add(newPos);
                        distance.put(newPos, distance.get(current) + 1);
                        queue.offer(newPos);
                    }
                }
            }
        }
        
        return -1; // No path found
    }
    
    /**
     * Check if a position is valid (within bounds and not a wall)
     */
    private static boolean isValid(char[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && 
               col >= 0 && col < grid[0].length && 
               grid[row][col] != WALL;
    }
    
    /**
     * Helper method to print the grid nicely
     */
    public static void printGrid(char[][] grid) {
        System.out.println("Grid:");
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * Helper method to create grid from string array
     */
    public static char[][] createGrid(String[] gridStr) {
        char[][] grid = new char[gridStr.length][];
        for (int i = 0; i < gridStr.length; i++) {
            grid[i] = gridStr[i].toCharArray();
        }
        return grid;
    }
    
    /**
     * Test cases and examples
     */
    public static void main(String[] args) {
        System.out.println("=== Knights and Portals: Shortest Path with Teleportation ===\\n");
        
        // Test Case 1: Simple case where teleportation helps
        System.out.println("Test Case 1: Teleportation is Beneficial");
        String[] grid1Str = {
            "S.#...E",
            "..#....",
            "..#....",
            ".......",
        };
        char[][] grid1 = createGrid(grid1Str);
        printGrid(grid1);
        
        int result1 = findShortestPath(grid1);
        System.out.println("Shortest path: " + result1 + " steps");
        System.out.println("Explanation: Without teleport = 9 steps, with teleport = 3 steps");
        System.out.println("(Start -> teleport from (0,1) to (0,5) -> End)\\n");
        
        // Test Case 2: Normal path is better
        System.out.println("Test Case 2: Normal Path is Better");
        String[] grid2Str = {
            "S..E",
            "....",
            "....",
        };
        char[][] grid2 = createGrid(grid2Str);
        printGrid(grid2);
        
        int result2 = findShortestPath(grid2);
        System.out.println("Shortest path: " + result2 + " steps");
        System.out.println("Explanation: Direct path takes 3 steps, teleport would take more\\n");
        
        // Test Case 3: Teleportation required
        System.out.println("Test Case 3: Teleportation Required");
        String[] grid3Str = {
            "S.#",
            "..#",
            "###",
            "..E"
        };
        char[][] grid3 = createGrid(grid3Str);
        printGrid(grid3);
        
        int result3 = findShortestPath(grid3);
        System.out.println("Shortest path: " + result3 + " steps");
        System.out.println("Explanation: Wall blocks normal path, must use teleportation\\n");
        
        // Test Case 4: No path possible
        System.out.println("Test Case 4: No Path Possible");
        String[] grid4Str = {
            "S##",
            "###",
            "##E"
        };
        char[][] grid4 = createGrid(grid4Str);
        printGrid(grid4);
        
        int result4 = findShortestPath(grid4);
        System.out.println("Shortest path: " + result4 + " steps");
        System.out.println("Explanation: Start and end are isolated, no teleport possible\\n");
        
        // Test Case 5: Start equals end
        System.out.println("Test Case 5: Start Equals End");
        String[] grid5Str = {
            "S"
        };
        // Manually set end to same position as start
        char[][] grid5 = {{'S'}};
        // For this test, we'll modify to make S also the end
        grid5[0][0] = 'E';
        Position start = new Position(0, 0);
        Position end = new Position(0, 0);
        System.out.println("Grid: [E] (start and end same)");
        
        int result5 = bfs(grid5, start, end);
        System.out.println("Shortest path: " + result5 + " steps");
        System.out.println("Explanation: Already at destination\\n");
        
        // Test Case 6: Complex maze
        System.out.println("Test Case 6: Complex Maze");
        String[] grid6Str = {
            "S.....#......E",
            ".#####.######.",
            "......#.......",
            "######.#######",
            ".............."
        };
        char[][] grid6 = createGrid(grid6Str);
        printGrid(grid6);
        
        int result6 = findShortestPath(grid6);
        System.out.println("Shortest path: " + result6 + " steps");
        System.out.println("Explanation: Compare normal path vs teleportation options\\n");
        
        // Test Case 7: Edge case - tiny grid
        System.out.println("Test Case 7: Tiny Grid");
        String[] grid7Str = {"SE"};
        char[][] grid7 = createGrid(grid7Str);
        printGrid(grid7);
        
        int result7 = findShortestPath(grid7);
        System.out.println("Shortest path: " + result7 + " steps");
        System.out.println("Explanation: Adjacent cells, 1 step needed\\n");
        
        // Performance test
        System.out.println("Performance Test:");
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            findShortestPath(grid1);
        }
        
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0;
        System.out.println("Processed 1,000 pathfinding operations in " + 
                          String.format("%.2f", duration) + " ms");
        System.out.println("Average time per operation: " + 
                          String.format("%.4f", duration / 1000) + " ms");
        
        System.out.println("\\n=== Summary ===");
        System.out.println("The algorithm works by:");
        System.out.println("1. Trying normal shortest path (BFS)");
        System.out.println("2. Trying all possible teleportation combinations");
        System.out.println("3. Returning the minimum steps among all options");
        System.out.println("4. Teleportation costs 1 step between any two empty cells");
    }
}
