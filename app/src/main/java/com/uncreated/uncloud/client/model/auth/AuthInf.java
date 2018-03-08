package com.uncreated.uncloud.client.model.auth;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class AuthInf extends RealmObject {

    @PrimaryKey
    @Required
    private String login;
    private byte[] passwordHash;
    private String accessToken;

    public AuthInf() {
    }

    public AuthInf(String login, byte[] passwordHash, String accessToken) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.accessToken = accessToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
