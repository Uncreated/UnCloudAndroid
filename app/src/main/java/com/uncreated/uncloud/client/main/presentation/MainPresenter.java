package com.uncreated.uncloud.client.main.presentation;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.MvpPresenter;
import com.uncreated.uncloud.client.main.ui.activity.MainView;

@InjectViewState
public class MainPresenter
		extends MvpPresenter<MainView>
		implements MainView
{
	public MainPresenter()
	{
		switchFiles();
	}

	@Override
	public void switchFiles()
	{
		getViewState().switchFiles();
	}

	@Override
	public void switchSettings()
	{
		getViewState().switchSettings();
	}

	@Override
	public void switchAbout()
	{
		getViewState().switchAbout();
	}

	@Override
	public void logout()
	{
		getViewState().logout();
	}
}
