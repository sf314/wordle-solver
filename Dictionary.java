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

    /**
     * Initialize the dictionary using the default 10k wordlist, and determine 
     * current letter frequency.
     */
    public Dictionary() {
        this.loadFrom(DICT_FILE);
        this.letterFrequency = this.determineLetterFrequency(this.filteredList);
    }

    /**
     * Initialize the dictionary using a custom wordlist, specified at the 
     * provided file path. Also determine current letter frequency.
     * @param filePath The path to a custom wordlist.
     */
    public Dictionary(String filePath) {
        this.loadFrom(filePath);
        this.letterFrequency = this.determineLetterFrequency(this.filteredList);
    }

    /**
     * Check if a word exists in the dictionary (full word list)
     * @param word
     * @return
     */
    public boolean contains(String word) {
        return this.fullWordList.contains(word);
    }

    /**
     * Get word at specified index
     * @param index
     * @return
     */
    public String getWord(int index) {
        return this.filteredList.get(index);
    }

    /**
     * Get the number of words in the current filtered list
     * @return
     */
    public int getCurrentSize() {
        return this.filteredList.size();
    }

    /**
     * Apply filter to the current wordlist based on feedback hint from Wordle.
     * This updates the object's internal filtered wordlist.
     * @param letter The desired letter to filter
     * @param status One of 'b', 'y', or 'g', else report exception
     * @param index The 0-based index of the letter, from 0 thru 4
     * @return The new size of the wordlist after filtering.
     */
    public int filter(char letter, char status, int index) {
        if (status == 'b') {
            System.out.println("Eliminating all words with " + letter);
            // Remove all words containing x
            this.filteredList.removeIf((word) -> {
                return word.contains(String.valueOf(letter));
            });
        } else if (status == 'y') {
            System.out.println("Eliminating all words without " + letter);
            // Remove all words that don't contain x at all
            this.filteredList.removeIf((word) -> {
                return !word.contains(String.valueOf(letter));
            });

            System.out.println("Eliminating all words with " + letter + " at index " + index);
            // Remove all words that contain x at this specific index
            final int idex = index; // Required due to lambda grrrr
            this.filteredList.removeIf((word) -> {
                return word.charAt(idex) == letter;
            });
        } else if (status == 'g') {
            System.out.println("Eliminating all words without " + letter + " at index " + index);
            // Remove all words that don't have x at this specific index
            final int idex = index;
            this.filteredList.removeIf((word) -> {
                return word.charAt(idex) != letter;
            });
        } else {
            throw new RuntimeException("Invalid letter status " + status);
        }

        // Re-compute frequency of letters in new list
        this.letterFrequency = this.determineLetterFrequency(this.filteredList);

        return this.filteredList.size();
    }

    /**
     * Print an ordered list of the letter frequency for the current wordlist.
     * Limit the number of printed lines by the top `maxLetters` letters.
     * @param maxLetters
     */
    public void printLetterFrequency(Integer maxLetters) {
        // List of all frequency entries
        List<FrequencyEntry> entries = this.letterFrequency;

        // Sort entries by frequency
        entries.sort((e1, e2) -> {
            return Float.compare(e2.getFrequency(), e1.getFrequency()); // Reversed to get largest-to-smallest ordering
        });

        // Print up to maxLetters entries
        int limit = Integer.min(maxLetters, entries.size());
        for (int i = 0; i < limit; i++) {
            char letter = entries.get(i).getLetter();
            float freq = entries.get(i).getFrequency();
            System.out.println(letter + ": " + freq);
        }
    }

    /**
     * Print an ordered list of the top words by score
     * @param maxWords Default 25
     */
    public void printTopWords(Integer maxWords) {
        // Create copy list of all words
        List<String> topWords = new ArrayList<>();
        topWords.addAll(this.filteredList);

        // Sort the list
        topWords.sort((w1, w2) -> {
            Float s1 = this.scoreForWord(w1);
            Float s2 = this.scoreForWord(w2);
            return s2.compareTo(s1);
        });

        // Print words, up to `maxWords`
        if (maxWords == null) {maxWords = 10;} // Default to 10
        int limit = Integer.min(maxWords, topWords.size());
        if (topWords.size() < 50) {
            limit = topWords.size();
        }
        for (int i = 0; i < limit; i++) {
            String word = topWords.get(i);
            System.out.println((i + 1) + ". " + word + ": " + this.scoreForWord(word));
        }
    }

    /**
     * Calculate the "score" for a given word, where score is an arbitrary
     * heuristic to decide how good of a guess a word is. A higher score means
     * the word will knock out more of the dictionary, and thus help you more
     * quickly converge on the solution.
     * @param word
     * @return
     */
    private Float scoreForWord(String word) {
        // Convert letter frequency to map for easy lookup
        Map<Character, Float> freqMap = new HashMap<>();
        this.letterFrequency.forEach((entry) -> {
            freqMap.put(entry.getLetter(), entry.getFrequency());
        });


        // Note: Repeated letters should not be counted twice!
        // Therefore, use map to ensure we don't have duplicates!
        Map<Character, Float> letterScore = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            char letter = word.charAt(i);
            float letterFreq = freqMap.getOrDefault(letter, 0F);
            letterScore.put(letter, letterFreq);
        }

        // Add up frequencies for letters
        Float score = 0F;
        for (Float f : letterScore.values().stream().toList()) {
            score += f;
        };

        return score;
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
     * Calculate the letter frequency for all letters in the provided wordlist.
     * Note: A FrequencyEntry is simply a tuple of (Character, Float).
     * @param wordList A list<String> of words
     * @return A list describing the frequency (percentage) of each letter in the wordlist
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