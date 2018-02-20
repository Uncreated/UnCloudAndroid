package com.uncreated.uncloud.client.view.auth;

import java.util.ArrayList;
import java.util.Set;

public interface AuthView
{
	String getJsonAuthInf();

	void setJsonAuthInf(String json);

	void setUsers(Set<String> logins);

	void selectUser(String login);

	//response
	void onAuthOk();

	void onRegisterOk();

	void onRequestTimeout();

	void onRequestIncorrect();
}
