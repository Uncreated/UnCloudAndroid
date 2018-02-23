package com.uncreated.uncloud.client.requests;

import com.uncreated.uncloud.common.filestorage.FileTransfer;
import com.uncreated.uncloud.common.filestorage.FolderNode;
import com.uncreated.uncloud.server.auth.Session;
import com.uncreated.uncloud.server.auth.User;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestHandler
{
	private static final String SERVER_URL = "http://10.0.2.2:8080/";
	private static final RestTemplate restTemplate = new RestTemplate();

	private static UnCloudApi unCloudApi;
	private Retrofit retrofit;

	private String accessToken;

	public RequestHandler()
	{
		retrofit = new Retrofit.Builder()
				.baseUrl(SERVER_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		unCloudApi = retrofit.create(UnCloudApi.class);
	}

	public RequestStatus register(User user)
	{
		try
		{
			Response response = unCloudApi.postRegister(user).execute();
			if (response.isSuccessful())
			{
				return new RequestStatus(true);
			}
			else
			{
				return new RequestStatus(false, response.code() + "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new RequestStatus(false, e.getMessage());
		}
	}

	public RequestStatus<String> auth(User user)
	{
		try
		{
			Response<Session> response = unCloudApi.postAuth(user).execute();
			accessToken = response.body().getAccessToken();
			if (response.isSuccessful())
			{
				return new RequestStatus<String>(true).setData(accessToken);
			}
			else
			{
				return new RequestStatus<>(false, response.code() + "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new RequestStatus<>(false, e.getMessage());
		}
	}

	public RequestStatus<String> auth(String accessToken)
	{
		this.accessToken = null;
		try
		{
			Response<Session> response = unCloudApi.putAuth(accessToken).execute();
			if (response.isSuccessful())
			{
				this.accessToken = response.body().getAccessToken();
				return new RequestStatus<String>(true).setData(this.accessToken);
			}
			else
			{
				return new RequestStatus<>(false, response.code() + "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new RequestStatus<>(false, e.getMessage());
		}
	}

	public RequestStatus<FolderNode> files()
	{
		try
		{
			Response<FolderNode> response = unCloudApi.getFiles(accessToken).execute();
			if (response.isSuccessful())
			{
				return new RequestStatus<FolderNode>(true).setData(response.body());
			}
			else
			{
				return new RequestStatus<>(false, response.code() + "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new RequestStatus<>(false, e.getMessage());
		}
	}

	public RequestStatus<FileTransfer> downloadFilePart(String path, Integer part)
	{
		try
		{
			Response<FileTransfer> response = unCloudApi.getFile(accessToken, path, part).execute();
			if (response.isSuccessful())
			{
				return new RequestStatus<FileTransfer>(true).setData(response.body());
			}
			else
			{
				return new RequestStatus<>(false, response.code() + "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new RequestStatus<>(false, e.getMessage());
		}
	}

	public RequestStatus removeFile(String path)
	{
		try
		{
			Response response = unCloudApi.deleteFile(accessToken, path).execute();
			if (response.isSuccessful())
			{
				return new RequestStatus(true);
			}
			else
			{
				return new RequestStatus(false, response.code() + "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new RequestStatus(false, e.getMessage());
		}
	}

	public RequestStatus setFile(FileTransfer fileTransfer)
	{
		try
		{
			Response response = unCloudApi.postFile(accessToken, fileTransfer).execute();
			if (response.isSuccessful())
			{
				return new RequestStatus(true);
			}
			else
			{
				return new RequestStatus<>(false, response.code() + "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new RequestStatus(false, e.getMessage());
		}
	}

	public RequestStatus createFolder(String path)
	{
		try
		{
			Response response = unCloudApi.postFolder(accessToken, path).execute();
			if (response.isSuccessful())
			{
				return new RequestStatus(true);
			}
			else
			{
				return new RequestStatus<>(false, response.code() + "");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new RequestStatus(false, e.getMessage());
		}
	}
}