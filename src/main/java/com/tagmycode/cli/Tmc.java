package com.tagmycode.cli;

import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Language;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.User;
import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Tmc {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public Api api;
    private String[] args = null;
    private Options options = new Options();
    private ReadInput readInput;
    private OutputStream outputStream = System.out;
    private OutputStream errorStream = System.err;
    private int exitStatus = 0;

    public static void main(String[] args) throws Exception {
        Tmc tmc = new Tmc(args);
        tmc.parse();
        System.exit(tmc.getExitStatus());
    }

    public Tmc(String[] args) {
        this.args = args;
        this.api = new Api(new TagMyCode(new Client(new Secret())), new Config());
        this.readInput = new ReadInput(System.in);
        configureOptions();
    }

    public void setApi(Api api) {
        this.api = api;
    }

    private void configureOptions() {
        options.addOption("h", "help", false, "Show help message");
        options.addOption(null, "login", false, "Authenticate to TagMyCode.com");
        Option fileOption = new Option("a", "add", true, "Create a snippet from file");
        fileOption.setArgName("filename");
        options.addOption(fileOption);
        options.addOption("p", "private", false, "Set the snippet private");
        options.addOption("v", "version", false, "Display version information");
        options.addOption("l", "languages", false, "Show languages");
        options.addOption(null, "info", false, "Show account information");
    }

    public Tmc parse() {
        DefaultParser parser = new DefaultParser();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                help();
                return this;
            }

            if (cmd.hasOption("v")) {
                printOutput("TagMyCode Cli version 0.1");
                return this;
            }

            if (cmd.hasOption("l")) {
                LanguageCollection languages = api.fetchLanguages();
                for (Language language : languages) {
                    printOutput(String.format("%s - %s\n", language.getName(), language.getCode()), false);
                }
                return this;
            }

            if (cmd.hasOption("a")) {
                String filePath = cmd.getOptionValue("add");
                boolean isPrivate = cmd.hasOption("p");
                printOutput(api.createSnippetFromFile(new FileSnippet(filePath), isPrivate).getUrl());
                return this;
            }

            if (cmd.hasOption("login")) {
                printOutput("Open this link:");
                printOutput(api.getAuthorizationUrl());
                printOutput("Enter the code: ", false);
                String code = readInput.read();

                api.authorizeWithVerificationCode(code);

                User account = api.fetchAccount();
                printOutput(String.format("Welcome %s", account.getEmail()));

                return this;
            }

            if (cmd.hasOption("info")) {
                User account = api.fetchAccount();
                printOutput(String.format("Email: %s", account.getEmail()));

                return this;
            }

            if (!cmd.hasOption("")) {
                exitStatus = 1;
                help();
            }

        } catch (ParseException e) {
            exitAndPrintException(e);
            help();
        } catch (TagMyCodeException e) {
            exitAndPrintException(e);
        } catch (Exception e) {
            exitAndPrintException(e);
        }
        return this;
    }

    private void exitAndPrintException(Exception e) {
        exitStatus = 1;
        printError(e.getMessage());
    }

    private void printError(String text) {
        new PrintStream(errorStream).print(text + LINE_SEPARATOR);
    }

    private void printOutput(String text) {
        printOutput(text, true);
    }

    private void printOutput(String text, boolean newLine) {
        if (newLine) {
            text += System.getProperty("line.separator");
        }
        new PrintStream(outputStream).print(text);
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();

        String commandName = "tmc";
        PrintWriter writer = new PrintWriter(outputStream);
        formatter.printHelp(writer, HelpFormatter.DEFAULT_WIDTH, commandName, null, options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, false);
        writer.flush();
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getErrorStream() {
        return errorStream;
    }

    public void setReadInput(ReadInput readInput) {
        this.readInput = readInput;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public void setErrorStream(OutputStream errorStream) {
        this.errorStream = errorStream;
    }
}
