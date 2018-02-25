package com.uncreated.uncloud.client.model.api;

import com.uncreated.uncloud.client.main.fragment.files.FilesView;
import com.uncreated.uncloud.client.model.api.entity.Session;
import com.uncreated.uncloud.client.model.api.entity.User;
import com.uncreated.uncloud.client.model.auth.AuthInf;
import com.uncreated.uncloud.client.model.storage.FileTransfer;
import com.uncreated.uncloud.client.model.storage.FolderNode;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiClient
{
	private UnCloudApi unCloudApi;

	public ApiClient(Retrofit retrofit)
	{
		this.unCloudApi = retrofit.create(UnCloudApi.class);
	}

	//Auth

	public void register(User user, CallbackCodes<Void> callback)
	{
		unCloudApi.postRegister(user).enqueue(callback);
	}

	public void auth(AuthInf authInf, CallbackCodes<Session> callback)
	{
		String accessToken = authInf.getAccessToken();
		if (accessToken != null)
		{
			unCloudApi.putAuth(accessToken).enqueue(new CallbackCodes<Session>()
					.setOnCompleteEvent(callback.getOnCompleteEvent())
					.setOnFailedEvent(message -> unCloudApi.postAuth(authInf.getUser()).enqueue(callback)));
		}
		else
		{
			unCloudApi.postAuth(authInf.getUser()).enqueue(callback);
		}
	}

	//Files

	public void updateFiles(CallbackCodes.OnCompleteEvent<FolderNode> onCompleteEvent, FilesView filesView)
	{
		unCloudApi.getFiles(Session.current.getAccessToken()).enqueue(new CallbackCodes<FolderNode>()
				.setOnCompleteEvent(onCompleteEvent)
				.setOnFailedEvent(filesView::onFailRequest));
	}

	public FileTransfer getFile(String path, int i) throws IOException
	{
		Response<FileTransfer> response = unCloudApi.getFile(Session.current.getAccessToken(), path, i).execute();
		if (response.isSuccessful() && response.body() != null)
		{
			return response.body();
		}
		return null;
	}

	public boolean postFile(FileTransfer fileTransfer) throws IOException
	{
		Response<FileTransfer> response = unCloudApi.postFile(Session.current.getAccessToken(), fileTransfer).execute();
		return response.isSuccessful();
	}

	public boolean postFolder(String filePath) throws IOException
	{
		Response<Void> response = unCloudApi.postFolder(Session.current.getAccessToken(), filePath).execute();
		return response.isSuccessful();
	}

	public void deleteFile(String filePath, CallbackCodes.OnCompleteEvent<Void> onCompleteEvent, FilesView filesView)
	{
		unCloudApi.deleteFile(Session.current.getAccessToken(), filePath).enqueue(new CallbackCodes<Void>()
				.setOnCompleteEvent(onCompleteEvent)
				.setOnFailedEvent(filesView::onFailRequest));
	}

	public void createFolder(String folderPath, CallbackCodes.OnCompleteEvent<Void> onCompleteEvent, FilesView filesView)
	{
		unCloudApi.postFolder(Session.current.getAccessToken(), folderPath).enqueue(new CallbackCodes<Void>()
				.setOnCompleteEvent(onCompleteEvent)
				.setOnFailedEvent(filesView::onFailRequest));
	}
}
