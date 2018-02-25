package com.uncreated.uncloud.client.auth.ui.activity;


import com.arellomobile.mvp.MvpView;

import java.util.Set;

public interface AuthView
		extends MvpView
{
	void addNames(Set<String> names);

	void showError(String message);

	void showMessage(String message);

	void setPassword(boolean lock);

	void setLogin(String login, boolean lock);

	void setLoading(boolean show);

	void switchMainActivity();
}
