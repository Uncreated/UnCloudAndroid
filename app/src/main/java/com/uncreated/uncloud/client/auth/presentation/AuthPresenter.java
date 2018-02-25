package com.uncreated.uncloud.client.auth.presentation;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.uncreated.uncloud.client.auth.ui.activity.AuthView;
import com.uncreated.uncloud.client.model.Model;
import com.uncreated.uncloud.client.model.api.ApiClient;
import com.uncreated.uncloud.client.model.api.CallbackCodes;
import com.uncreated.uncloud.client.model.api.entity.Session;
import com.uncreated.uncloud.client.model.api.entity.User;
import com.uncreated.uncloud.client.model.auth.AuthInf;
import com.uncreated.uncloud.client.model.auth.AuthManager;

@InjectViewState
public class AuthPresenter
		extends MvpPresenter<AuthView>
{
	//Model
	private AuthManager authManager;
	private ApiClient apiClient;


	private boolean withPass = true;


	public AuthPresenter()
	{
		authManager = Model.getInstance().getAuthManager();
		apiClient = Model.getInstance().getApiClient();

		onBack();
	}

	public void onBack()
	{
		selectName(authManager.getLastKey());
		getViewState().addNames(authManager.getKeys());
		getViewState().setLoading(false);
	}

	public void selectName(String name)
	{
		if (withPass = name == null)
		{
			getViewState().setLogin("", false);
			getViewState().setPassword(false);
		}
		else
		{
			getViewState().setLogin(name, true);
			getViewState().setPassword(true);
		}
	}

	public void onRegister(String login, String password)
	{
		if (login.length() > 0)
		{
			if (password.length() > 0)
			{
				getViewState().setLoading(true);
				apiClient.register(new User(login, password), new CallbackCodes<Void>()
						.setOnCompleteEvent(body ->
						{
							getViewState().setLoading(false);
							getViewState().showMessage("You are successfully registered");
						})
						.setOnFailedEvent(this::onFailed)
						.add(400, this::on400)
						.add(408, this::on408));
			}
			else
			{
				getViewState().showError("Empty password");
			}
		}
		else
		{
			getViewState().showError("Empty login");
		}
	}

	public void onAuth(String login, String password)
	{
		if (login.length() > 0)
		{
			AuthInf authInf;
			if (withPass)
			{
				if (password.length() > 0)
				{
					authInf = new AuthInf(new User(login, password), null);
				}
				else
				{
					getViewState().showError("Empty password");
					return;
				}
			}
			else
			{
				authInf = authManager.get(login);
			}
			getViewState().setLoading(true);
			apiClient.auth(authInf, new CallbackCodes<Session>()
					.setOnCompleteEvent(body ->
					{
						Session.current = body;
						authInf.setAccessToken(body.getAccessToken());
						authManager.save(authInf);
						getViewState().switchMainActivity();
					})
					.setOnFailedEvent(this::onFailed)
					.add(400, this::on400)
					.add(408, this::on408));
		}
		else
		{
			getViewState().showError("Empty login");
		}
	}

	private void onFailed(String message)
	{
		getViewState().setLoading(false);
		getViewState().showError(message);
	}

	private void on400()
	{
		getViewState().setPassword(false);
		getViewState().setLoading(false);
		getViewState().showError("Incorrect login or password");
	}

	private void on408()
	{
		getViewState().setLoading(false);
		getViewState().showError("Request timeout");
	}
}
