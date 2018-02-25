package com.uncreated.uncloud.client.mvp.model.api.auth;

import com.uncreated.uncloud.client.mvp.model.api.CallbackCodes;
import com.uncreated.uncloud.client.mvp.model.auth.AuthInf;

import retrofit2.Retrofit;

public class AuthApiClient
{
	private AuthApi authApi;

	public AuthApiClient(Retrofit retrofit)
	{
		this.authApi = retrofit.create(AuthApi.class);
	}

	public void register(User user, CallbackCodes<Void> callback)
	{
		authApi.postRegister(user).enqueue(callback);
	}

	public void auth(AuthInf authInf, CallbackCodes<Session> callback)
	{
		String accessToken = authInf.getAccessToken();
		if (accessToken != null)
		{
			authApi.putAuth(accessToken).enqueue(new CallbackCodes<Session>()
					.setOnCompleteEvent(callback.getOnCompleteEvent())
					.setOnFailedEvent(message -> authApi.postAuth(authInf.getUser()).enqueue(callback)));
		}
		else
		{
			authApi.postAuth(authInf.getUser()).enqueue(callback);
		}
	}
}
