import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Dictionary {

    // Config
    private static final String DICT_FILE = "dictionary.txt";

    // Props
    private List<String> fullWordList;
    private List<String> filteredList;
    private List<FrequencyEntry> letterFrequency;

    // Init
    public Dictionary() {
        this.init();
    }

    public Dictionary(String filePath) {
        this.init(filePath);
    }

    /**
     * Initialize the dictionary using the default wordlist, and determine 
     * current letter frequency.
     */
    public void init() {
        this.loadFrom(DICT_FILE);
        this.letterFrequency = this.determineLetterFrequency(this.filteredList);
    }

    /**
     * Initialize the dictionary using a custom wordlist, specified at the 
     * provided file path. Also determine current letter frequency.
     * @param filePath The path to a custom wordlist.
     */
    public void init(String filePath) {
        this.loadFrom(filePath);
    }
    
    /**
     * Populate the full word list with the contents of the specified file. This
     * also initializes the filtered word list using the same data.
     * @param filePath The path to a word list.
     * @throws RuntimeException When the file could not be read.
     */
    private void loadFrom(String filePath) {
        List<String> lines;
        try {
            Stream<String> stringStream = Files.lines(Paths.get(filePath));
            lines = stringStream.toList();
            stringStream.close(); // Required per Closable iface
        } catch (Exception e) {
            throw new RuntimeException("Failed to load dictionary: " + e.getMessage(), e);
        }

        this.fullWordList = new ArrayList<>();
        for (String l : lines) {
            this.fullWordList.add(l);
        }

        // Make second copy of word list for filtering
        this.filteredList = new ArrayList<>(this.fullWordList);
    }

    /**
     * Calculate the letter frequency for all letters in the provided wordlist
     * @param wordList A list<string> of words
     */
    private List<FrequencyEntry> determineLetterFrequency(List<String> wordList) {
        // Note: Technically case doesn't matter (which makes this case-sensitive)

        // Count number of times each letter appears in the list
        int totalLetters = 0;
        Map<Character, Integer> letterCounts = new HashMap<>();
        for (String word : wordList) {
            for (int i = 0; i < word.length(); i++) {
                Character letter = word.charAt(i);
                totalLetters++;

                // If this letter isn't in the map yet, then create an entry for it
                if (!letterCounts.containsKey(letter)) {
                    letterCounts.put(letter, 0);
                }

                // Increment the count for this letter
                letterCounts.put(letter, letterCounts.get(letter) + 1);
            }
        }

        // Compute frequency percentage for each letter
        List<FrequencyEntry> frequencyMapping = new ArrayList<>();
        final Integer total = totalLetters; // Required due to lambda grrr
        letterCounts.forEach((letter, count) -> {
            float frequency = count.floatValue() / total.floatValue();
            frequencyMapping.add(new FrequencyEntry(letter, frequency));
        });

        return frequencyMapping;
    }
}