package com.Frimi01;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

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

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
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
    }
}
