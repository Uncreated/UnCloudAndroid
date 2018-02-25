package com.uncreated.uncloud.client.mvp;

import android.app.Application;

import com.uncreated.uncloud.client.mvp.base.model.Model;
import com.uncreated.uncloud.client.mvp.presentation.auth.AuthPresenter;
import com.uncreated.uncloud.client.mvp.ui.auth.activity.AuthView;

public class App
		extends Application
{
	private AuthPresenter authPresenter;

	@Override
	public void onCreate()
	{
		super.onCreate();

		Model.init(this);
	}

	public AuthPresenter getPresenter()
	{
		if (authPresenter == null)
		{
			authPresenter = new AuthPresenter();
		}
		return authPresenter;
	}
}
