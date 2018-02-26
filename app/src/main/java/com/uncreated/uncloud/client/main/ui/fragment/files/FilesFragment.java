package com.uncreated.uncloud.client.main.ui.fragment.files;

import android.app.AlertDialog;
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

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.BaseFragment;
import com.uncreated.uncloud.client.main.presentation.FileInfo;
import com.uncreated.uncloud.client.main.presentation.FilesPresenter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class FilesFragment
		extends BaseFragment
		implements FilesView
{
	private static final int FILE_SELECT_CODE = 1;

	@BindView(R.id.recycler_view)
	RecyclerView recyclerView;

	@BindView(R.id.fab_menu)
	FloatingActionsMenu floatingActionsMenu;

	private DialogControls dialogControls;

	@InjectPresenter(type = PresenterType.GLOBAL, tag = "FilesPresenter")
	FilesPresenter filesPresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_files, container, false);

		ButterKnife.bind(this, rootView);

		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		FloatingActionButton createFolderButton = floatingActionsMenu.findViewById(R.id.create_folder_fab);
		createFolderButton.setOnClickListener(view -> onCreateFolderClick());

		FloatingActionButton addFileButton = floatingActionsMenu.findViewById(R.id.add_file_fab);
		addFileButton.setOnClickListener(view -> onAddFileClick());

		return rootView;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN && floatingActionsMenu.isExpanded())
		{
			Rect outRect = new Rect();
			floatingActionsMenu.getGlobalVisibleRect(outRect);

			if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
			{
				floatingActionsMenu.collapse();
				return true;
			}
		}
		return super.dispatchTouchEvent(event);
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

					if (uri != null)
					{
						File file = new File(uri.getPath());
						filesPresenter.copyFile(file);
					}
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void showFolder(ArrayList<FileInfo> files, boolean rootFolder)
	{
		FilesAdapter filesAdapter = new FilesAdapter(this, recyclerView, files, !rootFolder);
		recyclerView.setAdapter(filesAdapter);
	}

	public void onClickFile(FileInfo fileInfo)
	{
		if (fileInfo == null || fileInfo.isDirectory())
		{
			filesPresenter.openFolder(fileInfo);
		}
	}

	public void onLongClickFile(FileInfo fileInfo)
	{
		filesPresenter.actionFile(fileInfo);
	}

	@Override
	public void setActionDialog(boolean show, FileInfo fileInfo)
	{
		Log.d("FilesFragment", "setActionDialog(" + show + ")");
		if (show)
		{
			if (dialogControls == null)
			{
				dialogControls = new DialogControls(getActivity(), fileInfo,
						view -> filesPresenter.download(fileInfo),
						view -> filesPresenter.upload(fileInfo),
						view -> filesPresenter.deleteFileFromClient(fileInfo),
						view -> filesPresenter.deleteFileFromServer(fileInfo));
				dialogControls.setOnCancelListener(dialogInterface -> filesPresenter.cancelAction());
				dialogControls.show();
			}
		}
		else
		{
			if (dialogControls != null)
			{
				dialogControls.hide();
				dialogControls.dismiss();
				dialogControls = null;
			}
		}
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

		dialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

		dialogBuilder.setPositiveButton("Create", (dialogInterface, i) ->
		{
			String folderName = editText.getText().toString();
			if (folderName.length() > 0)
			{
				filesPresenter.createFolder(folderName);
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
			showError("Please install a File Manager.");
		}
	}
}
