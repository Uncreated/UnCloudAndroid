package com.uncreated.uncloud.client;

import android.os.Bundle;
import android.view.MotionEvent;

import com.arellomobile.mvp.MvpFragment;

public abstract class BaseFragment
		extends MvpFragment
		implements BaseView
{
	private Base base;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		base = new Base(getActivity());
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

	public boolean dispatchTouchEvent(MotionEvent event)
	{
		return false;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		base.onDestroy();
	}
}
