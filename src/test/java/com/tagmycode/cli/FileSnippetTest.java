package com.tagmycode.cli;

import com.tagmycode.sdk.model.Snippet;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class FileSnippetTest extends BaseTest {
    @Test

    public void buildSnippetWithValidFile() throws Exception {
        FileSnippet fileSnippet = new FileSnippet("src/test/resources/help.txt");
        Snippet snippet = fileSnippet.buildSnippet();

        assertEquals("text", snippet.getLanguage().getCode());
        assertEquals("help.txt", snippet.getTitle());
        assertEquals(getExpectedHelpText(), snippet.getCode());
        assertEquals(false, snippet.isPrivate());

        assertNull(snippet.getUrl());
    }

    @Test
    public void buildSnippetWithNonexistentFile() throws Exception {
        FileSnippet fileSnippet = new FileSnippet("src/test/resources/nonexistent.txt");
        try {
            fileSnippet.buildSnippet();
            fail("Expected exception");
        } catch (IOException e) {
            assertEquals("File 'src/test/resources/nonexistent.txt' does not exist", e.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void buildSnippetWithFileWithNoPermissions() throws Exception {
        File nonReadableFile = new File("src/test/resources/no_permissions.txt");
        nonReadableFile.setReadable(false);

        FileSnippet fileSnippet = new FileSnippet("src/test/resources/no_permissions.txt");
        try {
            fileSnippet.buildSnippet();
            fail("Expected exception");
        } catch (IOException e) {
            assertEquals("File 'src/test/resources/no_permissions.txt' cannot be read", e.getMessage());
        } finally {
            nonReadableFile.setReadable(true);
        }
    }

}