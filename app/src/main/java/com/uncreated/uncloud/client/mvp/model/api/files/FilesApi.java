package com.uncreated.uncloud.client.mvp.model.api.files;

import com.uncreated.uncloud.client.mvp.model.storage.FileTransfer;
import com.uncreated.uncloud.client.mvp.model.storage.FolderNode;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FilesApi
{
	@GET("api/files")
	Call<FolderNode> getFiles(@Header("Authorization") String accessToken);

	@GET("api/file")
	Call<FileTransfer> getFile(@Header("Authorization") String accessToken,
							   @Query("path") String path,
							   @Query("part") Integer part);

	@POST("api/file")
	Call<FileTransfer> postFile(@Header("Authorization") String accessToken,
								@Body FileTransfer fileTransfer);

	@DELETE("api/file")
	Call<Void> deleteFile(@Header("Authorization") String accessToken,
						  @Query("path") String path);


	@POST("api/folder")
	Call<Void> postFolder(@Header("Authorization") String accessToken,
						  @Query("path") String path);
}
