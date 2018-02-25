package com.uncreated.uncloud.client.mvp.ui.auth.activity;


import com.uncreated.uncloud.client.mvp.base.ui.View;

import java.util.Set;

public interface AuthView
		extends View
{
	void addNames(Set<String> names);

	void showError(String message);

	void showMessage(String message);

	void unlockPassword();

	void lockPassword();

	void unlockLogin();

	void lockLogin(String login);

	void switchMainActivity();
}
