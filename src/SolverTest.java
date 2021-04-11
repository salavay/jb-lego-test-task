import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tester of {@link Solver}
 * There are two types of test - correct and incorrect.
 * Checker does the simple realisation of what in task said
 */
class SolverTest {

    /**
     * Path to test file
     */
    private final static Path TEST_FILE_PATH = Path.of("src/input.txt");
    /**
     * System-depended new line separator
     */
    private final String NEW_LINE_SEPARATOR = System.lineSeparator();
    /**
     * Size of alphabet
     */
    private final static int ALPHABET_SIZE = 26;
    /**
     * Alphabet
     */
    private final ArrayList<Character> alphabet = new ArrayList<>(List.of(
            'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p',
            'q', 'r', 's', 't',
            'u', 'v', 'w', 'x',
            'y', 'z'));


    /**
     * Indices which uses in comparator.
     * Index there means where letter is in alphabet
     */
    private final int[] indices = new int[ALPHABET_SIZE];
    /**
     * Comparator that uses for fast compare letters in alphabet
     *
     * @see #indices
     */
    private final Comparator<String> stringComparator = (s1, s2) -> {
        for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
            int a = charToInt(s1.charAt(i)),
                    b = charToInt(s2.charAt(i));
            if (a != b) {
                if (indices[a] < indices[b]) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return Integer.compare(s1.length(), s2.length());
    };

    /**
     * Small possible test
     * Helps to check correct lengths of string
     */
    @Test
    public void SmallPossibleTest() {
        final List<String> names = preparePossibleTest(10, 1, 10);
        assertEquals(0, check(Solver.testSolve(TEST_FILE_PATH), names));
    }

    /**
     * Large possible test.
     * Max ranges of string's length.
     * Max number od strings.
     */
    @Test
    public void LargePossibleTest() {
        final List<String> names = preparePossibleTest(100, 50, 100);
        assertEquals(0, check(Solver.testSolve(TEST_FILE_PATH), names));
    }


    /**
     * Small impossible test
     * Helps to check correct lengths of string
     */
    @Test
    public void SmallImpossibleTest() {
        final List<String> names = prepareImpossibleTest(10, 1, 10);
        assertEquals(1, check(Solver.testSolve(TEST_FILE_PATH), names));
    }

    /**
     * Large impossible test.
     * Max ranges of string's length.
     * Max number od strings.
     */
    @Test
    public void LargeImpossibleTest() {
        final List<String> names = prepareImpossibleTest(100, 4, 100);
        assertEquals(1, check(Solver.testSolve(TEST_FILE_PATH), names));
    }


    /**
     * Test without generation test file.
     * Uses already created test's data.
     */
    public void TestFromExistingFile() {
        System.out.println(Solver.testSolve(TEST_FILE_PATH));
    }

    /**
     * Create possible test with given parameters.
     *
     * @param n number of strings
     * @param l minimal length of string
     * @param r maximal length of string
     * @return Test data, where first line - number of string, then n lines of strings.
     */
    private List<String> preparePossibleTest(final int n, final int l, final int r) {
        // creating test file
        if (TEST_FILE_PATH.getParent() != null) {
            try {
                Files.createDirectories(TEST_FILE_PATH.getParent());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        final List<String> names = new ArrayList<>();
        try (final BufferedWriter bufferedWriter = Files.newBufferedWriter(TEST_FILE_PATH)) {
            // writing N to test file
            bufferedWriter.write(Integer.toString(n));
            bufferedWriter.write(NEW_LINE_SEPARATOR);
            // generating random string
            generateNames(n, names, l, r);
            // shuffle alphabet
            // generating indices based on shuffled alphabet
            generateAlphabet();
            // sorting strings in ascending order by shuffled alphabet
            names.sort(stringComparator);
            // writing string to the test file
            for (final String name : names) {
                bufferedWriter.write(name);
                bufferedWriter.write(NEW_LINE_SEPARATOR);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * Shuffle alphabet using {@link Collections#shuffle(List)}
     */
    private void generateAlphabet() {
        Collections.shuffle(alphabet);
        getIndices(charsListToString(alphabet));
    }

    /**
     * Creating {@code indices} from alphabet.
     * Length of {@link String}  view of alphabet must be equal {@link #ALPHABET_SIZE}.
     *
     * @param alphabetStringView {@link String} view of alphabet
     */
    private void getIndices(final String alphabetStringView) {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            indices[charToInt(alphabetStringView.charAt(i))] = i;
        }
    }

    /**
     * Generates n random strings with length in [l, r].
     *
     * @param n     number of strings
     * @param names {@link List} where names would be collected
     * @param l     minimal length of string
     * @param r     maximal length of string
     */
    private void generateNames(final int n, final List<String> names, final int l, final int r) {
        final Random random = new Random();
        for (int i = 0; i < n; i++) {
            final char[] string = new char[random.nextInt(r - l + 1) + l];
            for (int c = 0; c < string.length; c++) {
                final int ind = random.nextInt(ALPHABET_SIZE);
                string[c] = alphabet.get(ind);
            }
            names.add(new String(string));
        }
    }

    /**
     * Return index of char starts from letter 'a'.
     *
     * @param a char which would be converted
     * @return index of char starts from letter 'a'
     */
    private static int charToInt(final char a) {
        return a - 'a';
    }

    /**
     * Return {@link String} view of {@link List<Character>} without any delimiter
     *
     * @param list list which would be converted to {@link String}
     * @return {@link String} view of given list
     */
    private String charsListToString(final List<Character> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining());
    }

    /**
     * Method uses to check answer for test.
     * Return 1 - if test data for impossible test
     * 0 - if for correct
     *
     * @param ans   answer that needs to check
     * @param names test data
     * @return 1 - if test data for impossible test and 0 - if for correct one.
     */
    private int check(final String ans, final List<String> names) {
        if (ans.equals("Impossible")) {
            return 1;
        }
        getIndices(ans);
        final int n = names.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (stringComparator.compare(names.get(i), names.get(j)) > 0) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * Create impossible test with given parameters.
     * Uses result of {@link #preparePossibleTest(int, int, int)} and makes it incorrect.
     *
     * @param n number of strings
     * @param l minimal length of string
     * @param r maximal length of string
     * @return Test data, where first line - number of string, then n lines of strings.
     * @see #preparePossibleTest(int, int, int)
     */
    private List<String> prepareImpossibleTest(final int n, final int l, final int r) {
        final List<String> names = preparePossibleTest(n, l, r);
        Collections.swap(names, 0, names.size() - 1);
        return names;
    }
}