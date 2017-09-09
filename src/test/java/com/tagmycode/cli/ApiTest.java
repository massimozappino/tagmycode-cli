package com.tagmycode.cli;

import com.tagmycode.cli.support.FakeSecret;
import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.authentication.OauthToken;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import support.ResourceGenerate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApiTest {

    private ResourceGenerate resourceGenerate;

    @Before
    public void initialize() {
        resourceGenerate = new ResourceGenerate();
    }

    @Test
    public void configCreateDirectoryInConstructor() throws Exception {
        OauthWallet configMock = createConfigMock();
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
        LanguagesCollection expectedLanguages = new ResourceGenerate().aLanguageCollection();
        when(tagMyCodeMock.fetchLanguages()).thenReturn(expectedLanguages);
        Api apiSpy = spy(new Api(tagMyCodeMock, createConfigMock()));

        LanguagesCollection actualLanguages = apiSpy.fetchLanguages();

        verify(tagMyCodeMock).fetchLanguages();
        assertEquals(expectedLanguages, actualLanguages);
    }

    @Test
    public void fetchAccount() throws Exception {
        TagMyCode tagMyCodeMock = createTagMyCodeMock();
        User expectedUser = resourceGenerate.aUser();
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
        OauthWallet configMock = createConfigMock();
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
        OauthWallet oauthWalletMock = createConfigMock();
        Client client = new Client(new FakeSecret(), oauthWalletMock);
        when(oauthWalletMock.loadOauthToken()).thenReturn(new OauthToken("123", "456"));

        Api api = new Api(new TagMyCode(client), oauthWalletMock);

        verify(oauthWalletMock, times(1)).loadOauthToken();
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

    private OauthWallet createConfigMock() {
        return mock(OauthWallet.class);
    }

}