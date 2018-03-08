package com.uncreated.uncloud.client.model.auth;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;

public class AuthManager {
    private static final String PREF_KEY_AUTH = "prefKeyAuthManager";
    private static final String KEY_LAST_LOGIN = "keyLastLogin";

    private SharedPreferences sharedPreferences;
    private RealmConfiguration realmConfiguration;

    public AuthManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_KEY_AUTH, MODE_PRIVATE);

        realmConfiguration = new RealmConfiguration.Builder()
                .name("UnCloud.AuthManager")
                .schemaVersion(1)
                .build();
    }

    public String[] getNames() {
        Realm realm = Realm.getInstance(realmConfiguration);
        RealmResults<AuthInf> authInfList = realm.where(AuthInf.class).findAll();

        if (authInfList.size() == 0)
            return null;

        String[] names = new String[authInfList.size()];
        for (int i = 0; i < authInfList.size(); i++) {
            names[i] = authInfList.get(i).getLogin();
        }
        realm.close();
        return names;
    }

    public String getLastName() {
        return sharedPreferences.getString(KEY_LAST_LOGIN, null);
    }

    public AuthInf get(String key) {
        Realm realm = Realm.getInstance(realmConfiguration);
        AuthInf authInf = realm.where(AuthInf.class)
                .equalTo("login", key)
                .findFirst();
        realm.close();

        return authInf;
    }

    public void save(AuthInf authInf) {
        Realm realm = Realm.getInstance(realmConfiguration);
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(authInf));
        realm.close();

        sharedPreferences.edit()
                .putString(KEY_LAST_LOGIN, authInf.getLogin())
                .apply();
    }
}
