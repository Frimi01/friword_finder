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

    @Parameters(index = "0", description = "Commands: FindWord, IsWord")
    private String command;

    @Parameters(index = "1", description = "Input of command")
    private String input;

    @CommandLine.Option(names = {"-mnl", "--MinLength"}, description = "Minimum Length of FindWord output (default 2)")
    private Integer minl;

    @CommandLine.Option(names = {"-mxl", "--MaxLength"}, description = "Maximum Length of FindWord output")
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


    // findPermutations("", input, i, foundWords);
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

        switch (command.toLowerCase()) {
            case "isword":
                String capValue = input.substring(0,1).toUpperCase() +
                        input.substring(1).toLowerCase();
                if (Words.contains(input.toLowerCase())){
                    System.out.println(capValue + " found in dictionary");
                } else {
                    System.out.println(capValue + " not found in dictionary");
                }
                break;
            case "findword":
                if (maxl == null) {maxl = input.length() + 1;}
                if (minl == null) {minl = 2;}

                Set<String> foundWords = new HashSet<>();

                for (int i = minl; i < maxl; i++){
                    findPermutations("", input, i, foundWords);
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
