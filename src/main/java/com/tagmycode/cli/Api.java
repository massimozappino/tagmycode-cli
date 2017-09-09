package com.tagmycode.cli;

import com.tagmycode.sdk.Client;
import com.tagmycode.sdk.TagMyCode;
import com.tagmycode.sdk.exception.TagMyCodeException;
import com.tagmycode.sdk.model.LanguagesCollection;
import com.tagmycode.sdk.model.Snippet;
import com.tagmycode.sdk.model.User;

import java.io.IOException;

public class Api {
    private final TagMyCode tagMyCode;

    public Api(TagMyCode tagMyCode, OauthWallet wallet) throws TagMyCodeException {
        this.tagMyCode = tagMyCode;
        wallet.createDirectory();
        tagMyCode.getClient().setWallet(wallet);
        tagMyCode.getClient().setOauthToken(wallet.loadOauthToken());
    }

    public LanguagesCollection fetchLanguages() throws TagMyCodeException {
        return tagMyCode.fetchLanguages();
    }

    public User fetchAccount() throws TagMyCodeException {
        return tagMyCode.fetchAccount();
    }

    public Client getClient() {
        return tagMyCode.getClient();
    }

    public void authorizeWithVerificationCode(String code) throws TagMyCodeException {
        getClient().fetchOauthToken(code);
    }

    public String getAuthorizationUrl() {
        return getClient().getAuthorizationUrl();
    }

    public boolean isAuthenticated() {
        return getClient().isAuthenticated();
    }

    public Snippet createSnippetFromFile(FileSnippet fileSnippet, boolean isPrivate) throws TagMyCodeException, IOException {
        Snippet snippet = fileSnippet.buildSnippet();
        snippet.setPrivate(isPrivate);
        return tagMyCode.createSnippet(snippet);
    }

}
