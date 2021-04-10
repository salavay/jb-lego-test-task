import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Solver {
    /**
     * Length of using alphabet.
     */
    private static final int ALPHABET = 26;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(solve(scanner));
    }

    /**
     * This method use to test with path of testFile.
     * {@link SolverTest} uses this method.
     *
     * @param path path of file with test
     * @return return answer for task
     */
    public static String testSolve(Path path) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(Files.newInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert scanner != null;
        return solve(scanner);
    }

    /**
     * Main solving method of task.
     *
     * @param scanner input of test
     * @return answer for task
     */
    public static String solve(Scanner scanner) {
        int n = Integer.parseInt(scanner.nextLine());
        String[] names = new String[n];
        for (int i = 0; i < n; i++) {
            names[i] = scanner.nextLine();
        }
        List<Set<Integer>> g = new ArrayList<>(ALPHABET);
        for (int i = 0; i < ALPHABET; i++) {
            g.add(new HashSet<>());
        }
        /* Creating graph of partial order
         * Checking correct lengths of string-prefix.
         */
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                boolean isSame = getEdges(names[i], names[j], g);
                if (isSame &&
                        names[i].length() > names[j].length()) {
                    return "Impossible";
                }
            }
        }
        List<Integer> ans = new ArrayList<>();
        /*
         * Building topological sort.
         * If there is no that sort, the answer is "Impossible"
         */
        if (!getTopSort(g, ans)) {
            return "Impossible";
        }
        StringBuilder sb = new StringBuilder();
        /*
         * Printing answer for task - permutation of alphabet based on topological sort
         */
        for (Integer c : ans) {
            sb.append(Character.toChars(c + 'a'));
        }
        return sb.toString();
    }

    /**
     * Generating topological sort by Depth-first search.
     *
     * @param g   oriented graph
     * @param ans {@link List<Integer>} where topological sort will be put
     * @return topological sort. All edges goes to vertex with bigger index
     */
    private static boolean getTopSort(List<? extends Set<Integer>> g, List<Integer> ans) {
        int[] used = new int[ALPHABET];
        for (int i = 0; i < ALPHABET; i++) {
            if (used[i] == 0) {
                if (dfsHasCycle(i, used, g, ans)) {
                    return false;
                }
            }
        }
        // reverse topological sort to get ascending order of letters
        Collections.reverse(ans);
        return true;
    }

    /**
     * Depth-first search with checking existence of cycle and building topological sort.
     *
     * @param v    current vertex
     * @param used array of already visited vertexes
     * @param g    graph
     * @param ans  {@link List<Integer>} where topological sort will be put
     * @return is subtree has vertex in cycle
     */
    private static boolean dfsHasCycle(int v, int[] used, List<? extends Set<Integer>> g, List<Integer> ans) {
        used[v] = 1;
        for (Integer to : g.get(v)) {
            if (used[to] == 0 && dfsHasCycle(to, used, g, ans)) {
                // there is cycle in subtree of vertex to
                return true;
            } else if (used[to] == 1) {
                // find a cycle, cause vertex to isn't closed yet
                return true;
            }
        }
        used[v] = 2;
        ans.add(v);
        return false;
    }


    /**
     * @param a smaller string in context of given lexicographical order
     * @param b higher string in context of given lexicographical order
     * @param g graph where new edge may be added
     * @return is first given string - is a prefix of the second given string
     */
    private static boolean getEdges(String a, String b, List<? extends Set<Integer>> g) {
        for (int i = 0; i < Math.min(a.length(), b.length()); i++) {
            Integer v = (int) a.charAt(i) - 'a',
                    u = (int) b.charAt(i) - 'a';
            if (!v.equals(u)) {
                g.get(v).add(u);
                return false;
            }
        }
        // All letter are the same. That means that a - is a prefix of b
        return true;
    }
}
