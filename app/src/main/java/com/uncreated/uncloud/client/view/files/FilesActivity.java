package com.uncreated.uncloud.client.view.files;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.App;
import com.uncreated.uncloud.client.view.ClientActivity;
import com.uncreated.uncloud.common.filestorage.FNode;
import com.uncreated.uncloud.common.filestorage.FolderNode;

public class FilesActivity
		extends ClientActivity
{
	FolderNode rootFolder;
	FolderNode curFolder;

	RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_files);

		recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		((App) getApplication()).getClientController().updateFiles();
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

	public void onOpenDialog(FNode fNode)
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

	@Override
	public void onBackPressed()
	{

	}

	@Override
	public void onLogout()
	{
		super.onBackPressed();
	}
}
