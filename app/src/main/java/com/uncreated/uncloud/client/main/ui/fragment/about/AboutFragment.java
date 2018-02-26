package com.uncreated.uncloud.client.main.ui.fragment.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.BaseFragment;
import com.uncreated.uncloud.client.main.presentation.AboutPresenter;

public class AboutFragment
		extends BaseFragment
		implements AboutView
{
	@InjectPresenter
	AboutPresenter aboutPresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_about, container, false);
	}
}
