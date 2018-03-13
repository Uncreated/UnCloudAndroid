package com.uncreated.uncloud.client.service;

public abstract class LoaderSubscriber {
    private final String login;

    protected LoaderSubscriber(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public abstract void onResult(String errorMessage);
}
