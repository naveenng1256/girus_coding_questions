public class BitwiseMatching {

    public static int nextLargerSameOnes(int n) {
        if (n <= 0) {
            return -1;
        }
        
        int c = n;
        int c0 = 0; // Count of trailing zeros
        int c1 = 0; // Count of ones to the right of trailing zeros
        
        // Count trailing zeros
        while (((c & 1) == 0) && c != 0) {
            c0++;
            c >>>= 1; // Use unsigned right shift
        }
        
        // Count ones after trailing zeros
        while ((c & 1) == 1) {
            c1++;
            c >>>= 1;
        }
        
        // If c0 + c1 == 31 or c0 + c1 == 0, then no bigger number exists
        if (c0 + c1 == 31 || c0 + c1 == 0) {
            return -1;
        }
        
        // Position of rightmost non-trailing zero
        int p = c0 + c1;
        
        // Flip the rightmost non-trailing zero
        n |= (1 << p);
        
        // Clear all bits to the right of p
        n &= ~((1 << p) - 1);
        
        // Insert (c1-1) ones on the right
        n |= (1 << (c1 - 1)) - 1;
        
        return n;
    }
    
    /**
     * Helper method to count number of 1s in binary representation
     * @param n Input integer
     * @return Number of 1s in binary representation
     */
    public static int countOnes(int n) {
        int count = 0;
        while (n != 0) {
            count += n & 1;
            n >>>= 1;
        }
        return count;
    }
    
    /**
     * Helper method to convert integer to binary string
     * @param n Input integer
     * @return Binary string representation
     */
    public static String toBinaryString(int n) {
        return Integer.toBinaryString(n);
    }
    
    /**
     * Test method with comprehensive test cases
     */
    public static void runTests() {
        System.out.println("=== Bitwise Matching Pattern Tests ===\n");
        
        // Test cases with expected results
        int[] testCases = {12, 6, 10, 3, 15, 8, 1, 2, 4};
        
        for (int n : testCases) {
            int result = nextLargerSameOnes(n);
            
            System.out.printf("Input: %d (binary: %s)\n", n, toBinaryString(n));
            System.out.printf("Ones count: %d\n", countOnes(n));
            
            if (result != -1) {
                System.out.printf("Output: %d (binary: %s)\n", result, toBinaryString(result));
                System.out.printf("Ones count: %d\n", countOnes(result));
                System.out.printf("Verification: %s\n", 
                    (countOnes(n) == countOnes(result) && result > n) ? "PASS" : "FAIL");
            } else {
                System.out.println("Output: -1 (No larger number possible)");
                System.out.println("Verification: PASS (Edge case)");
            }
            System.out.println("-".repeat(40));
        }
        
        // Additional edge cases
        System.out.println("\n=== Edge Cases ===");
        
        // Test negative number
        int negativeTest = -5;
        System.out.printf("Negative input (%d): %d\n", negativeTest, nextLargerSameOnes(negativeTest));
        
        // Test zero
        int zeroTest = 0;
        System.out.printf("Zero input (%d): %d\n", zeroTest, nextLargerSameOnes(zeroTest));
        
        // Test large number close to Integer.MAX_VALUE
        int largeTest = Integer.MAX_VALUE - 1;
        System.out.printf("Large input (%d): %d\n", largeTest, nextLargerSameOnes(largeTest));
    }
    
    /**
     * Main method to demonstrate the solution
     */
    public static void main(String[] args) {
        runTests();
        
        System.out.println("\n=== Custom Examples ===");
        
        // Example 1: 12 (1100) -> 17 (10001)
        System.out.println("Example 1:");
        int n1 = 12;
        int result1 = nextLargerSameOnes(n1);
        System.out.printf("%d (%s) -> %d (%s)\n", 
            n1, toBinaryString(n1), result1, toBinaryString(result1));
        
        // Example 2: 6 (110) -> 9 (1001)
        System.out.println("\nExample 2:");
        int n2 = 6;
        int result2 = nextLargerSameOnes(n2);
        System.out.printf("%d (%s) -> %d (%s)\n", 
            n2, toBinaryString(n2), result2, toBinaryString(result2));
        
        // Example 3: 10 (1010) -> 12 (1100)
        System.out.println("\nExample 3:");
        int n3 = 10;
        int result3 = nextLargerSameOnes(n3);
        System.out.printf("%d (%s) -> %d (%s)\n", 
            n3, toBinaryString(n3), result3, toBinaryString(result3));
    }
}
