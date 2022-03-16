
import java.util.Scanner;

public class Solver {

    public static void main(String[] args) throws Exception {
        System.out.println("Wordle Solver");

        Dictionary dictionary = new Dictionary();
        System.out.println("Dictionary size: " + dictionary.getCurrentSize());
        System.out.println("Top words: ");
        dictionary.printTopWords(10);
        System.out.println("Top letters by frequency: ");
        dictionary.printLetterFrequency(10);

        // Continually read input. Format: {guess} {result}
        Scanner scanner = new Scanner(System.in);
        
        try {
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
    
                if (!dictionary.contains(guess)) {
                    System.out.println("Guess needs to be a valid word!");
                    continue;
                }
    
                for (int i = 0; i < 5; i++) {
                    char letter = guess.charAt(i);
                    char status = result.charAt(i);
    
                    // Filter the dictionary based on feedback from Wordle
                    dictionary.filter(letter, status, i);
                }
    
                // Print prelim results
                System.out.println("New dictionary size: " + dictionary.getCurrentSize());
                System.out.println("Top words: ");
                dictionary.printTopWords(null);
                System.out.println("Top letters by frequency: ");
                dictionary.printLetterFrequency(10);
    
            }
    
            System.out.println("Thanks for playing!");
        } catch (Exception e) {
            System.out.println("Failure in the Solver runloop: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close(); // Scanner must be closed before exiting
        }
    }
}