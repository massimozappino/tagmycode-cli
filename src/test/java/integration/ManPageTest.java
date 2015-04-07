package integration;


import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import support.ResourceReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class ManPageTest {
    @SuppressWarnings("ConstantConditions")
    @Test
    public void manOutputIsCorrect() throws IOException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("man/man1/tmc.1").getFile());
        String output = executeCommand("man " + file.getAbsoluteFile() + " | col -bx");

        assertEquals(getExpectedIndentedManPage(), output);
    }

    private String getExpectedIndentedManPage() throws IOException {
        String helpOutput = new ResourceReader().readFile("help.txt");
        String indentedHelpOutput = "";
        ArrayList<String> indentedHelpLines = new ArrayList<String>();
        for (String line : helpOutput.split("\n")) {
            String currentLine = "";
            if (line.contains("usage")) {
                currentLine += " " ;
            }
            currentLine += "      " + line ;
            indentedHelpLines.add(currentLine);
            indentedHelpOutput = StringUtils.join(indentedHelpLines, "\n\n");
        }

        String manOutput = new ResourceReader().readFile("man_output.txt");
        return manOutput.replaceAll("@@@INDENTED_HELP@@@", indentedHelpOutput);
    }


    private String executeCommand(String command) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        String[] bashCommand = {"/bin/sh", "-c", command};

        Process p = Runtime.getRuntime().exec(bashCommand);
        p.waitFor();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output.toString();
    }
}
