package com.uncreated.uncloud.client.auth.ui.activity;

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

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.BaseActivity;
import com.uncreated.uncloud.client.auth.presentation.AuthPresenter;
import com.uncreated.uncloud.client.main.ui.activity.MainActivity;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity
		extends BaseActivity
		implements AuthView
{
	@BindView(R.id.login_edit_text)
	EditText loginEditText;

	@BindView(R.id.password_edit_text)
	EditText passwordEditText;

	@BindView(R.id.register_button)
	Button registerButton;

	@BindView(R.id.auth_button)
	Button authButton;

	@BindView(R.id.change_user_button)
	Button changeUserButton;

	private KeyListener loginKeyListener;
	private KeyListener passwordKeyListener;

	private Set<String> names;

	private AlertDialog alertDialog;

	@InjectPresenter(type = PresenterType.GLOBAL, tag = "AuthPresenter")
	AuthPresenter mAuthPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		ButterKnife.bind(this);

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
	public void setPassword(boolean lock)
	{
		if (lock)
		{
			passwordEditText.setText("12345");
			passwordEditText.setKeyListener(null);
		}
		else
		{
			passwordEditText.setText("");
			passwordEditText.setKeyListener(passwordKeyListener);
		}
	}

	@Override
	public void setLogin(String login, boolean lock)
	{
		if (lock)
		{
			loginEditText.setText(login);
			loginEditText.setKeyListener(null);

			registerButton.setVisibility(View.INVISIBLE);
		}
		else
		{
			loginEditText.setText("");
			loginEditText.setKeyListener(loginKeyListener);

			registerButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void setLoading(boolean show)
	{
		if (show)
		{
			if (alertDialog == null)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(R.layout.loading_dialog);
				builder.setOnKeyListener(null);
				alertDialog = builder.create();
				alertDialog.setCancelable(false);
				alertDialog.show();
			}
		}
		else
		{
			if (alertDialog != null)
			{
				alertDialog.dismiss();
				alertDialog = null;
			}
		}
	}

	@Override
	public void switchMainActivity()
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		mAuthPresenter.onBack();
	}

	public void onRegisterClick(View view)
	{
		mAuthPresenter.onRegister(loginEditText.getText().toString(), passwordEditText.getText().toString());
	}

	public void onAuthClick(View view)
	{
		mAuthPresenter.onAuth(loginEditText.getText().toString(), passwordEditText.getText().toString());
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
				mAuthPresenter.selectName(null);
				dialogInterface.dismiss();
			}
			else
			{
				mAuthPresenter.selectName(arrayAdapter.getItem(i));
				dialogInterface.dismiss();
			}
		});
		builder.show();
	}
}