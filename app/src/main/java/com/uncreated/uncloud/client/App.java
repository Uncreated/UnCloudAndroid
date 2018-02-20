package com.uncreated.uncloud.client;

import android.app.Application;

import com.uncreated.uncloud.client.auth.AuthController;
import com.uncreated.uncloud.client.files.FilesController;
import com.uncreated.uncloud.client.requests.RequestHandler;

public class App
		extends Application
{
	private FilesController filesController;

	private AuthController authController;

	private RequestHandler requestHandler;

	@Override
	public void onCreate()
	{
		super.onCreate();

		requestHandler = new RequestHandler();

		authController = new AuthController(requestHandler);

		filesController = new FilesController(requestHandler, getFilesDir().getAbsolutePath());
	}

	public FilesController getFilesController()
	{
		return filesController;
	}

	public AuthController getAuthController()
	{
		return authController;
	}
}
