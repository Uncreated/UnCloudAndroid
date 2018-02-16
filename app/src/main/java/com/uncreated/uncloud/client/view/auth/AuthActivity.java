package com.uncreated.uncloud.client.view.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.App;
import com.uncreated.uncloud.client.RequestStatus;
import com.uncreated.uncloud.client.view.ClientActivity;
import com.uncreated.uncloud.client.view.files.FilesActivity;

public class AuthActivity
		extends ClientActivity
{
	private static final String PREF_KEY = "authActivityPrefKey";
	private static final String REMEMBER_KEY = "rememberKey";
	private static final String LOGIN_KEY = "loginKey";
	private static final String PASSWORD_KEY = "passwordKey";

	private EditText loginEditText;
	private EditText passwordEditText;
	private Button registerButton;
	private Button authButton;
	private CheckBox rememberCheckBox;
	private ProgressBar progressBar;

	private boolean autoAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		loginEditText = findViewById(R.id.loginEditText);
		passwordEditText = findViewById(R.id.passwordEditText);
		registerButton = findViewById(R.id.registerButton);
		authButton = findViewById(R.id.authButton);
		rememberCheckBox = findViewById(R.id.rememberCheckBox);
		progressBar = findViewById(R.id.progressBar);

		SharedPreferences sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
		autoAuth = sharedPreferences.getBoolean(REMEMBER_KEY, false);
		if (autoAuth)
		{
			rememberCheckBox.setChecked(true);
			loginEditText.setText(sharedPreferences.getString(LOGIN_KEY, ""));
			passwordEditText.setText(sharedPreferences.getString(PASSWORD_KEY, ""));
		}
		else
		{
			setLoading(false);
		}
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();

		autoAuth = false;
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		if (rememberCheckBox.isChecked() && autoAuth)
		{
			onAuthClick(authButton);
		}
		else
		{
			setLoading(false);
		}
	}

	private void setLoading(boolean on)
	{
		loginEditText.setEnabled(!on);
		passwordEditText.setEnabled(!on);
		registerButton.setEnabled(!on);
		authButton.setEnabled(!on);
		rememberCheckBox.setEnabled(!on);
		progressBar.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
	}

	public void onRegisterClick(View view)
	{
	}

	public void onAuthClick(View view)
	{
		setLoading(true);
		String login = loginEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (login.length() > 0 && password.length() > 0)
		{
			SharedPreferences.Editor editor = getSharedPreferences(PREF_KEY, MODE_PRIVATE).edit();
			editor.putBoolean(REMEMBER_KEY, rememberCheckBox.isChecked());
			if (!rememberCheckBox.isChecked())
			{
				editor.remove(LOGIN_KEY);
				editor.remove(PASSWORD_KEY);
			}
			else
			{
				editor.putString(LOGIN_KEY, login);
				editor.putString(PASSWORD_KEY, password);
			}
			editor.apply();
			((App) getApplication()).getClientController().auth(login, password);
		}
	}

	@Override
	public void onAuth(RequestStatus requestStatus)
	{
		if (requestStatus.isOk())
		{
			Intent intent = new Intent(this, FilesActivity.class);
			startActivity(intent);
		}
		else
		{
			setLoading(false);
		}
	}
}
