package com.uncreated.uncloud.client.old.auth;

import com.uncreated.uncloud.client.old.Controller;
import com.uncreated.uncloud.client.old.auth.view.AuthView;

public class AuthController
		extends Controller<AuthView>
{/*
	private AuthInf selAuth;

	private AuthApiClient authApiClient;

	private boolean autoAuth = true;

	public AuthController(Retrofit retrofit)
	{
		authApiClient = new AuthApiClient(retrofit);
	}

	@Override
	public synchronized void onAttach(AuthView authView, Context context)
	{
		super.onAttach(authView, context);

		if (authManager == null)
		{
			authManager = new AuthManager(context);
			selectUser(authManager.getLastKey());
		}
		view.setUsers(authManager.getKeys());
		autoAuth = false;
	}

	public void selectUser(String login)
	{
		if (login == null)
		{
			selAuth = new AuthInf();
		}
		else
		{
			selAuth = authManager.get(login);
			view.selectUser(login, autoAuth);
		}
	}

	public void auth()
	{
		if (selAuth == null)
		{
			view.onRequestIncorrect();
			return;
		}

		authApiClient.auth(selAuth.getAccessToken(), selAuth.getUser(), view, this::authOk);
	}

	public void auth(String login, String password)
	{
		selAuth.setUser(new User(login, password));
		authApiClient.auth(selAuth.getUser(), view, this::authOk);
	}

	private void authOk(Session session)
	{
		selAuth.setAccessToken(session.getAccessToken());
		authManager.save(selAuth);
		view.onAuthOk();
	}

	public void register(String login, String password)
	{
		authApiClient.register(new User(login, password), view);
	}*/
}
