package com.tagmycode.cli;

import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.model.LanguageCollection;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.User;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import support.ResourceGenerate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApiTest {

    @Test
    public void configCreateDirectoryInConstructor() throws Exception {
        Config configMock = createConfigMock();
        when(configMock.createDirectory()).thenThrow(new RuntimeException());
        try {
            spy(new Api(createTagMyCodeMock(), configMock));
            fail("Expected exception");
        } catch (RuntimeException ignored) {
        }

    }

    @Test
    public void fetchLanguages() throws Exception {
        Client clientMock = createClientMock();
        TagMyCode tagMyCodeMock = createTagMyCodeMock(clientMock);
        LanguageCollection expectedLanguages = new ResourceGenerate().aLanguageCollection();
        when(tagMyCodeMock.fetchLanguages()).thenReturn(expectedLanguages);
        Api apiSpy = spy(new Api(tagMyCodeMock, createConfigMock()));

        LanguageCollection actualLanguages = apiSpy.fetchLanguages();

        verify(tagMyCodeMock).fetchLanguages();
        assertEquals(expectedLanguages, actualLanguages);
    }

    @Test
    public void fetchAccount() throws Exception {
        TagMyCode tagMyCodeMock = createTagMyCodeMock();
        User expectedUser = new ResourceGenerate().anUser();
        when(tagMyCodeMock.fetchAccount()).thenReturn(expectedUser);
        Api apiSpy = spy(new Api(tagMyCodeMock, createConfigMock()));

        User user = apiSpy.fetchAccount();

        verify(tagMyCodeMock).fetchAccount();
        assertEquals(expectedUser, user);
    }


    @Test
    public void createSnippetFromFile() throws Exception {
        Snippet expectedSnippet = new ResourceGenerate().aSnippet();

        FileSnippet fileSnippetMock = mock(FileSnippet.class);
        when(fileSnippetMock.buildSnippet()).thenReturn(expectedSnippet);

        TagMyCode tagMyCodeMock = createTagMyCodeMock();
        when(tagMyCodeMock.createSnippet(expectedSnippet)).thenReturn(expectedSnippet);
        Api apiSpy = spy(new Api(tagMyCodeMock, createConfigMock()));

        Snippet snippet = apiSpy.createSnippetFromFile(fileSnippetMock, false);

        verify(tagMyCodeMock, times(1)).createSnippet(expectedSnippet);
        assertEquals(expectedSnippet, snippet);
    }

    @Test
    public void createSnippetFromFileAndMarkAsPrivate() throws Exception {
        FileSnippet fileSnippetMock = mock(FileSnippet.class);
        when(fileSnippetMock.buildSnippet()).thenReturn(new ResourceGenerate().aSnippet());
        TagMyCode tagMyCodeMock = createTagMyCodeMock();
        Api apiSpy = spy(new Api(tagMyCodeMock, createConfigMock()));

        apiSpy.createSnippetFromFile(fileSnippetMock, true);

        ArgumentCaptor argument = ArgumentCaptor.forClass(Snippet.class);
        verify(tagMyCodeMock, times(1)).createSnippet((Snippet) argument.capture());
        assertEquals(true, ((Snippet) argument.getValue()).isPrivate());
    }

    @Test
    public void getAuthorizationUrl() throws Exception {
        TagMyCode tagMyCodeMock = createTagMyCodeMock();
        Client clientMock = createClientMock();
        when(tagMyCodeMock.getClient()).thenReturn(clientMock);

        Api api = new Api(tagMyCodeMock, createConfigMock());
        api.getAuthorizationUrl();

        verify(clientMock).getAuthorizationUrl();
    }

    @Test
    public void authorizeWithVerificationCode() throws Exception {
        OauthToken oauthToken = new OauthToken("123", "456");
        TagMyCode tagMyCodeMock = createTagMyCodeMock();
        Client clientMock = createClientMock();
        when(tagMyCodeMock.getClient()).thenReturn(clientMock);
        when(clientMock.getOauthToken()).thenReturn(oauthToken);
        Config configMock = createConfigMock();
        Api apiSpy = spy(new Api(tagMyCodeMock, configMock));

        apiSpy.authorizeWithVerificationCode("myCode");

        verify(clientMock).fetchOauthToken("myCode");
    }

    @Test
    public void isAuthenticatedTrue() throws Exception {
        Client clientMock = createClientMock();
        when(clientMock.isAuthenticated()).thenReturn(true);
        Api api = new Api(new TagMyCode(clientMock), createConfigMock());
        assertTrue(api.isAuthenticated());
    }

    @Test
    public void isAuthenticatedFalse() throws Exception {
        Client clientMock = createClientMock();
        when(clientMock.isAuthenticated()).thenReturn(false);
        Api api = new Api(new TagMyCode(clientMock), createConfigMock());
        assertFalse(api.isAuthenticated());
    }

    @Test
    public void authenticationDependsOnConfig() throws Exception {
        Client client = new Client("123", "456");
        Config configMock = createConfigMock();
        when(configMock.loadAccessToken()).thenReturn(new OauthToken("123", "456"));

        Api api = new Api(new TagMyCode(client), configMock);

        verify(configMock, times(1)).loadAccessToken();
        assertEquals(true, api.isAuthenticated());
    }

    private TagMyCode createTagMyCodeMock(Client client) {
        TagMyCode tagMyCodeMock = mock(TagMyCode.class);
        when(tagMyCodeMock.getClient()).thenReturn(client);
        return tagMyCodeMock;
    }

    private TagMyCode createTagMyCodeMock() {
        return createTagMyCodeMock(createClientMock());
    }

    private Client createClientMock() {
        return mock(Client.class);
    }

    private Config createConfigMock() {
        return mock(Config.class);
    }

}