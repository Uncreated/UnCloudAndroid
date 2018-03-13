package com.uncreated.uncloud.client.model.auth;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;

public class AuthManager {
    private static final String PREF_KEY_AUTH = "prefKeyAuthManager";
    private static final String KEY_LAST_LOGIN = "keyLastLogin";

    private final SharedPreferences sharedPreferences;
    private final Realm realm;

    public AuthManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_KEY_AUTH, MODE_PRIVATE);

        realm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("UnCloud.AuthManager")
                .schemaVersion(1)
                .build());
    }

    public List<String> getNames() {
        RealmResults<AuthInfo> authInfoList = realm.where(AuthInfo.class).findAll();

        if (authInfoList.size() == 0)
            return null;

        List<String> names = new LinkedList<>();
        for (int i = 0; i < authInfoList.size(); i++) {
            AuthInfo authInfo = authInfoList.get(i);
            if (authInfo == null) {
                authInfoList.deleteFromRealm(i);
            } else {
                names.add(authInfo.getLogin());
            }
        }
        return names;
    }

    public String getLastName() {
        return sharedPreferences.getString(KEY_LAST_LOGIN, null);
    }

    public AuthInfo get(String key) {
        return realm.where(AuthInfo.class)
                .equalTo("login", key)
                .findFirst();
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
