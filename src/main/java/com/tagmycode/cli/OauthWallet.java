package com.tagmycode.cli;

import com.tagmycode.sdk.IOauthWallet;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.exception.TagMyCodeException;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class OauthWallet implements IOauthWallet {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private String path;

    public OauthWallet() {
        String userHome = System.getProperty("user.home");
        path = userHome + File.separator + ".tagmycode";
    }

    public OauthWallet(String path) {
        this.path = path;
    }

    public String getDirectoryPath() {
        return path;
    }

    @Override
    public void saveOauthToken(OauthToken oauthToken) throws TmcSaveTokenException {
        String fileName = getOauthFileName();
        String fileContent = oauthToken.getAccessToken().getToken()
                + LINE_SEPARATOR
                + oauthToken.getRefreshToken().getToken();
        try {
            writeFile(fileName, fileContent);
        } catch (IOException e) {
            throw new TmcSaveTokenException();
        }
    }

    @Override
    public void deleteOauthToken() throws TagMyCodeException {

    }

    protected String getOauthFileName() {
        return "oauth.txt";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    void writeFile(String fileName, String fileContent) throws IOException {
        File file = new File(getFilePath(fileName));

        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(fileContent);
        writer.close();
    }

    @Override
    public OauthToken loadOauthToken() {
        OauthToken oauthToken = null;
        try {
            String fileName = getOauthFileName();
            String fileContent = loadFileContent(fileName);
            if (fileContent.contains(LINE_SEPARATOR)) {
                String[] split = fileContent.split(LINE_SEPARATOR);
                oauthToken = new OauthToken(split[0], split[1]);
            }
        } catch (IOException ignored) {
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return oauthToken;
    }

    private String loadFileContent(String fileName) throws IOException {
        String filePath = getFilePath(fileName);

        FileInputStream inputStream = new FileInputStream(filePath);
        String fileContent = "";
        try {
            fileContent = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }
        return fileContent;
    }

    private String getFilePath(String fileName) {
        return getDirectoryPath() + File.separator + fileName;
    }

    public boolean createDirectory() {
        File directory = new File(getDirectoryPath());
        return directory.mkdir();
    }
}
