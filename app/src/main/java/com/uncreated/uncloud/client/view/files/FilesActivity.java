package com.uncreated.uncloud.client.view.files;

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
import com.uncreated.uncloud.client.App;
import com.uncreated.uncloud.client.view.ClientActivity;
import com.uncreated.uncloud.common.filestorage.FNode;
import com.uncreated.uncloud.common.filestorage.FolderNode;

import java.io.File;

public class FilesActivity
		extends ClientActivity
{
	//FolderNode rootFolder;
	FolderNode curFolder;

	RecyclerView recyclerView;
	FloatingActionsMenu floatingActionsMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_files);

		recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		floatingActionsMenu = findViewById(R.id.fabMenu);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		((App) getApplication()).getClientController().updateFiles();
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
				((App) getApplication()).getClientController().createFolder(folderName, curFolder);
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
						((App) getApplication()).getClientController().copyFile(file, curFolder);
					}
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showFolder(FolderNode folderNode)
	{
		if (folderNode != null)
		{
			curFolder = folderNode;
			FilesAdapter filesAdapter = new FilesAdapter(this, recyclerView, folderNode);
			recyclerView.setAdapter(filesAdapter);
		}
	}

	public void onClickFNode(FNode fNode)
	{
		if (fNode instanceof FolderNode)
		{
			showFolder((FolderNode) fNode);
		}
	}

	public void openDialog(FNode fNode)
	{
		DialogControls dialogControls = new DialogControls(this, fNode);
		dialogControls.setOnClickDownload(view ->
		{
			((App) getApplication()).getClientController().download(fNode);
			dialogControls.hide();
		});
		dialogControls.setOnClickUpload(view ->
		{
			((App) getApplication()).getClientController().upload(fNode);
			dialogControls.hide();
		});
		dialogControls.setOnClickDeleteClient(view ->
		{
			((App) getApplication()).getClientController().removeFileFromClient(fNode);
			dialogControls.hide();
		});
		dialogControls.setOnClickDeleteServer(view ->
		{
			((App) getApplication()).getClientController().removeFileFromServer(fNode);
			dialogControls.hide();
		});
		dialogControls.show();
	}

	@Override
	public void onUpdateFiles(FolderNode mergedFiles)
	{
		String savedPath = "";
		if (curFolder != null)
		{
			savedPath += curFolder.getFilePath();
		}
		savedPath += "/";

		showFolder(mergedFiles.goTo(savedPath));
	}

	public void goBack()
	{
		showFolder(curFolder.getParentFolder());
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

	@Override
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
