package com.uncreated.uncloud.client.model.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class AuthManager
{
	private static final String PREF_KEY_AUTH = "prefKeyAuthManager";
	private static final String KEY_AUTH_INF_BOX = "keyAuthInfBox";

	private SharedPreferences sharedPreferences;

	private AuthInfBox authInfBox;
	private Gson gson;

	public AuthManager(Context context)
	{
		this.gson = new Gson();

		sharedPreferences = context.getSharedPreferences(PREF_KEY_AUTH, MODE_PRIVATE);

		String json = sharedPreferences.getString(KEY_AUTH_INF_BOX, null);
		if (json != null)
		{
			try
			{
				authInfBox = gson.fromJson(json, AuthInfBox.class);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (authInfBox == null)
		{
			authInfBox = new AuthInfBox();
		}
	}

	public Set<String> getKeys()
	{
		return authInfBox.authInfMap.keySet();
	}

	public String getLastKey()
	{
		return authInfBox.keyLast;
	}

	public AuthInf get(String key)
	{
		return authInfBox.authInfMap.get(key);
	}

	public void save(AuthInf authInf)
	{
		authInfBox.keyLast = authInf.getUser().getLogin();
		if (!authInfBox.authInfMap.containsValue(authInf))
		{
			authInfBox.authInfMap.put(authInfBox.keyLast, authInf);
		}

		String json = gson.toJson(authInfBox);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY_AUTH_INF_BOX, json);
		editor.apply();
	}

	private class AuthInfBox
	{
		HashMap<String, AuthInf> authInfMap;
		String keyLast;

		AuthInfBox()
		{
			authInfMap = new HashMap<>();
		}
	}
}
