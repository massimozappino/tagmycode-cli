package com.tagmycode.cli;

import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.Snippet;
import org.junit.Before;
import org.junit.Test;
import support.ResourceGenerate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TmcTest extends BaseTest {
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;

    @Before
    public void initOutputStream() {
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
    }

    @Test
    public void launchWithoutParamsShowsHelp() throws Exception {
        Tmc tmc = createTMCWithArguments();
        tmc.parse();

        assertOutputIs(tmc, getExpectedHelpText());
        assertExitStatusIs(tmc, 1);
    }

    @Test
    public void launchWithHelpParam() throws Exception {
        Tmc tmc = createTMCWithArguments("-h");
        tmc.parse();

        assertOutputIs(tmc, getExpectedHelpText());
        assertExitStatusIs(tmc, 0);
    }

    @Test
    public void versionParam() throws Exception {
        Tmc tmc = createTMCWithArguments("-v");
        tmc.parse();

        assertOutputIs(tmc, "TagMyCode Cli version 0.1\n");
        assertExitStatusIs(tmc, 0);
    }

    @Test
    public void showLanguages() throws Exception {
        Api apiMock = mock(Api.class);
        when(apiMock.fetchLanguages()).thenReturn(new ResourceGenerate().aLanguageCollection());
        Tmc tmc = createTMCWithArguments("-l");
        tmc.setApi(apiMock);

        tmc.parse();

        assertOutputIs(tmc, "Java - java\nXxx - xxx\n");
    }

    @Test
    public void createNewSnippetWithNoFileParamThrowsException() throws Exception {
        Tmc tmc = createTMCWithArguments("-a");
        tmc.parse();

        assertErrorIs(tmc, "Missing argument for option: a\n");
        assertOutputIs(tmc, getExpectedHelpText());
    }

    @Test
    public void createNewSnippetWithUnreadableFile() throws Exception {
        Tmc tmc = createTMCWithArguments("--add", "filename.txt");
        Api apiMock = mock(Api.class);

        when(apiMock.createSnippetFromFile((FileSnippet) any(), anyBoolean())).thenThrow(new IOException("Permission denied"));
        tmc.setApi(apiMock);
        tmc.parse();

        assertErrorIs(tmc, "Permission denied\n");
        assertExitStatusIs(tmc, 1);
    }

    @Test
    public void createNewSnippetWithFile() throws Exception {
        Snippet expectedSnippet = new ResourceGenerate().aSnippet();
        Tmc tmc = createTMCWithArguments("-a", "filename.txt");
        Api apiMock = mock(Api.class);

        when(apiMock.createSnippetFromFile((FileSnippet) any(), anyBoolean())).thenReturn(expectedSnippet);
        tmc.setApi(apiMock);
        tmc.parse();

        verify(apiMock, times(1)).createSnippetFromFile((FileSnippet) any(), eq(false));

        assertOutputIs(tmc, "https://tagmycode.com/snippet/1\n");
        assertExitStatusIs(tmc, 0);
    }

    @Test
    public void createNewSnippetAndMarkAsPrivate() throws Exception {
        Snippet expectedSnippet = new ResourceGenerate().aSnippet();
        Tmc tmc = createTMCWithArguments("-a", "filename.txt", "-p");
        Api apiMock = mock(Api.class);

        when(apiMock.createSnippetFromFile((FileSnippet) any(), anyBoolean())).thenReturn(expectedSnippet);
        tmc.setApi(apiMock);
        tmc.parse();

        verify(apiMock, times(1)).createSnippetFromFile((FileSnippet) any(), eq(true));

        assertOutputIs(tmc, "https://tagmycode.com/snippet/1\n");
        assertExitStatusIs(tmc, 0);
    }

    @Test
    public void afterAuthenticationIAmAlwaysAuthenticated() {
        assertEquals("", "");
    }

    @Test
    public void authenticate() throws Exception {
        Api apiMock = mock(Api.class);
        ReadInput readInputMock = mock(ReadInput.class);
        when(apiMock.fetchLanguages()).thenReturn(new ResourceGenerate().aLanguageCollection());
        when(apiMock.fetchAccount()).thenReturn(new ResourceGenerate().aUser());
        when(apiMock.getAuthorizationUrl()).thenReturn("https://tagmycode.com/oauth2/authorize?client_id=mockedUrl");
        when(readInputMock.read()).thenReturn("myAuthCode");

        Tmc tmc = createTMCWithArguments("--login");
        tmc.setApi(apiMock);
        tmc.setReadInput(readInputMock);
        tmc.parse();

        verify(apiMock).authorizeWithVerificationCode("myAuthCode");

        assertOutputIs(tmc, "Open this link:\nhttps://tagmycode.com/oauth2/authorize?client_id=mockedUrl\n" +
                "Enter the code: Welcome myfake@email.not\n");
        assertExitStatusIs(tmc, 0);
    }

    @Test
    public void showAccountForLoggedUser() throws Exception {
        Tmc tmc = createTMCWithArguments("--info");
        Api apiMock = mock(Api.class);

        when(apiMock.fetchAccount()).thenReturn(new ResourceGenerate().aUser());
        tmc.setApi(apiMock);
        tmc.parse();

        verify(apiMock, times(1)).fetchAccount();

        assertOutputIs(tmc, "Email: myfake@email.not\n");
        assertExitStatusIs(tmc, 0);
    }

    @Test
    public void showAccountForNotLoggedUser() throws Exception {
        Tmc tmc = createTMCWithArguments("--info");
        Api apiMock = mock(Api.class);
        when(apiMock.fetchAccount()).thenThrow(new TagMyCodeException("Not authenticated"));
        tmc.setApi(apiMock);

        tmc.parse();

        verify(apiMock, times(1)).fetchAccount();
        assertErrorIs(tmc, "Not authenticated\n");
        assertExitStatusIs(tmc, 1);
    }

    @Test
    public void exceptionIsThrownWhenOauthFileIsNotWritable() throws Exception {
        Tmc tmc = createTMCWithArguments("--add", "filename");
        Api apiMock = mock(Api.class);
        when(apiMock.createSnippetFromFile((FileSnippet) any(), anyBoolean())).thenThrow(new TagMyCodeException("A custom error message"));
        tmc.setApi(apiMock);

        tmc.parse();

        assertErrorIs(tmc, "A custom error message\n");
        assertExitStatusIs(tmc, 1);
    }

    private void assertOutputIs(Tmc tmc, String expectedOutput) {
        assertEquals(expectedOutput, getOutput(tmc));
    }

    private void assertErrorIs(Tmc tmc, String expectedOutput) {
        assertEquals(expectedOutput, getError(tmc));
    }

    private String getOutput(Tmc tmc) {
        return tmc.getOutputStream().toString();
    }

    private String getError(Tmc tmc) {
        return tmc.getErrorStream().toString();
    }

    private void assertExitStatusIs(Tmc tmc, int exitStatus) {
        assertEquals(exitStatus, tmc.getExitStatus());
    }

    private Tmc createTMCWithArguments(String... arguments) throws Exception {
        Tmc tmc = new Tmc(arguments);
        tmc.setOutputStream(outputStream);
        tmc.setErrorStream(errorStream);
        return tmc;
    }
}
