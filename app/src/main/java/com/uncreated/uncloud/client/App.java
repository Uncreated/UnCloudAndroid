package com.uncreated.uncloud.client;

import android.app.Application;

import com.uncreated.uncloud.client.model.Model;

import io.realm.Realm;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        Model.init(this);
    }
}
