package com.uncreated.uncloud.client.model.api.entity;

import com.uncreated.uncloud.client.model.auth.AuthInf;

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

    public User(AuthInf authInf) {
        this.login = authInf.getLogin();
        this.passwordHash = authInf.getPasswordHash();
    }

    public String getLogin() {
        return login;
    }

    public static byte[] generatePasswordHash(String password) {
        try {
            //sha512(sha512(pass))
            byte[] passBytes = password.getBytes("UTF-8");
            byte[] sha512a = sha512(passBytes);
            byte[] sha512b = sha512(sha512a);
            return sha512b;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] sha512(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-512").digest(data);
    }
}
