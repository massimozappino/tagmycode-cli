package com.tagmycode.cli;

import com.tagmycode.sdk.exception.TagMyCodeException;

public class TmcSaveTokenException extends TagMyCodeException {
    public TmcSaveTokenException() {
        super("There was an error saving account information");
    }
}
