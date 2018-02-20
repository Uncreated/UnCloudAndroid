package com.uncreated.uncloud.client.view.auth;

public interface AuthController
{
	void attach(AuthView authView);

	void selectUser(String login);

	void auth();

	void auth(String login, String password);

	void register(String login, String password);
}
