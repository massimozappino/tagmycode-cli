package com.tagmycode.cli;


import support.ResourceReader;

import java.io.IOException;

public class BaseTest {

    protected String getExpectedHelpText() throws IOException {
        return new ResourceReader().readFile("help.txt");
    }
}
