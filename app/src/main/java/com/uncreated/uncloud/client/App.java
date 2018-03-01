package com.uncreated.uncloud.client;

import android.app.Application;
import com.uncreated.uncloud.client.auth.presentation.AuthPresenter;
import com.uncreated.uncloud.client.model.Model;

public class App extends Application {
    private AuthPresenter authPresenter;

    @Override
    public void onCreate() {
        super.onCreate();

        Model.init(this);
    }

    public AuthPresenter getPresenter() {
        if (authPresenter == null) {
            authPresenter = new AuthPresenter();
        }
        return authPresenter;
    }
}
