package com.uncreated.uncloud.client.model.auth;

import com.uncreated.uncloud.client.model.api.entity.User;

public class AuthInf {
    private User user;
    private String accessToken;

    public AuthInf() {
    }

    public AuthInf(User user, String accessToken) {
        this.user = user;
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
