package com.uncreated.uncloud.client.mvp.presentation.auth;

import com.uncreated.uncloud.client.mvp.base.model.Model;
import com.uncreated.uncloud.client.mvp.base.presentation.Presenter;
import com.uncreated.uncloud.client.mvp.model.api.CallbackCodes;
import com.uncreated.uncloud.client.mvp.model.api.auth.AuthApiClient;
import com.uncreated.uncloud.client.mvp.model.api.auth.Session;
import com.uncreated.uncloud.client.mvp.model.api.auth.User;
import com.uncreated.uncloud.client.mvp.model.auth.AuthInf;
import com.uncreated.uncloud.client.mvp.model.auth.AuthManager;
import com.uncreated.uncloud.client.mvp.ui.auth.activity.AuthView;

public class AuthPresenter
		extends Presenter<AuthView>
{
	//ViewState
	private AuthView authView;

	//Model
	private AuthManager authManager;
	private AuthApiClient authApiClient;


	private boolean withPass = true;


	public AuthPresenter()
	{
		super(new AuthViewState());
		authView = (AuthViewState)viewState;

		authManager = Model.getInstance().getAuthManager();
		authApiClient = Model.getInstance().getAuthApiClient();

		selectName(authManager.getLastKey());
		authView.addNames(authManager.getKeys());
	}

	public void selectName(String name)
	{
		if (withPass = name == null)
		{
			authView.unlockLogin();
			authView.unlockPassword();
		}
		else
		{
			authView.lockLogin(name);
			authView.lockPassword();
		}
	}

	public void onRegister(String login, String password)
	{
		if (login.length() > 0)
		{
			if (password.length() > 0)
			{
				authView.showLoading();
				authApiClient.register(new User(login, password), new CallbackCodes<Void>()
						.setOnCompleteEvent(body ->
						{
							authView.hideLoading();
							authView.showMessage("You are successfully registered");
						})
						.setOnFailedEvent(this::onFailed)
						.add(400, this::on400)
						.add(408, this::on408));
			}
			else
			{
				authView.showError("Empty password");
			}
		}
		else
		{
			authView.showError("Empty login");
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
					authView.showError("Empty password");
					return;
				}
			}
			else
			{
				authInf = authManager.get(login);
			}
			authView.showLoading();
			authApiClient.auth(authInf, new CallbackCodes<Session>()
					.setOnCompleteEvent(body ->
					{
						authInf.setAccessToken(body.getAccessToken());
						authManager.save(authInf);
						authView.switchMainActivity();
					})
					.setOnFailedEvent(this::onFailed)
					.add(400, this::on400)
					.add(408, this::on408));
		}
		else
		{
			authView.showError("Empty login");
		}
	}

	private void onFailed(String message)
	{
		authView.hideLoading();
		authView.showError(message);
	}

	private void on400()
	{
		authView.unlockPassword();
		authView.hideLoading();
		authView.showError("Incorrect login or password");
	}

	private void on408()
	{
		authView.hideLoading();
		authView.showError("Request timeout");
	}
}
