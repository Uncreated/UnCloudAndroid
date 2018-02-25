package com.uncreated.uncloud.client.mvp.model.api.auth;

public class Session
{
	public static Session current;

	private String accessToken;
	private Long expiryDate;

	private String login;

	public String getAccessToken()
	{
		return accessToken;
	}

	public String getLogin()
	{
		return login;
	}
}