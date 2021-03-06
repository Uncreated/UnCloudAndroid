package com.uncreated.uncloud.client.main.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.MvpFragment;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.BaseFragment;
import com.uncreated.uncloud.client.main.ui.fragment.about.AboutFragment;
import com.uncreated.uncloud.client.main.ui.fragment.files.FilesFragment;
import com.uncreated.uncloud.client.main.ui.fragment.settings.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String contentFragmentTag = "content_fragment_tag";
    public static final String KEY_LOGIN = "keyLogin";

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FilesFragment filesFragment;
    private SettingsFragment settingsFragment;
    private AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        textView.setText(getIntent().getStringExtra(KEY_LOGIN));

        if (getFragmentManager().findFragmentByTag(contentFragmentTag) == null) {
            switchFiles();
        }
    }

    private void switchFiles() {
        switchFragment(filesFragment = new FilesFragment());
    }

    private void switchSettings() {
        switchFragment(settingsFragment = new SettingsFragment());
    }

    private void switchAbout() {
        switchFragment(aboutFragment = new AboutFragment());
    }

    private void logout() {
        if (filesFragment != null) {
            filesFragment.getMvpDelegate().onDestroy();
        }
        if (settingsFragment != null) {
            settingsFragment.getMvpDelegate().onDestroy();
        }
        if (aboutFragment != null) {
            aboutFragment.getMvpDelegate().onDestroy();
        }
        finish();
    }

    private void switchFragment(MvpFragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, fragment, contentFragmentTag)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            View view = findViewById(android.R.id.content);
            if (view != null) {
                Snackbar.make(view, "Close the application?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", this::closeApp)
                        .show();
            }
        }
    }

    private void closeApp(View view) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        BaseFragment baseFragment = (BaseFragment) getFragmentManager().findFragmentByTag(contentFragmentTag);
        return baseFragment != null && baseFragment.dispatchTouchEvent(event) || super.dispatchTouchEvent(event);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                switchFiles();
                break;
            case R.id.nav_settings:
                switchSettings();
                break;
            case R.id.nav_about:
                switchAbout();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}