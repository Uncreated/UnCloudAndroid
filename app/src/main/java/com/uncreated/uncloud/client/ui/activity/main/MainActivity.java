package com.uncreated.uncloud.client.ui.activity.main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.ui.fragment.about.AboutFragment;
import com.uncreated.uncloud.client.ui.fragment.files.FilesFragment;
import com.uncreated.uncloud.client.ui.fragment.settings.SettingsFragment;
import com.uncreated.uncloud.client.mvp.model.api.auth.Session;

public class MainActivity
		extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{
	private static final String KEY_SELECTED_NAV_ID = "keySelectedNavId";

	private FilesFragment filesFragment;
	private SettingsFragment settingsFragment;
	private AboutFragment aboutFragment;

	private int selectedNavId = R.id.nav_home;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		TextView textView = navigationView.getHeaderView(0).findViewById(R.id.loginTextView);
		textView.setText(Session.current.getLogin());

		filesFragment = new FilesFragment();
		settingsFragment = new SettingsFragment();
		aboutFragment = new AboutFragment();

		if (savedInstanceState != null)
		{
			selectedNavId = savedInstanceState.getInt(KEY_SELECTED_NAV_ID);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setCheckedItem(selectedNavId);

		navigationSelect();
		//switchFragment(filesFragment);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putInt(KEY_SELECTED_NAV_ID, selectedNavId);
	}

	private boolean firstClickOnBack = false;

	protected void news(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

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
				news("Click again to exit the app");
				firstClickOnBack = true;
				new Handler().postDelayed(() ->
				{
					firstClickOnBack = false;
				}, 2000);
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		news("onDestroy");
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (selectedNavId == R.id.nav_home && filesFragment.dispatchTouchEvent(event))
		{
			return true;
		}

		return super.dispatchTouchEvent(event);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		selectedNavId = item.getItemId();

		navigationSelect();

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void navigationSelect()
	{
		if (selectedNavId == R.id.nav_home)
		{
			switchFragment(filesFragment);
		}
		else if (selectedNavId == R.id.nav_settings)
		{
			switchFragment(settingsFragment);
		}
		else if (selectedNavId == R.id.nav_logout)
		{
			finish();
		}
		else if (selectedNavId == R.id.nav_about)
		{
			switchFragment(aboutFragment);
		}
	}

	private void switchFragment(Fragment fragment)
	{
		getFragmentManager().beginTransaction()
				.replace(R.id.main_fragment_container, fragment)
				.commit();
	}
}
