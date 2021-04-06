import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Solver {
    private static final int ALPHABET = 26;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(solve(scanner));
    }

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
        if (!getTopSort(g, ans)) {
            return "Impossible";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer c : ans) {
            sb.append(Character.toChars(c + 'a'));
        }
        return sb.toString();
    }

    private static boolean getTopSort(List<? extends Set<Integer>> g, List<Integer> ans) {
        int[] used = new int[ALPHABET];
        for (int i = 0; i < ALPHABET; i++) {
            if (used[i] == 0) {
                if (dfsHasCycle(i, used, g, ans)) {
                    return false;
                }
            }
        }
        Collections.reverse(ans);
        return true;
    }

    private static boolean dfsHasCycle(int v, int[] used, List<? extends Set<Integer>> g, List<Integer> ans) {
        used[v] = 1;
        for (Integer to : g.get(v)) {
            if (used[to] == 0 && dfsHasCycle(to, used, g, ans)) {
                return true;
            } else if (used[to] == 1) {
                return true;
            }
        }
        used[v] = 2;
        ans.add(v);
        return false;
    }

    // a < b
    private static boolean getEdges(String a, String b, List<? extends Set<Integer>> g) {
        for (int i = 0; i < Math.min(a.length(), b.length()); i++) {
            Integer v = (int) a.charAt(i) - 'a',
                    u = (int) b.charAt(i) - 'a';
            if (!v.equals(u)) {
                g.get(v).add(u);
                return false;
            }
        }
        return true;
    }
}
