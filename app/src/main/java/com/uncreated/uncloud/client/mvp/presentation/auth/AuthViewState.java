package com.uncreated.uncloud.client.mvp.presentation.auth;

import com.uncreated.uncloud.client.mvp.base.presentation.ViewState;
import com.uncreated.uncloud.client.mvp.ui.auth.activity.AuthView;

import java.util.Set;

public class AuthViewState
		extends ViewState<AuthView>
		implements AuthView
{
	@Override
	public void addNames(Set<String> names)
	{
		append(new Command(1)
		{
			@Override
			public void run(AuthView view)
			{
				view.addNames(names);
			}
		});
	}

	@Override
	public void showError(String message)
	{
		skip(new Command(0)
		{
			@Override
			public void run(AuthView view)
			{
				view.showError(message);
			}
		});
	}

	@Override
	public void showMessage(String message)
	{
		skip(new Command(0)
		{
			@Override
			public void run(AuthView view)
			{
				view.showMessage(message);
			}
		});
	}

	@Override
	public void unlockPassword()
	{
		appendUnique(new Command(2)
		{
			@Override
			public void run(AuthView view)
			{
				view.unlockPassword();
			}
		});
	}

	@Override
	public void lockPassword()
	{
		appendUnique(new Command(2)
		{
			@Override
			public void run(AuthView view)
			{
				view.lockPassword();
			}
		});
	}

	@Override
	public void unlockLogin()
	{
		appendUnique(new Command(3)
		{
			@Override
			public void run(AuthView view)
			{
				view.unlockLogin();
			}
		});
	}

	@Override
	public void lockLogin(String login)
	{
		appendUnique(new Command(3)
		{
			@Override
			public void run(AuthView view)
			{
				view.lockLogin(login);
			}
		});
	}

	@Override
	public void switchMainActivity()
	{
		skip(new Command(0)
		{
			@Override
			public void run(AuthView view)
			{
				view.switchMainActivity();
			}
		});
	}

	@Override
	public void showLoading()
	{
		appendUnique(new Command(4)
		{
			@Override
			public void run(AuthView view)
			{
				view.showLoading();
			}
		});
	}

	@Override
	public void hideLoading()
	{
		appendUnique(new Command(4)
		{
			@Override
			public void run(AuthView view)
			{
				view.hideLoading();
			}
		});
	}
}
