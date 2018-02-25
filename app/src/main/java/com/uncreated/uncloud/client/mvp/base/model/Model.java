package com.uncreated.uncloud.client.mvp.base.model;

import android.content.Context;

import com.uncreated.uncloud.client.mvp.model.api.auth.AuthApiClient;
import com.uncreated.uncloud.client.mvp.model.api.files.FilesApiClient;
import com.uncreated.uncloud.client.mvp.model.auth.AuthManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Model
{
	private static Model instance;

	public static void init(Context context)
	{
		if (instance == null)
		{
			instance = new Model(context);
		}
	}

	public static Model getInstance()
	{
		return instance;
	}

	public static final String SERVER_URL = "http://10.0.2.2:8080/";

	private final AuthManager authManager;
	private final AuthApiClient authApiClient;
	private final FilesApiClient filesApiClient;

	private Model(Context context)
	{

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(SERVER_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		authManager = new AuthManager(context);
		authApiClient = new AuthApiClient(retrofit);
		filesApiClient = new FilesApiClient(retrofit);

	}

	public AuthManager getAuthManager()
	{
		return authManager;
	}

	public AuthApiClient getAuthApiClient()
	{
		return authApiClient;
	}

	public FilesApiClient getFilesApiClient()
	{
		return filesApiClient;
	}
}
