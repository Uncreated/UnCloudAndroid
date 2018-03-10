package com.uncreated.uncloud.client.model.auth;

import android.content.Context;
import android.content.SharedPreferences;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;

public class AuthManager {
    private static final String PREF_KEY_AUTH = "prefKeyAuthManager";
    private static final String KEY_LAST_LOGIN = "keyLastLogin";

    private SharedPreferences sharedPreferences;
    private Realm realm;

    public AuthManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_KEY_AUTH, MODE_PRIVATE);

        realm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("UnCloud.AuthManager")
                .schemaVersion(1)
                .build());
    }

    public String[] getNames() {
        RealmResults<AuthInfo> authInfoList = realm.where(AuthInfo.class).findAll();

        if (authInfoList.size() == 0)
            return null;

        String[] names = new String[authInfoList.size()];
        for (int i = 0; i < authInfoList.size(); i++) {
            names[i] = authInfoList.get(i).getLogin();
        }
        return names;
    }

    public String getLastName() {
        return sharedPreferences.getString(KEY_LAST_LOGIN, null);
    }

    public AuthInfo get(String key) {
        AuthInfo authInfo = realm.where(AuthInfo.class)
                .equalTo("login", key)
                .findFirst();

        return authInfo;
    }

    public void save(AuthInfo authInfo, String accessToken) {
        realm.executeTransaction(realm1 -> {
            authInfo.setAccessToken(accessToken);
            realm1.insertOrUpdate(authInfo);
        });

        sharedPreferences.edit()
                .putString(KEY_LAST_LOGIN, authInfo.getLogin())
                .apply();
    }
}
