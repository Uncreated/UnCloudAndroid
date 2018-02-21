package com.uncreated.uncloud.client.files.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.uncreated.uncloud.AboutActivity;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.ActivityView;
import com.uncreated.uncloud.client.files.FileInfo;
import com.uncreated.uncloud.client.files.FilesController;

import java.io.File;
import java.util.ArrayList;

public class FilesActivity
		extends ActivityView<FilesController>
		implements FilesView, NavigationView.OnNavigationItemSelectedListener
{
	private RecyclerView recyclerView;
	private FloatingActionsMenu floatingActionsMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_files);

		setController(app.getFilesController());

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		floatingActionsMenu = findViewById(R.id.fabMenu);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		//showLoading();
	}

	@Override
	public void showFolder(ArrayList<FileInfo> files, boolean rootFolder)
	{
		hideLoading();
		FilesAdapter filesAdapter = new FilesAdapter(this, recyclerView, files, !rootFolder);
		recyclerView.setAdapter(filesAdapter);
	}

	@Override
	public void onFailRequest(String message)
	{
		hideLoading();
	}

	public void onClickFile(FileInfo fileInfo)
	{
		if (fileInfo == null || fileInfo.isDirectory())
		{
			controller.openFolder(fileInfo);
		}
	}

	public void onLongClickFile(FileInfo fileInfo)
	{
		DialogControls dialogControls = new DialogControls(this, fileInfo);
		dialogControls.setOnClickDownload(view ->
		{
			controller.download(fileInfo);
			dialogControls.hide();
		});
		dialogControls.setOnClickUpload(view ->
		{
			controller.upload(fileInfo);
			dialogControls.hide();
		});
		dialogControls.setOnClickDeleteClient(view ->
		{
			controller.removeFileFromClient(fileInfo);
			dialogControls.hide();
		});
		dialogControls.setOnClickDeleteServer(view ->
		{
			controller.removeFileFromServer(fileInfo);
			dialogControls.hide();
		});
		dialogControls.show();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (floatingActionsMenu.isExpanded())
			{
				Rect outRect = new Rect();
				floatingActionsMenu.getGlobalVisibleRect(outRect);

				if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
				{
					floatingActionsMenu.collapse();
					return true;
				}
			}
		}

		return super.dispatchTouchEvent(event);
	}

	public void onCreateFolderClick(View v)
	{
		floatingActionsMenu.collapse();

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
		dialogBuilder.setTitle("Create Folder");
		dialogBuilder.setMessage("Enter folder name:");

		EditText editText = new EditText(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		editText.setLayoutParams(layoutParams);
		dialogBuilder.setView(editText);

		dialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) ->
		{
			dialogInterface.cancel();
		});

		dialogBuilder.setPositiveButton("Create", (dialogInterface, i) ->
		{
			String folderName = editText.getText().toString();
			if (folderName.length() > 0)
			{
				controller.createFolder(folderName);
				dialogInterface.cancel();
			}
		});

		dialogBuilder.show();
	}

	public void onAddFileClick(View v)
	{
		floatingActionsMenu.collapse();
		//news("onAddFileClick");
		showFileChooser();
	}

	private static final int FILE_SELECT_CODE = 0;

	private void showFileChooser()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try
		{
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
		}
		catch (android.content.ActivityNotFoundException ex)
		{
			news("Please install a File Manager.");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case FILE_SELECT_CODE:
				if (resultCode == RESULT_OK)
				{
					Uri uri = data.getData();
					if (uri != null)
					{
						File file = new File(uri.getPath());
						controller.copyFile(file);
					}
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				news("Click again to exit the app");
				firstClickOnBack = true;
				new Handler().postDelayed(() ->
				{
					firstClickOnBack = false;
				}, 2000);
			}
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.nav_settings)
		{
			//startActivity(new Intent(this, SettingsActivity.class));
		}
		else if (id == R.id.nav_logout)
		{
			finish();
		}
		else if (id == R.id.nav_about)
		{
			startActivity(new Intent(this, AboutActivity.class));
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
