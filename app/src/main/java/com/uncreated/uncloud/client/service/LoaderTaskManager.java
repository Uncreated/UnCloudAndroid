package com.uncreated.uncloud.client.service;

import android.os.AsyncTask;

import com.uncreated.uncloud.client.model.auth.AuthInfo;
import com.uncreated.uncloud.client.model.auth.AuthManager;
import com.uncreated.uncloud.client.model.fileloader.FileLoader;
import com.uncreated.uncloud.client.model.storage.FileNode;
import com.uncreated.uncloud.client.model.storage.FolderNode;

import java.io.IOException;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class LoaderTaskManager {
    private LoaderService loaderService;
    private FileLoader fileLoader;

    private HashMap<String, LoaderSubscriber> subscribers = new HashMap<>();
    private Realm realm;
    private Worker worker;
    private AuthManager authManager;

    LoaderTaskManager(LoaderService loaderService) {
        this.loaderService = loaderService;

        realm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("UnCloud.LoaderTaskManager")
                .schemaVersion(1)
                .build());

        authManager = new AuthManager(loaderService);
        fileLoader = new FileLoader(loaderService);

        startWorker();
    }

    private void startWorker() {
        LoaderCommand loaderCommand = realm.where(LoaderCommand.class).findFirst();
        if (loaderCommand != null && (worker == null || worker.getStatus() != AsyncTask.Status.RUNNING)) {
            AuthInfo authInfo = authManager.get(loaderCommand.getLogin());
            fileLoader.setUser(authInfo.getLogin(), authInfo.getAccessToken());
            worker = new Worker(authInfo);
            worker.execute();
        }
    }

    public boolean taskDownloadFile(FileNode fileNode, LoaderSubscriber loaderSubscriber) {
        return task(fileNode, loaderSubscriber, LoaderCommand.FILE_DOWNLOAD);
    }

    public boolean taskUploadFile(FileNode fileNode, LoaderSubscriber loaderSubscriber) {
        return task(fileNode, loaderSubscriber, LoaderCommand.FILE_UPLOAD);
    }

    public boolean taskDownloadFolder(FolderNode folderNode, LoaderSubscriber loaderSubscriber) {
        return task(folderNode, loaderSubscriber, LoaderCommand.FOLDER_DOWNLOAD);
    }

    public boolean taskUploadFolder(FolderNode folderNode, LoaderSubscriber loaderSubscriber) {
        return task(folderNode, loaderSubscriber, LoaderCommand.FOLDER_UPLOAD);
    }

    private boolean task(FileNode fNode, LoaderSubscriber loaderSubscriber, int type) {
        if (!isTaskEmpty(loaderSubscriber.getLogin()))
            return false;

        subscribers.put(loaderSubscriber.getLogin(), loaderSubscriber);

        realm.beginTransaction();
        if (fNode instanceof FolderNode)
            addFolder(type, loaderSubscriber.getLogin(), (FolderNode) fNode);
        else
            addFile(type, loaderSubscriber.getLogin(), fNode);
        realm.commitTransaction();

        startWorker();

        return true;
    }

    private void addFile(int type, String login, FileNode fileNode) {
        if (type == LoaderCommand.FILE_DOWNLOAD && fileNode.isOnServer() ||
                type == LoaderCommand.FILE_UPLOAD && fileNode.isOnClient()) {
            realm.insert(new LoaderCommand(type, login, fileNode.getFilePath()));
        }
    }

    private void addFolder(int type, String login, FolderNode folderNode) {
        if (type == LoaderCommand.FOLDER_DOWNLOAD && folderNode.isOnServer() ||
                type == LoaderCommand.FOLDER_UPLOAD && folderNode.isOnClient()) {
            realm.insert(new LoaderCommand(type, login, folderNode.getFilePath()));
        }
        for (FileNode fileNode : folderNode.getFiles()) {
            addFile(type, login, fileNode);
        }
        for (FolderNode folderNode1 : folderNode.getFolders()) {
            addFolder(type, login, folderNode1);
        }
    }

    public boolean subscribeMe(LoaderSubscriber loaderSubscriber) {
        if (isTaskEmpty(loaderSubscriber.getLogin()))
            return false;

        subscribers.put(loaderSubscriber.getLogin(), loaderSubscriber);
        return true;
    }

    private boolean isTaskEmpty(String login) {
        return getNextCommand(login) == null;
    }

    private LoaderCommand getNextCommand(String login) {
        return realm.where(LoaderCommand.class)
                .equalTo("login", login)
                .findFirst();
    }

    class Worker extends AsyncTask<String, String, String> {
        private String login;

        public Worker(AuthInfo authInfo) {
            this.login = authInfo.getLogin();
            fileLoader.setUser(login, authInfo.getAccessToken());
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            loaderService.onNotification(values[0], values[1]);
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            LoaderSubscriber loaderSubscriber = subscribers.remove(login);
            if (loaderSubscriber != null) {
                loaderSubscriber.sendResult(s);
            }
            startWorker();
        }

        private void doCommand(LoaderCommand loaderCommand) throws IOException {
            boolean ok = false;
            switch (loaderCommand.getType()) {
                case LoaderCommand.FILE_DOWNLOAD:
                    ok = fileLoader.downloadFile(loaderCommand.getPath());
                    break;
                case LoaderCommand.FILE_UPLOAD:
                    ok = fileLoader.uploadFile(loaderCommand.getPath());
                    break;
                case LoaderCommand.FOLDER_DOWNLOAD:
                    ok = fileLoader.getApiClient().postFolder(loaderCommand.getPath());
                    break;
                case LoaderCommand.FOLDER_UPLOAD:
                    ok = fileLoader.getStorage().createPath(loaderCommand.getPath());
                    break;
            }
            if (!ok) {
                throw new IOException();
            }

            publishProgress(loaderCommand.getLogin(), loaderCommand.getPath());
        }
    }
}
