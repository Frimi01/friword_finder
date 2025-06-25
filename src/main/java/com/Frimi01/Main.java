package com.Frimi01;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Command(
        name = "friword-finder",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Helps find words from provided dictionary"
)
public class Main implements Runnable {

    @Parameters(index = "0", description = "Action: FindWord, PrintAll, IsWord")
    private String action;

    @CommandLine.Option(names = {"-v", "--value"}, description = "Value for action")
    private String value;

    @CommandLine.Option(names = {"--MinLength"}, description = "Minimum Length of Returned Words (default 2)")
    private Integer minl;

    @CommandLine.Option(names = {"--MaxLength"}, description = "Maximum Length of Returned Words")
    private Integer maxl;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    public static Set<String> loadDictionaryTxt(String path) {
        try {
            return new HashSet<>(Files.readAllLines(Paths.get(path)));
        } catch (IOException e) {
            System.err.println("Error: Dictionary text file not found or unreadable!");
            System.exit(1);
        }
        return Collections.emptySet();
    }


    // findPermutations("", value, i, foundWords);
    private void findPermutations(String prefix, String remaining, int targetLength, Set<String> results) {
        if (prefix.length() == targetLength) {
            results.add(prefix);
            return;
        }

        for (int i = 0; i < remaining.length(); i++) {
            String newPrefix = prefix + remaining.charAt(i);
            String newRemaining = remaining.substring(0, i) + remaining.substring(i + 1);
            findPermutations(newPrefix, newRemaining, targetLength, results);
        }
    }

    @Override
    public void run() {
        Set<String> Words = loadDictionaryTxt("assets/words.txt");

        switch (action.toLowerCase()) {
            case "printall":
                System.out.println(Words);
                break;
            case "isword":
                if (value == null){
                    System.out.println("You must provide value for this command. Use -v or --value options");
                } else {
                    String capValue = value.substring(0,1).toUpperCase() +
                            value.substring(1).toLowerCase();
                    if (Words.contains(value.toLowerCase())){
                        System.out.println(capValue + " found in dictionary");
                    } else {
                        System.out.println(capValue + " not found in dictionary");
                    }
                }
                break;
            case "findword":
                if (value == null) {
                    System.out.println("You must provide value for this command. Use -v or --value options");
                    break;
                }
                if (maxl == null) {maxl = value.length() + 1;}
                if (minl == null) {minl = 2;}

                Set<String> foundWords = new HashSet<>();

                for (int i = minl; i < maxl; i++){
                    findPermutations("", value, i, foundWords);
                }

                List<String> validWords = new ArrayList<>();
                for (String word : foundWords){
                    if (Words.contains(word.toLowerCase())){
                        validWords.add(word.toLowerCase());
                    }
                }

                validWords.sort((a, b) -> Integer.compare(b.length(), a.length()));
                System.out.printf("Total words found: %d\nnr| len | word\n", validWords.toArray().length);
                for (int i = 0; i < validWords.size(); i++) {
                    if (i < 9){
                        System.out.printf("%d.  (%d) | %s\n", i + 1, validWords.get(i).length(),validWords.get(i));
                    } else{
                        System.out.printf("%d. (%d) | %s\n", i + 1, validWords.get(i).length(),validWords.get(i));
                    }
                }
                break;
            default:
                System.out.println("Unknown Action: Use -h or --help to see all actions.");
        }
    }
}
