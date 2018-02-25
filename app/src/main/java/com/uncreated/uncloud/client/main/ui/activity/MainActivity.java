package com.uncreated.uncloud.client.main.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.main.presentation.MainPresenter;
import com.uncreated.uncloud.client.main.ui.fragment.about.AboutFragment;
import com.uncreated.uncloud.client.main.ui.fragment.files.FilesFragment;
import com.uncreated.uncloud.client.main.ui.fragment.settings.SettingsFragment;
import com.uncreated.uncloud.client.model.api.entity.Session;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
		extends MvpAppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener,
		MainView
{
	@BindView(R.id.nav_view)
	NavigationView navigationView;

	@BindView(R.id.drawer_layout)
	DrawerLayout drawer;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private FilesFragment filesFragment;
	private SettingsFragment settingsFragment;
	private AboutFragment aboutFragment;

	private boolean files = true;

	@InjectPresenter(type = PresenterType.GLOBAL, tag = "MainPresenter")
	MainPresenter mainPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		navigationView.setNavigationItemSelectedListener(this);

		TextView textView = navigationView.getHeaderView(0).findViewById(R.id.login_text_view);
		textView.setText(Session.current.getLogin());

		filesFragment = new FilesFragment();
		settingsFragment = new SettingsFragment();
		aboutFragment = new AboutFragment();
	}

	@Override
	public void switchFiles()
	{
		files = true;
		switchFragment(filesFragment);
	}

	@Override
	public void switchSettings()
	{
		files = false;
		switchFragment(settingsFragment);
	}

	@Override
	public void switchAbout()
	{
		files = false;
		switchFragment(aboutFragment);
	}

	@Override
	public void logout()
	{
		filesFragment.getMvpDelegate().onDestroy();
		settingsFragment.getMvpDelegate().onDestroy();
		aboutFragment.getMvpDelegate().onDestroy();
		getMvpDelegate().onDestroy();
		finish();
	}

	private void switchFragment(MvpFragment fragment)
	{
		getFragmentManager().beginTransaction()
				.replace(R.id.main_fragment_container, fragment)
				.commitAllowingStateLoss();
	}

	private boolean firstClickOnBack = false;

	@Override
	public void onBackPressed()
	{
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else
		{
			if (firstClickOnBack)
			{
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			else
			{
				Toast.makeText(this, "Click again to exit the app", Toast.LENGTH_LONG).show();
				firstClickOnBack = true;
				new Handler().postDelayed(() -> firstClickOnBack = false, 2000);
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (files && filesFragment.dispatchTouchEvent(event))
		{
			return true;
		}

		return super.dispatchTouchEvent(event);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.nav_home:
				mainPresenter.switchFiles();
				break;
			case R.id.nav_settings:
				mainPresenter.switchSettings();
				break;
			case R.id.nav_about:
				mainPresenter.switchAbout();
				break;
			case R.id.nav_logout:
				mainPresenter.logout();
				break;
		}

		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}