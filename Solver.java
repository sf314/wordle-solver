import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Solver {
    private static String DICT_FILE = "dictionary.txt";

    public static void main(String[] args) throws Exception {
        System.out.println("Wordle Solver");

        // Read dictionary file into list
        List<String> lines;
        try {
            Stream<String> stringStream = Files.lines(Paths.get(DICT_FILE));
            lines = stringStream.toList();
            stringStream.close(); // Required per Closable iface
        } catch (Exception e) {
            System.out.println("Failed to load dictionary!");
            throw e;
        }

        List<String> dictionary = new ArrayList<>();
        for (String l : lines) {
            dictionary.add(l);
        }

        // Validate dictionary
        System.out.println("Dictionary is of size: " + dictionary.size());
        for (String word : dictionary) {
            if (word.length() != 5) {
                System.out.println("Word " + word + " is not 5 letters long!");
            }
        }

        // Continually read input. Format: {guess} {result}
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("Command: guess, exit");
            String command = scanner.nextLine();
            System.out.println("Will " + command);
            if (command.equals("exit")) {
                break;
            }

            if (!command.equals("guess")) {
                continue;
            }

            System.out.println("Guess the first word. Provide results as [b]lack, [y]ellow, or [green] for each letter.");
            System.out.println("Note: if a repeated letter is both [b] and [y/g], then mark [b] instances as [y].");
            String input = scanner.nextLine();
            if (input.length() != 11) {
                System.out.println("MUST PROVIDE GUESS AND RESULTS (11 chars)");
                continue;
            }

            String guess = input.split(" ")[0];
            String result = input.split(" ")[1];
            System.out.println("Guess: " + guess);
            System.out.println("Result: " + result);

            // VALIDATION
            if (guess.length() != 5) {
                System.out.println("Guess needs to be 5 letters!");
                continue;
            }

            if (result.length() != 5) {
                System.out.println("Result needs to be 5 letters!");
                continue;
            }

            if (!lines.contains(guess)) {
                System.out.println("Guess needs to be a valid word!");
                continue;
            }

            for (int i = 0; i < 5; i++) {
                char ch = guess.charAt(i);
                char charResult = result.charAt(i);

                if (charResult == 'b') {
                    System.out.println("Eliminating all words with " + ch);
                    // Remove all words containing x
                    dictionary.removeIf((word) -> {
                        return word.contains(String.valueOf(ch));
                    });
                } else if (charResult == 'y') {
                    System.out.println("Eliminating all words without " + ch);
                    // Remove all words that don't contain x at all
                    dictionary.removeIf((word) -> {
                        return !word.contains(String.valueOf(ch));
                    });

                    System.out.println("Eliminating all words with " + ch + " at index " + i);
                    // Remove all words that contain x at this specific index
                    final int idex = i; // Required due to lambda grrrr
                    dictionary.removeIf((word) -> {
                        return word.charAt(idex) == ch;
                    });
                } else if (charResult == 'g') {
                    System.out.println("Eliminating all words without " + ch + " at index " + i);
                    // Remove all words that don't have x at this specific index
                    final int idex = i;
                    dictionary.removeIf((word) -> {
                        return word.charAt(idex) != ch;
                    });
                } else {
                    System.out.println("Invalid result char " + charResult);
                }
            }

            // Print prelim results
            System.out.println("New dictionary size: " + dictionary.size());
            System.out.println("Top words: ");
            for (int i = 0; i < (dictionary.size() > 50 ? 10 : dictionary.size()); i++) {
                System.out.println("  " + dictionary.get(i));
            }

        }
        scanner.close();

        System.out.println("Thanks for playing!");
    }
}