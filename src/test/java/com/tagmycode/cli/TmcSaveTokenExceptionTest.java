package com.tagmycode.cli;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TmcSaveTokenExceptionTest {

    @Test
    public void testGetMessage() throws Exception {
        TmcSaveTokenException tmcSaveTokenException = new TmcSaveTokenException();
        assertEquals("There was an error saving account information",
                tmcSaveTokenException.getMessage());
    }
}