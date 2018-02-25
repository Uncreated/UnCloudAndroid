package com.uncreated.uncloud.client.model;

import android.content.Context;

import com.uncreated.uncloud.client.model.api.ApiClient;
import com.uncreated.uncloud.client.model.auth.AuthManager;

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
	private final ApiClient apiClient;

	private Model(Context context)
	{

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(SERVER_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		authManager = new AuthManager(context);
		apiClient = new ApiClient(retrofit);

	}

	public AuthManager getAuthManager()
	{
		return authManager;
	}

	public ApiClient getApiClient()
	{
		return apiClient;
	}
}
