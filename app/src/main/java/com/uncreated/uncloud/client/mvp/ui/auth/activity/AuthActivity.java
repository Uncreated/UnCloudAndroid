package com.uncreated.uncloud.client.mvp.ui.auth.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.mvp.base.ui.BaseActivity;
import com.uncreated.uncloud.client.mvp.presentation.auth.AuthPresenter;
import com.uncreated.uncloud.client.ui.activity.main.MainActivity;

import java.util.Set;

public class AuthActivity
		extends BaseActivity<AuthPresenter>
		implements AuthView
{

	private EditText loginEditText;
	private EditText passwordEditText;
	private Button registerButton;
	private Button authButton;
	private Button changeUserButton;

	private KeyListener loginKeyListener;
	private KeyListener passwordKeyListener;

	private Set<String> names;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		loginEditText = findViewById(R.id.loginEditText);
		passwordEditText = findViewById(R.id.passwordEditText);
		registerButton = findViewById(R.id.registerButton);
		authButton = findViewById(R.id.authButton);
		changeUserButton = findViewById(R.id.changeUserButton);

		loginKeyListener = loginEditText.getKeyListener();
		passwordKeyListener = passwordEditText.getKeyListener();


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
	protected AuthPresenter getPresenter()
	{
		return app.getPresenter();
	}

	@Override
	public void addNames(Set<String> names)
	{
		this.names = names;
		if (names.size() == 0)
		{
			changeUserButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void showError(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void showMessage(String message)
	{
		showError(message);//dbg
	}

	@Override
	public void unlockPassword()
	{
		passwordEditText.setText("");
		passwordEditText.setKeyListener(passwordKeyListener);
	}

	@Override
	public void lockPassword()
	{
		passwordEditText.setText("12345");
		passwordEditText.setKeyListener(null);
	}

	@Override
	public void unlockLogin()
	{
		loginEditText.setText("");
		loginEditText.setKeyListener(loginKeyListener);

		registerButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void lockLogin(String login)
	{
		loginEditText.setText(login);
		loginEditText.setKeyListener(null);

		registerButton.setVisibility(View.INVISIBLE);
	}

	@Override
	public void switchMainActivity()
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	public void onRegisterClick(View view)
	{
		presenter.onRegister(loginEditText.getText().toString(), passwordEditText.getText().toString());
	}

	public void onAuthClick(View view)
	{
		presenter.onAuth(loginEditText.getText().toString(), passwordEditText.getText().toString());
	}

	public void onChangeUserClick(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select user:");

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
		arrayAdapter.add("New user");
		for (String login : names)
		{
			arrayAdapter.add(login);
		}

		builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

		builder.setAdapter(arrayAdapter, (dialogInterface, i) ->
		{
			if (i == 0)
			{
				presenter.selectName(null);
				dialogInterface.dismiss();
			}
			else
			{
				presenter.selectName(arrayAdapter.getItem(i));
				dialogInterface.dismiss();
			}
		});
		builder.show();
	}
}