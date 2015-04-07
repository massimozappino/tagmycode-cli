package com.tagmycode.cli;

import java.io.InputStream;
import java.util.Scanner;

public class ReadInput {
    private Scanner scanner;

    public ReadInput(InputStream inputStream) {
        scanner = new Scanner(inputStream);
    }

    public String read() {
        return scanner.next();
    }
}
