package com.uncreated.uncloud.client;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;

public abstract class BaseActivity
		extends MvpAppCompatActivity
		implements BaseView
{
	private Base base;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		base = new Base(this);
	}

	@Override
	public void showError(String message)
	{
		base.showError(message);
	}

	@Override
	public void showMessage(String message)
	{
		base.showMessage(message);
	}

	@Override
	public void setLoading(boolean show)
	{
		base.setLoading(show);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		base.onDestroy();
	}
}
