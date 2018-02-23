package com.uncreated.uncloud.client.auth.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.ActivityView;
import com.uncreated.uncloud.client.auth.AuthController;
import com.uncreated.uncloud.client.files.view.MainActivity;

import java.util.Set;

public class AuthActivity
		extends ActivityView<AuthController>
		implements AuthView
{
	private static final String PREF_KEY_AUTH = "prefKeyAuthActivity";
	private static final String KEY_AUTH_INF = "keyAuthInf";

	public static final String KEY_LOGIN = "loginKey";

	private EditText loginEditText;
	private EditText passwordEditText;
	private Button registerButton;
	private Button authButton;
	private Button changeUserButton;
	//private ProgressBar progressBar;

	private KeyListener loginKeyListener;
	private KeyListener passwordKeyListener;

	private SharedPreferences sharedPreferences;

	private Set<String> logins;

	private boolean withPass = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		setController(app.getAuthController());

		sharedPreferences = getSharedPreferences(PREF_KEY_AUTH, MODE_PRIVATE);

		loginEditText = findViewById(R.id.loginEditText);
		passwordEditText = findViewById(R.id.passwordEditText);
		registerButton = findViewById(R.id.registerButton);
		authButton = findViewById(R.id.authButton);
		changeUserButton = findViewById(R.id.changeUserButton);
		//progressBar = findViewById(R.id.progressBar);

		InputFilter[] filters = {(charSequence, i, i1, spanned, i2, i3) ->
		{
			for (int j = 0; j < charSequence.length(); j++)
			{
				if (!Character.isLetterOrDigit(charSequence.charAt(j)))
				{
					return "";
				}
			}
			return null;
		}};

		loginEditText.setFilters(filters);
		passwordEditText.setFilters(filters);
	}

	@Override
	public String getJsonAuthInf()
	{
		return sharedPreferences.getString(KEY_AUTH_INF, null);
	}

	@Override
	public void setJsonAuthInf(String json)
	{
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY_AUTH_INF, json);
		editor.apply();
	}

	@Override
	public void setUsers(Set<String> logins)
	{
		this.logins = logins;
		if (logins.size() == 0)
		{
			changeUserButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void selectUser(String login, boolean autoAuth)
	{
		withPass = false;

		registerButton.setVisibility(View.INVISIBLE);

		loginEditText.setText(login);
		if (loginKeyListener == null)
		{
			loginKeyListener = loginEditText.getKeyListener();
		}
		loginEditText.setKeyListener(null);

		passwordEditText.setText("12345");
		if (passwordKeyListener == null)
		{
			passwordKeyListener = passwordEditText.getKeyListener();
		}
		passwordEditText.setKeyListener(null);

		if (autoAuth)
		{
			onAuthClick(authButton);
		}
	}

	private void selectNewUser()
	{
		withPass = true;

		registerButton.setVisibility(View.VISIBLE);

		loginEditText.setText("");
		loginEditText.setKeyListener(loginKeyListener);

		passwordEditText.setText("");
		passwordEditText.setKeyListener(passwordKeyListener);
	}

	@Override
	public void onRegisterOk()
	{
		hideLoading();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("User successfully registered");
		builder.setMessage("You can authorize now");
		builder.setPositiveButton("Ok", (dialogInterface, i) ->
		{
			dialogInterface.dismiss();
		});
		builder.show();
	}

	@Override
	public void onAuthOk(String login)
	{
		controller.clear();
		app.getFilesController().setLogin(controller.getSelLogin());
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(KEY_LOGIN, login);
		startActivity(intent);
		hideLoading();
	}

	@Override
	public void onRequestTimeout()
	{
		hideLoading();
		news("Request timed out");
	}

	@Override
	public void onRequestIncorrect()
	{
		hideLoading();
		news("Incorrect login or password");
		passwordEditText.setText("");
		passwordEditText.setKeyListener(passwordKeyListener);
	}

	public void onRegisterClick(View view)
	{
		String login = loginEditText.getText().toString();
		if (login.length() > 0)
		{
			String password = passwordEditText.getText().toString();
			if (password.length() > 0)
			{
				showLoading();
				controller.register(login, password);
			}
			else
			{
				news("Empty password");
			}
		}
		else
		{
			news("Empty login");
		}
	}

	public void onAuthClick(View view)
	{
		String login = loginEditText.getText().toString();
		if (login.length() > 0)
		{
			if (withPass)
			{
				String password = passwordEditText.getText().toString();
				if (password.length() > 0)
				{
					showLoading();
					controller.auth(login, password);
				}
				else
				{
					news("Empty password");
				}
			}
			else
			{
				showLoading();
				controller.auth();
			}
		}
		else
		{
			news("Empty login");
		}
	}

	public void onChangeUserClick(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select user:");

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
		arrayAdapter.add("New user");
		for (String login : logins)
		{
			arrayAdapter.add(login);
		}

		builder.setNegativeButton("Cancel", (dialogInterface, i) ->
		{
			dialogInterface.dismiss();
		});

		builder.setAdapter(arrayAdapter, (dialogInterface, i) ->
		{
			if (i == 0)
			{
				selectNewUser();
				dialogInterface.dismiss();
			}
			else
			{
				controller.selectUser(arrayAdapter.getItem(i));
				dialogInterface.dismiss();
			}
		});
		builder.show();
	}
}
