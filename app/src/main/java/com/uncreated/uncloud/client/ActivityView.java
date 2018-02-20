package com.uncreated.uncloud.client;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public abstract class ActivityView<T extends Controller>
		extends AppCompatActivity
		implements View
{
	protected App app;
	protected T controller;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		app = ((App) getApplication());
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		controller.onAttach(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		controller.onDetach();
	}

	protected void showLoading()
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.show();
	}

	protected void hideLoading()
	{
		if (progressDialog != null)
		{
			progressDialog.hide();
		}
	}

	protected void setController(T controller)
	{
		this.controller = controller;
	}

	public void call(Runnable runnable)
	{
		runOnUiThread(runnable);
	}

	protected void news(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}
