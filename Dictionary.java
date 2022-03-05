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
    private Map<Character, Float> letterFrequency = new HashMap<>();

    // Init
    public Dictionary() {

    }

    /**
     * Initialize the dictionary using the default wordlist
     */
    public void init() {
        this.loadFrom(DICT_FILE);
    }

    /**
     * Initialize the dictionary using a custom wordlist, specified at the 
     * provided file path. 
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
}