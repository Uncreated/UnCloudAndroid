package com.uncreated.uncloud.client.model.api;

import com.uncreated.uncloud.client.model.Model;
import com.uncreated.uncloud.client.model.api.entity.Session;
import com.uncreated.uncloud.client.model.api.entity.User;
import com.uncreated.uncloud.client.model.auth.AuthInfo;
import com.uncreated.uncloud.client.model.storage.FileTransfer;
import com.uncreated.uncloud.client.model.storage.FolderNode;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String SERVER_URL = "http://192.168.7.101:8080/";
   // private static final String SERVER_URL = "http://10.0.2.2:8080/";

    private UnCloudApi unCloudApi;
    private String accessToken;

    public ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(Model.getGson()))
                .build();
        this.unCloudApi = retrofit.create(UnCloudApi.class);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    //Auth

    public void registerAsync(User user, CallbackApi<Void> callback) {
        unCloudApi.postRegister(user).enqueue(callback);
    }

    public void authAsync(AuthInfo authInfo, CallbackApi<Session> callback) {
        String accessToken = authInfo.getAccessToken();
        if (accessToken != null) {
            unCloudApi.putAuth(accessToken).enqueue(new CallbackApi<Session>()
                    .setOnCompleteEvent(callback.getOnCompleteEvent())
                    .setOnFailedEvent(message -> authPassAsync(authInfo, callback)));
        } else {
            authPassAsync(authInfo, callback);
        }
    }

    private void authPassAsync(AuthInfo authInfo, CallbackApi<Session> callback) {
        unCloudApi.postAuth(new User(authInfo)).enqueue(callback);
    }

    //Files

    public void updateFilesAsync(CallbackApi<FolderNode> callback) {
        unCloudApi.getFiles(accessToken).enqueue(callback);
    }

    public FileTransfer getFile(String path, int i) throws IOException {
        return unCloudApi.getFile(accessToken, path, i).execute().body();
    }

    public boolean postFile(FileTransfer fileTransfer) throws IOException {
        return unCloudApi.postFile(accessToken, fileTransfer).execute().isSuccessful();
    }

    public void deleteFileAsync(String filePath, CallbackApi<Void> callback) {
        unCloudApi.deleteFile(accessToken, filePath).enqueue(callback);
    }

    public void postFolderAsync(String folderPath, CallbackApi<Void> callback) {
        unCloudApi.postFolder(accessToken, folderPath).enqueue(callback);
    }

    public boolean postFolder(String folderPath) throws IOException {
        return unCloudApi.postFolder(accessToken, folderPath).execute().isSuccessful();
    }
}
