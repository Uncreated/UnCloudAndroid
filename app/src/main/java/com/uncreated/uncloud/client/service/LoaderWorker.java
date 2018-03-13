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

class LoaderWorker extends AsyncTask<String, LoaderWorker.Progress, String> {
    private final String login;
    private final FileLoader fileLoader;
    private final OnProgressListener onProgressListener;
    private final OnPostListener onPostListener;

    class Progress {
        final String login;
        final String path;
        final int cur;
        final int max;

        private Progress(String login, String path, int cur, int max) {
            this.login = login;
            this.path = path;
            this.cur = cur;
            this.max = max;
        }

        public String getLogin() {
            return login;
        }

        public String getPath() {
            return path;
        }

        public int getCur() {
            return cur;
        }

        public int getMax() {
            return max;
        }
    }

    LoaderWorker(FileLoader fileLoader, AuthInfo authInfo, OnProgressListener onProgressListener, OnPostListener onPostListener) {
        this.login = authInfo.getLogin();
        this.fileLoader = fileLoader;
        this.onProgressListener = onProgressListener;
        this.onPostListener = onPostListener;

        fileLoader.setUser(login, authInfo.getAccessToken());
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);

        onProgressListener.onProgress(values[0]);
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
        int szi = loaderCommands.size();
        int i = 0;
        while (loaderCommands.size() > 0) {
            try {
                doCommand(loaderCommands.first(), i, szi);
            } catch (Exception e) {
                e.printStackTrace();
            }
            realm.executeTransaction(realm1 -> loaderCommands.deleteFirstFromRealm());
            i++;
        }
        return login;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        onPostListener.onPost(s);
    }

    private void doCommand(LoaderCommand loaderCommand, int cur, int max) throws IOException {
        String path = loaderCommand.getPathSrc();
        publishProgress(new Progress(login, path, cur, max));
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
    }

    interface OnPostListener {
        void onPost(String s);
    }

    interface OnProgressListener {
        void onProgress(Progress progress);
    }
}
