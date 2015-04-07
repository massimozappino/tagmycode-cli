package com.tagmycode.cli;

import com.tagmycode.sdk.IWallet;
import com.tagmycode.sdk.authentication.OauthToken;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class Config implements IWallet {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private String path;

    public Config() {
        String userHome = System.getProperty("user.home");
        path = userHome + File.separator + ".tagmycode";
    }

    public Config(String path) {
        this.path = path;
    }

    public String getDirectoryPath() {
        return path;
    }

    public void saveAccessToken(OauthToken accessToken) throws TmcSaveTokenException {
       if (!saveOauthToken(accessToken)) {
           throw new TmcSaveTokenException();
       }
    }

    @Override
    public boolean saveOauthToken(OauthToken oauthToken) {
        String fileName = getOauthFileName();
        String fileContent = oauthToken.getAccessToken().getToken()
                + LINE_SEPARATOR
                + oauthToken.getRefreshToken().getToken();
        try {
            writeFile(fileName, fileContent);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    protected String getOauthFileName() {
        return "oauth.txt";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected Void writeFile(String fileName, String fileContent) throws IOException {
        File file = new File(getFilePath(fileName));

        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(fileContent);
        writer.close();
        return null;
    }

    public OauthToken loadAccessToken() {
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
