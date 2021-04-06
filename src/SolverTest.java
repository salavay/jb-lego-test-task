import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolverTest {

    private final static Path TEST_FILE_PATH = Path.of("src/input.txt");
    private final String NEW_LINE_SEPARATOR = System.lineSeparator();
    private final static int ALPHABET_SIZE = 26;
    private List<Character> alphabet = List.of(
            'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p',
            'q', 'r', 's', 't',
            'u', 'v', 'w', 'x',
            'y', 'z');


    private static final int MAX_STRING_LENGTH = 40;
    private static final int MIN_STRING_LENGTH = 2;

    private final int[] indices = new int[ALPHABET_SIZE];
    private final Comparator<String> stringComparator = (s1, s2) -> {
        for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
            int a = charToInt(s1.charAt(i)),
                    b = charToInt(s1.charAt(i));
            if (a != b) {
                if (indices[a] < indices[b]) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return 0;
    };

    @Test
    public void Test1() {
        List<String> names = preparePossibleTest(4);
        assertEquals(0, check(Solver.testSolve(TEST_FILE_PATH), names));
    }

    @Test
    public void Test2() {
        List<String> names = prepareImpossibleTest(40);
        assertEquals(1, check(Solver.testSolve(TEST_FILE_PATH), names));
    }

    private List<String> preparePossibleTest(int n) {
        if (TEST_FILE_PATH.getParent() != null) {
            try {
                Files.createDirectories(TEST_FILE_PATH.getParent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<String> names = new ArrayList<>();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(TEST_FILE_PATH)) {
            bufferedWriter.write(Integer.toString(n));
            bufferedWriter.write(NEW_LINE_SEPARATOR);
            generateNames(n, names);
            generateAlphabet();
            names.sort(stringComparator);
            for (String name : names) {
                bufferedWriter.write(name);
                bufferedWriter.write(NEW_LINE_SEPARATOR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    private void generateAlphabet() {
        if (alphabet instanceof ArrayList) {
            Collections.shuffle(alphabet);
        }
        getIndices(alphabet.stream().map(Object::toString).collect(Collectors.joining()));
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            indices[charToInt(alphabet.get(i))] = i;
        }
    }

    private void getIndices(String alphabetStringView) {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            indices[charToInt(alphabetStringView.charAt(i))] = i;
        }
    }

    private void generateNames(int n, List<String> names) {
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            char[] string = new char[random.nextInt(MAX_STRING_LENGTH - MIN_STRING_LENGTH) + MIN_STRING_LENGTH];
            for (int c = 0; c < string.length; c++) {
                int ind = random.nextInt(ALPHABET_SIZE);
                string[c] = alphabet.get(ind);
            }
            names.add(new String(string));
        }
    }


    private static int charToInt(char a) {
        return a - 'a';
    }

    private static char intToChar(int a) {
        return Character.toChars(a + 'a')[0];
    }

    private int check(String ans, List<String> names) {
        if (ans.equals("Impossible")) {
            return 1;
        }
        getIndices(ans);
        int n = names.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (stringComparator.compare(names.get(i), names.get(j)) > 0) {
                    return 1;
                }
            }
        }
        return 0;
    }

    private List<String> prepareImpossibleTest(int n) {
        List<String> names = preparePossibleTest(n);
        Collections.swap(names, 0, names.size() - 1);
        return names;
    }
}