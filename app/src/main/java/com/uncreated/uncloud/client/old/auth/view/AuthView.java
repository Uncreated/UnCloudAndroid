package com.uncreated.uncloud.client.old.auth.view;

import com.uncreated.uncloud.client.old.View;

import java.util.Set;

public interface AuthView
		extends View
{
	void setUsers(Set<String> logins);

	void selectUser(String login, boolean autoAuth);

	//response
	void onAuthOk();

	void onRegisterOk();

	void onRequestTimeout();

	void onRequestIncorrect();

	void onRequestFail();
}
