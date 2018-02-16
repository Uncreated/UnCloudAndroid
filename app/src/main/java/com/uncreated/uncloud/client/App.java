package com.uncreated.uncloud.client;

import android.app.Application;

public class App
		extends Application
{
	private ClientController clientController;

	@Override
	public void onCreate()
	{
		super.onCreate();

		clientController = new ClientController(getFilesDir().getAbsolutePath());
	}

	public ClientController getClientController()
	{
		return clientController;
	}
}
