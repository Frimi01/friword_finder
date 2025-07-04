package com.Frimi01;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

@Command(
        name = "friword-finder",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Helps find words from provided dictionary"
)
public class Main implements Callable<Integer> {

    @Parameters(index = "0", description = "Commands: FindWord, IsWord")
    private String command;

    @Parameters(index = "1", description = "Input of command")
    private String input;

    @CommandLine.Option(names = {"-mnl", "--MinLength"}, description = "Minimum Length of FindWord output (default 2)")
    private Integer minl;

    @CommandLine.Option(names = {"-mxl", "--MaxLength"}, description = "Maximum Length of FindWord output")
    private Integer maxl;

    @CommandLine.Option(names = {"-debug"}, description = "enables extra debug information")
    private boolean debugFlag;

    @CommandLine.Option(names = {"--noWarn"}, description = "Executes commands despite set limits (Caution!)")
    private boolean ignoreWarnings;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    public static Set<String> loadDictionaryTxt(String path) {
        try {
            return new HashSet<>(Files.readAllLines(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException("Error: Dictionary text file not found or unreadable!");
        }
    }


    // findPermutations("", input, i, foundWords);
    private void findPermutations(String prefix, String remaining, int targetLength, Set<String> words, Trie trie, Set<String> foundWords) {
        if (!trie.isPrefix(prefix)) return;
        if (prefix.length() == targetLength) {
            if (words.contains(prefix.toLowerCase())) {
                foundWords.add(prefix);
            }
        }

        for (int i = 0; i < remaining.length(); i++) {
            String newPrefix = prefix + remaining.charAt(i);
            String newRemaining = remaining.substring(0, i) + remaining.substring(i + 1);
            findPermutations(newPrefix, newRemaining, targetLength, words, trie, foundWords);
        }
    }

    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isWord = false;
    }

    class Trie {
        private final TrieNode root = new TrieNode();

        private int index(char ch) {
            return ch - 'a';
        }

        public void insert(String word) {
            TrieNode node = root;
            for (char ch : word.toLowerCase().toCharArray()) {
                int idx = index(ch);
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.isWord = true;
        }

        public boolean isWord(String word) {
            TrieNode node = root;
            for (char ch : word.toLowerCase().toCharArray()) {
                int idx = index(ch);
                if (node.children[idx] == null) return false;
                node = node.children[idx];
            }
            return node.isWord;
        }

        public boolean isPrefix(String prefix) {
            TrieNode node = root;
            for (char ch : prefix.toLowerCase().toCharArray()) {
                int idx = index(ch);
                if (node.children[idx] == null) return false;
                node = node.children[idx];
            }
            return true;
        }
    }

    @Override
    public Integer call(){
        int code;
        try {
            code = run();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            code = 1;
        }
        if (debugFlag) {
            System.out.println("Program shutting down with exitCode: " + code);
        }
        return code;
    }
    public int run() {
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
                if ((maxl > input.length() + 1 || maxl > 10) && !ignoreWarnings) {
                    System.err.println("Input too long for findWord command (limit 10). Long inputs can take time or have a lot of results. --noWarn to continue anyway.");
                    return 1;
                }
                if (minl < 1) {
                    System.err.println("Length of string cannot be smaller than 1.");
                    return 1;
                }

                Set<String> foundWords = new HashSet<>();
                Trie trie = new Trie();
                try {
                    for (String word : Files.readAllLines(Paths.get("assets/words.txt"))) {
                        trie.insert(word);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for (int i = minl; i < maxl; i++){
                    findPermutations("", input, i, Words, trie, foundWords);
                }

                List<String> resultList = new ArrayList<>(foundWords);

                resultList.sort((a, b) -> Integer.compare(b.length(), a.length()));
                System.out.printf("Total words found: %d\nnr | len | word\n", resultList.size());
                for (int i = 0; i < resultList.size(); i++) {
                    System.out.printf("%3d. (%d) | %s\n", i + 1, resultList.get(i).length(), resultList.get(i));
                }
                
                if (debugFlag){
                    int trueWords = 0;
                    int falseWords = 0;
                    for (String word : resultList){
                        if (Words.contains(word)){
                            trueWords++;
                        } else {
                            falseWords++;
                        }
                    }
                    System.out.printf("\nChecked words are true: %d and false: %d\n", trueWords, falseWords);
                }
                break;
            default:
                System.err.println("Unknown command: " + command);
                CommandLine.usage(this, System.out);
                return 1;
        }
        return 0;
    }
}
