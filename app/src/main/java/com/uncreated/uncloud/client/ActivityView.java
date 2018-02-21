package com.uncreated.uncloud.client;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.uncreated.uncloud.R;

public abstract class ActivityView<T extends Controller>
		extends AppCompatActivity
		implements View
{
	protected App app;
	protected T controller;

	private AlertDialog alertDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		app = ((App) getApplication());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(R.layout.loading_dialog);
		builder.setOnKeyListener(null);
		alertDialog = builder.create();
		alertDialog.setCancelable(false);

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
		alertDialog.show();
	}

	protected void hideLoading()
	{
		alertDialog.hide();
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
