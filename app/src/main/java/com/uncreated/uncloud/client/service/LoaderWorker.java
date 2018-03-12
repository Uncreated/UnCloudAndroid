package com.uncreated.uncloud.client.service;

import android.os.AsyncTask;

import com.uncreated.uncloud.client.model.auth.AuthInfo;
import com.uncreated.uncloud.client.model.fileloader.FileLoader;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.uncreated.uncloud.client.service.LoaderCommand.ACTION_DELETE;
import static com.uncreated.uncloud.client.service.LoaderCommand.ACTION_DOWNLOAD;
import static com.uncreated.uncloud.client.service.LoaderCommand.ACTION_UPLOAD;
import static com.uncreated.uncloud.client.service.LoaderCommand.OBJ_FILE;

class LoaderWorker extends AsyncTask<String, String, String> {
    private String login;
    private FileLoader fileLoader;
    private OnProgressListener onProgressListener;
    private OnPostListener onPostListener;

    LoaderWorker(FileLoader fileLoader, AuthInfo authInfo) {
        this.login = authInfo.getLogin();
        this.fileLoader = fileLoader;
        fileLoader.setUser(login, authInfo.getAccessToken());
    }

    void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (onProgressListener != null) {
            onProgressListener.onProgress(values[0], values[1]);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        Realm realm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("UnCloud.LoaderTaskManager")
                .schemaVersion(1)
                .build());
        RealmResults<LoaderCommand> loaderCommands = realm.where(LoaderCommand.class)
                .equalTo("login", login)
                .findAll();
        while (loaderCommands.size() > 0) {
            try {
                doCommand(loaderCommands.first());
            } catch (Exception e) {
                e.printStackTrace();
            }
            realm.executeTransaction(realm1 -> loaderCommands.deleteFirstFromRealm());
        }
        return login;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (onPostListener != null) {
            onPostListener.onPost(s);
        }
    }

    private void doCommand(LoaderCommand loaderCommand) throws IOException {
        String path = loaderCommand.getPathSrc();
        switch (loaderCommand.getActionType()) {
            case ACTION_DOWNLOAD:
                if (loaderCommand.getObjType() == OBJ_FILE) {
                    if (!fileLoader.downloadFile(path)) {
                        throw new IOException("Can't download file:\n" + path);
                    }
                } else {
                    if (!fileLoader.getStorage().createPath(path)) {
                        throw new IOException("Can't download folder:\n" + path);
                    }
                }
                break;
            case ACTION_UPLOAD:
                if (loaderCommand.getObjType() == OBJ_FILE) {
                    if (!fileLoader.uploadFile(path)) {
                        throw new IOException("Can't upload file:\n" + path);
                    }
                } else {
                    if (!fileLoader.getApiClient().postFolder(path)) {
                        throw new IOException("Can't upload folder:\n" + path);
                    }
                }
                break;
            case ACTION_DELETE:
                if (!fileLoader.getStorage().remove(path)) {
                    throw new IOException("Can't delete:\n" + path);
                }
                break;
        }

        publishProgress(loaderCommand.getLogin(), loaderCommand.getPathSrc());
    }

    interface OnPostListener {
        void onPost(String s);
    }

    interface OnProgressListener {
        void onProgress(String login, String info);
    }
}
