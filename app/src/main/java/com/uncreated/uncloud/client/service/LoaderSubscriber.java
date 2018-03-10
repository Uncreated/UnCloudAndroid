package com.uncreated.uncloud.client.service;


public abstract class LoaderSubscriber {
    private String login;

    public LoaderSubscriber(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    protected abstract void onResult(String errorMessage);

    void sendResult(String errorMessage) {
        try {
            onResult(errorMessage);
        } catch (Exception e) {
            //bad idea
        }
    }
}
