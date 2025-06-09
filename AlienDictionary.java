import java.util.*;

public class AlienDictionary {

    public static String findAlienOrder(String[] words) {
        // Handle edge cases
        if (words == null || words.length == 0) {
            return "";
        }
        
        if (words.length == 1) {
            return getUniqueChars(words[0]);
        }
        
        // Step 1: Create a graph to track which letter comes before which
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> incomingEdges = new HashMap<>();
        
        // Initialize all characters
        for (String word : words) {
            for (char c : word.toCharArray()) {
                graph.put(c, new HashSet<>());
                incomingEdges.put(c, 0);
            }
        }
        
        // Step 2: Compare each pair of adjacent words to build relationships
        for (int i = 0; i < words.length - 1; i++) {
            String first = words[i];
            String second = words[i + 1];
            
            // Check for impossible case: "apple" before "app" is wrong
            if (first.length() > second.length() && first.startsWith(second)) {
                return ""; // This ordering is impossible
            }
            
            // Find the first different character
            int minLength = Math.min(first.length(), second.length());
            for (int j = 0; j < minLength; j++) {
                char firstChar = first.charAt(j);
                char secondChar = second.charAt(j);
                
                if (firstChar != secondChar) {
                    // firstChar comes before secondChar in alien alphabet
                    if (!graph.get(firstChar).contains(secondChar)) {
                        graph.get(firstChar).add(secondChar);
                        incomingEdges.put(secondChar, incomingEdges.get(secondChar) + 1);
                    }
                    break; // Only look at first difference
                }
            }
        }
        
        // Step 3: Use topological sort to find the order
        return sortCharacters(graph, incomingEdges);
    }
    
    /**
     * Get unique characters from a word in order they appear
     */
    private static String getUniqueChars(String word) {
        StringBuilder result = new StringBuilder();
        Set<Character> seen = new HashSet<>();
        
        for (char c : word.toCharArray()) {
            if (!seen.contains(c)) {
                seen.add(c);
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * Sort characters based on their relationships using BFS
     * Think of it like: start with chars that don't depend on others,
     * then gradually add chars whose dependencies are satisfied
     */
    private static String sortCharacters(Map<Character, Set<Character>> graph, 
                                       Map<Character, Integer> incomingEdges) {
        Queue<Character> queue = new LinkedList<>();
        StringBuilder result = new StringBuilder();
        
        // Start with characters that have no dependencies (incoming edges = 0)
        for (char c : incomingEdges.keySet()) {
            if (incomingEdges.get(c) == 0) {
                queue.add(c);
            }
        }
        
        // Process characters one by one
        while (!queue.isEmpty()) {
            char current = queue.poll();
            result.append(current);
            
            // For each character that comes after current
            for (char next : graph.get(current)) {
                // Reduce its dependency count
                incomingEdges.put(next, incomingEdges.get(next) - 1);
                
                // If all dependencies satisfied, add to queue
                if (incomingEdges.get(next) == 0) {
                    queue.add(next);
                }
            }
        }
        
        // If we couldn't include all characters, there's a cycle (impossible order)
        if (result.length() != incomingEdges.size()) {
            return "";
        }
        
        return result.toString();
    }
    
    /**
     * Test if words are correctly ordered according to alien alphabet
     */
    public static boolean isCorrectOrder(String[] words, String alienOrder) {
        if (words == null || words.length <= 1) return true;
        
        // Create position map for alien characters
        Map<Character, Integer> position = new HashMap<>();
        for (int i = 0; i < alienOrder.length(); i++) {
            position.put(alienOrder.charAt(i), i);
        }
        
        // Check each adjacent pair
        for (int i = 0; i < words.length - 1; i++) {
            if (compareWords(words[i], words[i + 1], position) > 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Compare two words using alien alphabet order
     */
    private static int compareWords(String word1, String word2, Map<Character, Integer> position) {
        int len = Math.min(word1.length(), word2.length());
        
        for (int i = 0; i < len; i++) {
            char c1 = word1.charAt(i);
            char c2 = word2.charAt(i);
            
            int pos1 = position.getOrDefault(c1, -1);
            int pos2 = position.getOrDefault(c2, -1);
            
            if (pos1 != pos2) {
                return Integer.compare(pos1, pos2);
            }
        }
        
        return Integer.compare(word1.length(), word2.length());
    }
    
    public static void main(String[] args) {
        System.out.println("=== Alien Dictionary Solver ===\n");
        
        // Test 1: Basic example - figure out alien alphabet
        System.out.println("Test 1: Basic Alien Words");
        String[] words1 = {"wrt", "wrf", "er", "ett", "rftt"};
        System.out.println("Alien words: " + Arrays.toString(words1));
        String order1 = findAlienOrder(words1);
        System.out.println("Alien alphabet order: " + order1);
        System.out.println("Is this correct? " + isCorrectOrder(words1, order1));
        System.out.println("Explanation: w comes before e, e before r, r before t, t before f\n");
        
        // Test 2: Simple case
        System.out.println("Test 2: Simple Case");
        String[] words2 = {"z", "x"};
        System.out.println("Alien words: " + Arrays.toString(words2));
        String order2 = findAlienOrder(words2);
        System.out.println("Alien alphabet order: " + order2);
        System.out.println("Explanation: Since 'z' comes before 'x', alien order is zx\n");
        
        // Test 3: Impossible case - contradiction
        System.out.println("Test 3: Impossible Case (Contradiction)");
        String[] words3 = {"z", "x", "z"};
        System.out.println("Alien words: " + Arrays.toString(words3));
        String order3 = findAlienOrder(words3);
        System.out.println("Alien alphabet order: " + order3);
        System.out.println("Explanation: Can't have z<x AND x<z at same time - impossible!\n");
        
        // Test 4: Invalid - longer word before shorter prefix
        System.out.println("Test 4: Invalid Input");
        String[] words4 = {"apple", "app"};
        System.out.println("Alien words: " + Arrays.toString(words4));
        String order4 = findAlienOrder(words4);
        System.out.println("Alien alphabet order: " + order4);
        System.out.println("Explanation: 'apple' can't come before 'app' in any valid ordering\n");
        
        // Test 5: All different first letters
        System.out.println("Test 5: Different First Letters");
        String[] words5 = {"a", "b", "c", "d"};
        System.out.println("Alien words: " + Arrays.toString(words5));
        String order5 = findAlienOrder(words5);
        System.out.println("Alien alphabet order: " + order5);
        System.out.println("Explanation: Each word starts with different letter, so a<b<c<d\n");
        
        // Test 6: Same prefix, different endings
        System.out.println("Test 6: Same Prefix");
        String[] words6 = {"ab", "abc", "abcd"};
        System.out.println("Alien words: " + Arrays.toString(words6));
        String order6 = findAlienOrder(words6);
        System.out.println("Alien alphabet order: " + order6);
        System.out.println("Explanation: No contradictions, just shows a, b, c, d exist\n");
        
        // Test 7: Real-world example
        System.out.println("Test 7: More Complex Example");
        String[] words7 = {"ac", "ab", "zc", "zb"};
        System.out.println("Alien words: " + Arrays.toString(words7));
        String order7 = findAlienOrder(words7);
        System.out.println("Alien alphabet order: " + order7);
        System.out.println("Is this correct? " + isCorrectOrder(words7, order7));
        System.out.println("Explanation: a<z (from first comparison), c<b (from second comparison)\n");
        
        // Test 8: Single word
        System.out.println("Test 8: Single Word");
        String[] words8 = {"hello"};
        System.out.println("Alien words: " + Arrays.toString(words8));
        String order8 = findAlienOrder(words8);
        System.out.println("Alien alphabet order: " + order8);
        System.out.println("Explanation: Just unique letters from the word: h, e, l, o\n");
        
        System.out.println("=== Summary ===");
        System.out.println("The algorithm works by:");
        System.out.println("1. Comparing adjacent words to find letter relationships");
        System.out.println("2. Building a graph of 'letter A comes before letter B'");
        System.out.println("3. Sorting the letters based on these relationships");
        System.out.println("4. Returning empty string if contradictions found");
    }
}
