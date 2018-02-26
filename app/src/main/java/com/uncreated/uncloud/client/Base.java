package com.uncreated.uncloud.client;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.uncreated.uncloud.R;

public class Base
		implements BaseView
{
	private AlertDialog alertDialog;
	private Context context;

	public Base(Context context)
	{
		this.context = context;

		alertDialog = new AlertDialog.Builder(context)
				.setView(R.layout.loading_dialog)
				.setOnKeyListener(null)
				.setCancelable(false)
				.create();
	}

	@Override
	public void showError(String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void showMessage(String message)
	{
		showError(message);//dbg
	}

	@Override
	public void setLoading(boolean show)
	{
		Log.e("setLoading", show + "");
		if (show)
		{
			alertDialog.show();
			Log.e("setLoading", "show");
		}
		else
		{
			alertDialog.hide();
			Log.e("setLoading", "hide");
		}
	}

	public void onDestroy()
	{
		alertDialog.dismiss();
	}
}
