package com.uncreated.uncloud.client.model.api.entity;

import com.uncreated.uncloud.client.model.auth.AuthInfo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private String login;
    private byte[] passwordHash;

    public User(String login, String password) {
        this.login = login;
        this.passwordHash = generatePasswordHash(password);
    }

    public User(AuthInfo authInfo) {
        this.login = authInfo.getLogin();
        this.passwordHash = authInfo.getPasswordHash();
    }

    public String getLogin() {
        return login;
    }

    public static byte[] generatePasswordHash(String password) {
        try {
            return sha512(sha512(password.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] sha512(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-512").digest(data);
    }
}
