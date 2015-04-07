package com.tagmycode.cli;

import org.junit.Test;


import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

public class ReadInputTest {
    @Test
    public void read() {
        String inputText = "myAuthCode\n";
        ReadInput readInput = new ReadInput(new ByteArrayInputStream(inputText.getBytes()));
        assertEquals("myAuthCode", readInput.read());
    }
}
