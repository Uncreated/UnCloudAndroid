package com.uncreated.uncloud.client.model.fileloader;

import android.content.Context;

import com.uncreated.uncloud.client.model.Model;
import com.uncreated.uncloud.client.model.api.ApiClient;
import com.uncreated.uncloud.client.model.storage.FileTransfer;
import com.uncreated.uncloud.client.model.storage.Storage;

import java.io.IOException;

public class FileLoader {
    private Storage storage;
    private ApiClient apiClient;

    public FileLoader(Context context) {
        storage = new Storage(context.getFilesDir().getAbsolutePath());
        apiClient = new ApiClient();
    }

    public void setUser(String login, String accessToken) {
        storage.setLogin(login);
        apiClient.setAccessToken(accessToken);
    }

    public Storage getStorage() {
        return storage;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public boolean downloadFile(String path) throws IOException {
        FileTransfer fileTransfer;
        int i = 0;
        do {
            fileTransfer = Model.getApiClient().getFile(path, i);
            if (fileTransfer == null) {
                return false;
            }
            storage.write(fileTransfer);
            i++;
        }
        while (i < fileTransfer.getParts());
        return true;
    }

    public boolean uploadFile(String path) throws IOException {
        FileTransfer fileTransfer;
        long size = storage.getFileSize(path);
        int i = 0;
        do {
            fileTransfer = new FileTransfer(path, i, FileTransfer.getSizeOfPart(size, i));
            storage.read(fileTransfer);
            if (!Model.getApiClient().postFile(fileTransfer)) {
                return false;
            }
            i++;
        }
        while (i < fileTransfer.getParts());
        return true;
    }
}
