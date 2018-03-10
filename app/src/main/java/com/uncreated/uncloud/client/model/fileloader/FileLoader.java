package com.uncreated.uncloud.client.model.fileloader;

import android.content.Context;

import com.uncreated.uncloud.client.model.Model;
import com.uncreated.uncloud.client.model.api.ApiClient;
import com.uncreated.uncloud.client.model.storage.FileNode;
import com.uncreated.uncloud.client.model.storage.FileTransfer;
import com.uncreated.uncloud.client.model.storage.FolderNode;
import com.uncreated.uncloud.client.model.storage.Storage;

import java.io.IOException;

public class FileLoader {
    private Storage storage;
    private ApiClient apiClient;

    // private CallbackLoader callbackLoader;
    //private boolean loading = false;

    public FileLoader(Context context) {
        this.storage = new Storage(context.getFilesDir().getAbsolutePath());
        this.apiClient = new ApiClient();
    }

    public void setUser(String login, String accessToken) {
        storage.setLogin(login);
        this.apiClient.setAccessToken(accessToken);
    }

    public Storage getStorage() {
        return storage;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    /*public boolean attach(CallbackLoader callback) {
        if (loading) {
            this.callbackLoader = callback;
        }
        Log.e("FileLoader", "attach " + loading);
        return loading;
    }

    private void newTask(CallbackLoader callback, String msg) {
        this.callbackLoader = callback;
        this.loading = true;
    }

    public void downloadFile(FileNode fileNode, CallbackLoader callback) {
        newTask(callback, "Downloading file " + fileNode.getFilePath());
        new DownloadFileTask().execute(fileNode);
    }

    public void downloadFolder(FolderNode folderNode, CallbackLoader callback) {
        newTask(callback, "Downloading folder " + folderNode.getFilePath());
        new DownloadFolderTask().execute(folderNode);
    }

    public void uploadFile(FileNode fileNode, CallbackLoader callback) {
        newTask(callback, "Uploading file " + fileNode.getFilePath());
        new UploadFileTask().execute(fileNode);
    }

    public void uploadFolder(FolderNode folderNode, CallbackLoader callback) {
        newTask(callback, "Uploading folder " + folderNode.getFilePath());
        new UploadFolderTask().execute(folderNode);
    }*/

    public boolean downloadFile(String path) {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean downloadFolder(FolderNode folderNode) {
        if (!folderNode.isOnClient()) {
            storage.createPath(folderNode.getFilePath());
        }
        for (FolderNode folder : folderNode.getFolders()) {
            if (!downloadFolder(folder)) {
                return false;
            }
        }

        for (FileNode fileNode : folderNode.getFiles()) {
            if (!fileNode.isOnClient()) {
                if (!downloadFile(fileNode.getFilePath())) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean uploadFile(String path) {
        try {
            FileTransfer fileTransfer;
            long size = storage.getFileSize(path);
            int i = 0;
            do {
                fileTransfer = new FileTransfer(path, i, FileTransfer.getSizeOfPart(size, i));
                if (!Model.getApiClient().postFile(fileTransfer)) {
                    return false;
                }
                i++;
            }
            while (i < fileTransfer.getParts());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean uploadFolder(FolderNode folderNode) {
        if (!folderNode.isOnServer()) {
            try {
                if (!apiClient.postFolder(folderNode.getFilePath())) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (FolderNode folder : folderNode.getFolders()) {
            if (!uploadFolder(folder)) {
                return false;
            }
        }

        for (FileNode fileNode : folderNode.getFiles()) {
            if (!fileNode.isOnServer()) {
                if (!uploadFile(fileNode.getFilePath())) {
                    return false;
                }
            }
        }
        return true;
    }

    /*abstract class LoaderTask<T> extends AsyncTask<T, Integer, Boolean> {
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            loading = false;
            if (callbackLoader != null) {
                if (aBoolean) {
                    callbackLoader.onCompletePost();
                } else {
                    callbackLoader.onFailedPost("");
                }
            }
        }

        @Override
        protected Boolean doInBackground(T... params) {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("AsyncTask", "Tick");
                if (2 + 3 == 6) break;
            }
            return true;
        }
    }

    class DownloadFolderTask extends LoaderTask<FolderNode> {
        @Override
        protected Boolean doInBackground(FolderNode... folderNodes) {
            super.doInBackground(folderNodes);
            return downloadFolder(folderNodes[0]);
        }
    }

    class UploadFolderTask extends LoaderTask<FolderNode> {
        @Override
        protected Boolean doInBackground(FolderNode... folderNodes) {
            super.doInBackground(folderNodes);
            return uploadFolder(folderNodes[0]);
        }
    }

    class DownloadFileTask extends LoaderTask<FileNode> {
        @Override
        protected Boolean doInBackground(FileNode... fileNodes) {
            super.doInBackground(fileNodes);
            return downloadFile(fileNodes[0]);
        }
    }

    class UploadFileTask extends LoaderTask<FileNode> {
        @Override
        protected Boolean doInBackground(FileNode... fileNodes) {
            super.doInBackground(fileNodes);
            return uploadFile(fileNodes[0]);
        }
    }*/
}
