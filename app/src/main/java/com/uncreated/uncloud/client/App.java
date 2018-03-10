package com.uncreated.uncloud.client;

import android.app.Application;
import android.content.Context;

import com.uncreated.uncloud.client.model.Model;

import io.realm.Realm;

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        Realm.init(this);

        Model.init(this);
    }
}
