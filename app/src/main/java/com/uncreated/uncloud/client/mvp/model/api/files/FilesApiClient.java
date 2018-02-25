package com.uncreated.uncloud.client.mvp.model.api.files;

import com.uncreated.uncloud.client.mvp.model.api.auth.Session;
import com.uncreated.uncloud.client.mvp.model.api.CallbackCodes;
import com.uncreated.uncloud.client.mvp.model.storage.FileTransfer;
import com.uncreated.uncloud.client.mvp.model.storage.FolderNode;
import com.uncreated.uncloud.client.ui.fragment.files.FilesView;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;

public class FilesApiClient
{
	private FilesApi filesApi;

	public FilesApiClient(Retrofit retrofit)
	{
		this.filesApi = retrofit.create(FilesApi.class);
	}

	public void updateFiles(CallbackCodes.OnCompleteEvent<FolderNode> onCompleteEvent, FilesView filesView)
	{
		filesApi.getFiles(Session.current.getAccessToken()).enqueue(new CallbackCodes<FolderNode>()
				.setOnCompleteEvent(onCompleteEvent)
				.setOnFailedEvent(filesView::onFailRequest));
	}

	public FileTransfer getFile(String path, int i) throws IOException
	{
		Response<FileTransfer> response = filesApi.getFile(Session.current.getAccessToken(), path, i).execute();
		if (response.isSuccessful() && response.body() != null)
		{
			return response.body();
		}
		return null;
	}

	public boolean postFile(FileTransfer fileTransfer) throws IOException
	{
		Response<FileTransfer> response = filesApi.postFile(Session.current.getAccessToken(), fileTransfer).execute();
		return response.isSuccessful();
	}

	public boolean postFolder(String filePath) throws IOException
	{
		Response<Void> response = filesApi.postFolder(Session.current.getAccessToken(), filePath).execute();
		return response.isSuccessful();
	}

	public void deleteFile(String filePath, CallbackCodes.OnCompleteEvent<Void> onCompleteEvent, FilesView filesView)
	{
		filesApi.deleteFile(Session.current.getAccessToken(), filePath).enqueue(new CallbackCodes<Void>()
				.setOnCompleteEvent(onCompleteEvent)
				.setOnFailedEvent(filesView::onFailRequest));
	}

	public void createFolder(String folderPath, CallbackCodes.OnCompleteEvent<Void> onCompleteEvent, FilesView filesView)
	{
		filesApi.postFolder(Session.current.getAccessToken(), folderPath).enqueue(new CallbackCodes<Void>()
				.setOnCompleteEvent(onCompleteEvent)
				.setOnFailedEvent(filesView::onFailRequest));
	}

}
