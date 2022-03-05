import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Dictionary {

    // Props
    private List<String> fullWordList;
    private List<String> filteredList;
    private Map<Character, Float> letterFrequency = new HashMap<>();

    // Init
    public Dictionary() {

    }

    // Funcs
    public void loadFrom(String filePath) {
        List<String> lines;
        try {
            Stream<String> stringStream = Files.lines(Paths.get("dictionary.txt"));
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