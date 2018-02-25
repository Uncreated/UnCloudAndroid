package com.uncreated.uncloud.client.mvp.base.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.mvp.App;
import com.uncreated.uncloud.client.mvp.base.presentation.Presenter;

public abstract class BaseActivity<P extends Presenter>
		extends AppCompatActivity
		implements View
{
	protected P presenter;
	protected App app;

	private AlertDialog alertDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		app = ((App) getApplication());
		presenter = getPresenter();
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		presenter.attach(this);
	}

	protected abstract P getPresenter();


	@Override
	public void showLoading()
	{
		if (alertDialog == null)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(R.layout.loading_dialog);
			builder.setOnKeyListener(null);
			alertDialog = builder.create();
			alertDialog.setCancelable(false);
			alertDialog.show();
		}
	}

	@Override
	public void hideLoading()
	{
		if (alertDialog != null)
		{
			alertDialog.dismiss();
			alertDialog = null;
		}
	}
}
