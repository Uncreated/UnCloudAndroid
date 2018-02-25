package com.uncreated.uncloud.client.mvp.model.auth;

import com.uncreated.uncloud.client.mvp.model.api.auth.User;

public class AuthInf
{
	private User user;
	private String accessToken;

	public AuthInf()
	{
	}

	public AuthInf(User user, String accessToken)
	{
		this.user = user;
		this.accessToken = accessToken;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getAccessToken()
	{
		return accessToken;
	}

	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}
}
