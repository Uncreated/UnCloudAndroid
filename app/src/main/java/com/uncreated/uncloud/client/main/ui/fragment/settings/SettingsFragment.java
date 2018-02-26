package com.uncreated.uncloud.client.main.ui.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.BaseFragment;
import com.uncreated.uncloud.client.main.presentation.SettingsPresenter;

public class SettingsFragment
		extends BaseFragment
		implements SettingsView
{
	@InjectPresenter
	SettingsPresenter settingsPresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_settings, container, false);
	}
}
