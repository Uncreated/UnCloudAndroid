package com.uncreated.uncloud.client.model.api.entity;

public class Session {
    private String accessToken;
    private Long expiryDate;

    private String login;

    public String getAccessToken() {
        return accessToken;
    }

    public String getLogin() {
        return login;
    }
}