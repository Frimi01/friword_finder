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
            default:
                System.out.println("Unknown Action: Use -h or --help to see all actions.");
        }
    }
}
