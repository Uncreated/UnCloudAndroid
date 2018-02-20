package com.uncreated.uncloud.client.files.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.ActivityView;
import com.uncreated.uncloud.client.files.FileInfo;
import com.uncreated.uncloud.client.files.FilesController;

import java.io.File;
import java.util.ArrayList;

public class FilesActivity
		extends ActivityView<FilesController>
		implements FilesView
{
	private RecyclerView recyclerView;
	private FloatingActionsMenu floatingActionsMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_files);

		setController(app.getFilesController());

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

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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

	public void onLogout()
	{
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_logout:
				onLogout();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
