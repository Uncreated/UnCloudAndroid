package com.uncreated.uncloud.client.main.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.App;
import com.uncreated.uncloud.client.auth.view.AuthActivity;
import com.uncreated.uncloud.client.main.FileInfo;
import com.uncreated.uncloud.client.main.FilesController;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class FilesFragment
		extends Fragment
		implements FilesView
{
	private static final int FILE_SELECT_CODE = 0;

	private FilesController controller;

	private AlertDialog alertDialog;

	private RecyclerView recyclerView;
	private FloatingActionsMenu floatingActionsMenu;

	public FilesFragment()
	{

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		controller = ((App) getActivity().getApplication()).getFilesController();
		String login = getActivity().getIntent().getStringExtra(AuthActivity.KEY_LOGIN);
		Log.d("fileFramgent: login = ", login);
		controller.setLogin(login);


		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(R.layout.loading_dialog);
		builder.setOnKeyListener(null);
		alertDialog = builder.create();
		alertDialog.setCancelable(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_files, container, false);

		recyclerView = rootView.findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		floatingActionsMenu = rootView.findViewById(R.id.fab_menu);
		FloatingActionButton createFolderButton = floatingActionsMenu.findViewById(R.id.create_folder_fab);
		createFolderButton.setOnClickListener(view ->
		{
			onCreateFolderClick();
		});

		FloatingActionButton addFileButton = floatingActionsMenu.findViewById(R.id.add_file_fab);
		addFileButton.setOnClickListener(view ->
		{
			onAddFileClick();
		});

		return rootView;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		controller.onAttach(this);
	}

	@Override
	public void onStop()
	{
		super.onStop();

		controller.onAttach(this);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case FILE_SELECT_CODE:
				if (resultCode == RESULT_OK)
				{
					Uri uri = data.getData();
					/*try
					{
						InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
						OutputStream outputStream =
					}
					catch (FileNotFoundException e)
					{


					}*/

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

	protected void showLoading()
	{
		alertDialog.show();
	}

	protected void hideLoading()
	{
		alertDialog.hide();
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

		return false;
	}

	public void call(Runnable runnable)
	{
		getActivity().runOnUiThread(runnable);
	}

	public void onLongClickFile(FileInfo fileInfo)
	{
		DialogControls dialogControls = new DialogControls(getActivity(), fileInfo);
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

	public void onCreateFolderClick()
	{
		floatingActionsMenu.collapse();

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
		dialogBuilder.setTitle("Create Folder");
		dialogBuilder.setMessage("Enter folder name:");

		EditText editText = new EditText(getActivity());
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

	public void onAddFileClick()
	{
		floatingActionsMenu.collapse();
		showFileChooser();
	}

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

	protected void news(String msg)
	{
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
}
