package com.uncreated.uncloud.client.mvp.model.api.auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthApi
{
	@POST("api/register")
	Call<Void> postRegister(@Body User user);

	@POST("api/auth")
	Call<Session> postAuth(@Body User user);

	@PUT("api/auth")
	Call<Session> putAuth(@Header("Authorization") String accessToken);
}
