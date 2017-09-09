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
        OauthWallet config = new OauthWallet();
        String expectedString = System.getProperty("user.home") + File.separator + ".tagmycode";
        assertEquals(expectedString, config.getDirectoryPath());
    }

    @Test
    public void createDirectory() {
        OauthWallet config = getTestOauthWallet();
        config.createDirectory();
        File directory = new File(config.getDirectoryPath());
        assertTrue(directory.exists());
    }

    @Test
    public void saveAccessToken() throws Exception {
        OauthWallet oauthWallet = getTestOauthWallet();
        OauthToken accessToken = new OauthToken("123", "456");
        oauthWallet.saveOauthToken(accessToken);
        OauthToken loadedOauthToken = oauthWallet.loadOauthToken();
        assertEquals(accessToken.getAccessToken(), loadedOauthToken.getAccessToken());
        assertEquals(accessToken.getRefreshToken(), loadedOauthToken.getRefreshToken());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void saveAccessTokenOnNotValidFileThrowsException() throws IOException {
        OauthWallet oauthWallet = getTestOauthWallet();

        // simulate non writable oauthFileName
        String directoryPath = oauthWallet.getDirectoryPath();
        File file = new File(directoryPath + File.separator + oauthWallet.getOauthFileName());
        file.createNewFile();
        file.setWritable(false);

        try {
            oauthWallet.saveOauthToken(new OauthToken("123", "456"));
            fail("Exception expected");
        } catch (Exception e) {
            assertNull(oauthWallet.loadOauthToken());
        }
    }

    @Test
    public void loadInvalidAccessToken() throws Exception {
        OauthWallet oauthWallet = getTestOauthWallet();
        oauthWallet.writeFile(oauthWallet.getOauthFileName(), "one line");
        assertNull(oauthWallet.loadOauthToken());

        oauthWallet.writeFile(oauthWallet.getOauthFileName(), System.getProperty("line.separator"));
        assertNull(oauthWallet.loadOauthToken());
    }

    private OauthWallet getTestOauthWallet() {
        File tempDir = Files.createTempDir();

        return new OauthWallet(tempDir.getPath());
    }
}

