package com.tagmycode.cli.support;

import com.tagmycode.sdk.AbstractSecret;

public class FakeSecret extends AbstractSecret {
    @Override
    public String getConsumerId() {
        return "123";
    }

    @Override
    public String getConsumerSecret() {
        return "456";
    }
}
