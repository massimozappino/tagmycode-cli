package com.tagmycode.cli;

public class TmcSaveTokenException extends Exception {
    @Override
    public String getMessage() {
        return "There was an error saving account information";
    }
}
