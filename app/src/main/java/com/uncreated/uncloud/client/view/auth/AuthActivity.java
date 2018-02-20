package com.uncreated.uncloud.client.view.auth;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.App;
import com.uncreated.uncloud.client.view.ClientActivity;

import java.util.Set;

public class AuthActivity
		extends ClientActivity
		implements AuthView
{
	private static final String PREF_KEY_AUTH = "prefKeyAuthActivity";
	private static final String KEY_AUTH_INF = "keyAuthInf";

	private EditText loginEditText;
	private EditText passwordEditText;
	private Button registerButton;
	private Button authButton;
	private Button changeUserButton;
	private ProgressBar progressBar;

	private KeyListener loginKeyListener;
	private KeyListener passwordKeyListener;

	private SharedPreferences sharedPreferences;

	private App app;

	private AuthController authController;

	private Set<String> logins;

	private boolean autoAuth = true;
	private boolean withPass = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		sharedPreferences = getSharedPreferences(PREF_KEY_AUTH, MODE_PRIVATE);

		loginEditText = findViewById(R.id.loginEditText);
		passwordEditText = findViewById(R.id.passwordEditText);
		registerButton = findViewById(R.id.registerButton);
		authButton = findViewById(R.id.authButton);
		changeUserButton = findViewById(R.id.changeUserButton);
		progressBar = findViewById(R.id.progressBar);

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

		/*passwordEditText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
			{

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
			{

			}

			@Override
			public void afterTextChanged(Editable editable)
			{
				withPass = true;
			}
		});*/

		app = ((App) getApplication());
		authController = app.getClientController();

		authController.attach(this);
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
		//Получили список пользователей, добавим его в какой-нибудь список
		this.logins = logins;
		if (logins.size() == 0)
		{
			changeUserButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void selectUser(String login)
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
		setLoading(false);
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
	public void onAuthOk()
	{
		/*Intent intent = new Intent(this, FilesActivity.class);
		startActivity(intent);*/
		setLoading(false);
		news("onAuthOk");
	}

	@Override
	public void onRequestTimeout()
	{
		setLoading(false);
		news("Request timed out");
	}

	@Override
	public void onRequestIncorrect()
	{
		setLoading(false);
		news("Incorrect login or password");
		passwordEditText.setText("");
		passwordEditText.setKeyListener(passwordKeyListener);
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

		if (autoAuth && !withPass)
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
		changeUserButton.setEnabled(!on);
		progressBar.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
	}

	public void onRegisterClick(View view)
	{
		String login = loginEditText.getText().toString();
		if (login.length() > 0)
		{
			String password = passwordEditText.getText().toString();
			if (password.length() > 0)
			{
				setLoading(true);
				authController.register(login, password);
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
					setLoading(true);
					authController.auth(login, password);
				}
				else
				{
					news("Empty password");
				}
			}
			else
			{
				authController.auth();
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
				authController.selectUser(arrayAdapter.getItem(i));
				dialogInterface.dismiss();
			}
		});
		builder.show();
	}
}
