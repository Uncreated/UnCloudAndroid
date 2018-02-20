package com.uncreated.uncloud.client.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.uncreated.uncloud.client.App;
import com.uncreated.uncloud.client.RequestStatus;
import com.uncreated.uncloud.common.filestorage.FolderNode;

public abstract class ClientActivity
		extends AppCompatActivity
		implements ClientView
{

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		((App) getApplication()).getClientController().setClientView(this);
	}

	@Override
	public void call(Runnable runnable)
	{
		runOnUiThread(runnable);
	}

	@Override
	public void onRegister(RequestStatus requestStatus)
	{
		reqStatus("onRegister", requestStatus);
	}

	@Override
	public void onAuth(RequestStatus<String> requestStatus)
	{
		reqStatus("onAuth", requestStatus);
	}

	@Override
	public void onUpdateFiles(FolderNode mergedFiles)
	{

	}

	@Override
	public void onFailRequest(RequestStatus requestStatus)
	{
		news(requestStatus.getMsg());
	}

	@Override
	public void onLogout()
	{

	}

	public void news(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	public void reqStatus(String msg, RequestStatus requestStatus)
	{
		if (requestStatus.isOk())
		{
			news(msg);
		}
		else
		{
			news(msg + "\n" + requestStatus.getMsg());
		}
	}
}
