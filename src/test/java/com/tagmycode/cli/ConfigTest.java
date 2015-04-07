package com.tagmycode.cli;

import com.google.common.io.Files;
import com.tagmycode.sdk.authentication.OauthToken;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ConfigTest {
    @Test
    public void getPath() {
        Config config = new Config();
        String expectedString = System.getProperty("user.home") + File.separator + ".tagmycode";
        assertEquals(expectedString, config.getDirectoryPath());
    }

    @Test
    public void createDirectory() {
        Config config = getTestConfig();
        config.createDirectory();
        File directory = new File(config.getDirectoryPath());
        assertTrue(directory.exists());
    }

    @Test
    public void saveAccessToken() throws Exception {
        Config config = getTestConfig();
        OauthToken accessToken = new OauthToken("123", "456");
        config.saveAccessToken(accessToken);
        OauthToken loadedOauthToken = config.loadAccessToken();
        assertEquals(accessToken.getAccessToken(), loadedOauthToken.getAccessToken());
        assertEquals(accessToken.getRefreshToken(), loadedOauthToken.getRefreshToken());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void saveAccessTokenOnNotValidFileThrowsException() throws IOException {
        Config config = getTestConfig();

        // simulate non writable oauthFileName
        String directoryPath = config.getDirectoryPath();
        File file = new File(directoryPath + File.separator + config.getOauthFileName());
        file.createNewFile();
        file.setWritable(false);

        try {
            config.saveAccessToken(new OauthToken("123", "456"));
            fail("Exception expected");
        } catch (Exception e) {
            assertNull(config.loadAccessToken());
        }
    }

    @Test
    public void loadInvalidAccessToken() throws Exception {
        Config config = getTestConfig();
        config.writeFile(config.getOauthFileName(), "one line");
        assertNull(config.loadAccessToken());

        config.writeFile(config.getOauthFileName(), System.getProperty("line.separator"));
        assertNull(config.loadAccessToken());
    }

    private Config getTestConfig() {
        File tempDir = Files.createTempDir();

        return new Config(tempDir.getPath());
    }
}

