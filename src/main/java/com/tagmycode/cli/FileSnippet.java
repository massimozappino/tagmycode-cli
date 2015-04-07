package com.tagmycode.cli;

import com.tagmycode.sdk.model.Snippet;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileSnippet {
    private File file;

    public FileSnippet(String filePath) {
        this.file = new File(filePath);
    }

    public Snippet buildSnippet() throws IOException {
        Snippet snippet = new Snippet();
        snippet.setTitle(file.getName());
        snippet.setCode(readFileContent());

        return snippet;
    }

    private String readFileContent() throws IOException {
        return FileUtils.readFileToString(file);
    }
}
