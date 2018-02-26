package com.uncreated.uncloud.client.model.api;

import com.uncreated.uncloud.client.model.api.entity.Session;
import com.uncreated.uncloud.client.model.api.entity.User;
import com.uncreated.uncloud.client.model.auth.AuthInf;
import com.uncreated.uncloud.client.model.storage.FileTransfer;
import com.uncreated.uncloud.client.model.storage.FolderNode;

import java.io.IOException;

import retrofit2.Retrofit;

public class ApiClient
{
	private UnCloudApi unCloudApi;

	public ApiClient(Retrofit retrofit)
	{
		this.unCloudApi = retrofit.create(UnCloudApi.class);
	}

	//Auth

	public void register(User user, CallbackApi<Void> callback)
	{
		unCloudApi.postRegister(user).enqueue(callback);
	}

	public void auth(AuthInf authInf, CallbackApi<Session> callback)
	{
		String accessToken = authInf.getAccessToken();
		if (accessToken != null)
		{
			unCloudApi.putAuth(accessToken).enqueue(new CallbackApi<Session>()
					.setOnCompleteEvent(callback.getOnCompleteEvent())
					.setOnFailedEvent(message -> auth(authInf.getUser(), callback)));
		}
		else
		{
			auth(authInf.getUser(), callback);
		}
	}

	private void auth(User user, CallbackApi<Session> callback)
	{
		unCloudApi.postAuth(user).enqueue(callback);
	}

	//Files

	public void updateFiles(CallbackApi<FolderNode> callback)
	{
		unCloudApi.getFiles(Session.current.getAccessToken()).enqueue(callback);
	}

	public FileTransfer getFile(String path, int i) throws IOException
	{
		return unCloudApi.getFile(Session.current.getAccessToken(), path, i).execute().body();
	}

	public boolean postFile(FileTransfer fileTransfer) throws IOException
	{
		return unCloudApi.postFile(Session.current.getAccessToken(), fileTransfer).execute().isSuccessful();
	}

	public void deleteFile(String filePath, CallbackApi<Void> callback)
	{
		unCloudApi.deleteFile(Session.current.getAccessToken(), filePath).enqueue(callback);
	}

	public void postFolder(String folderPath, CallbackApi<Void> callback)
	{
		unCloudApi.postFolder(Session.current.getAccessToken(), folderPath).enqueue(callback);
	}

	public boolean postFolder(String folderPath) throws IOException
	{
		return unCloudApi.postFolder(Session.current.getAccessToken(), folderPath).execute().isSuccessful();
	}
}
