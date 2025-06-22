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
        description = "Says Hello or Goodbye"
)
public class Main implements Runnable {

    @Parameters(index = "0", description = "The greeting type: 'hello' or 'goodbye'")
    private String greeting;

    @Parameters(index = "1", description = "Something to print")
    private String printable;

    @Parameters(index = "2", description = "Action: PrintAll, FindWord")
    private String action;

    @Parameters(index = "3", description = "Value for action")
private String value;

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



    @Override
    public void run() {
        Set<String> Words = loadDictionaryTxt("assets/words.txt");

        switch (greeting.toLowerCase()) {
            case "hello":
                System.out.println("Hello, world!");
                break;
            case "goodbye":
                System.out.println("Goodbye, world!");
                break;
            default:
                System.out.println("Unknown greeting: " + greeting);
                System.out.println("Please use 'hello' or 'goodbye'");
        }
        System.out.println(printable);

        switch (action.toLowerCase()) {
            case "printall":
                System.out.println(Words);
                break;
            case "findword":
                System.out.println(Words.contains(value.toLowerCase()));
        }
    }
}
