package com.uncreated.uncloud.client.service;

import android.os.AsyncTask;

import com.uncreated.uncloud.client.model.auth.AuthInfo;
import com.uncreated.uncloud.client.model.auth.AuthManager;
import com.uncreated.uncloud.client.model.fileloader.FileLoader;
import com.uncreated.uncloud.client.model.storage.FileNode;
import com.uncreated.uncloud.client.model.storage.FolderNode;
import com.uncreated.uncloud.client.widget.FilesAppWidget;

import java.io.File;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.uncreated.uncloud.client.service.LoaderCommand.ACTION_DELETE;
import static com.uncreated.uncloud.client.service.LoaderCommand.ACTION_DOWNLOAD;
import static com.uncreated.uncloud.client.service.LoaderCommand.ACTION_UPLOAD;
import static com.uncreated.uncloud.client.service.LoaderCommand.OBJ_FILE;
import static com.uncreated.uncloud.client.service.LoaderCommand.OBJ_FOLDER;

/*
Задачи пользователя разбиваются на команды (LoaderCommand) и сохраняются в бд
После выполнения каждой задачи запись из БД удаляется
При прерывании выполнения задачи, после перезапуска работа возобновляется
 */
public class LoaderTaskManager {

    private final LoaderService loaderService;
    private final FileLoader fileLoader;
    private final AuthManager authManager;
    private final HashMap<String, LoaderSubscriber> subscribers = new HashMap<>();
    private final Realm realm;

    private LoaderWorker worker;

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

    private boolean startWorker() {
        LoaderCommand loaderCommand = realm.where(LoaderCommand.class).findFirst();
        if (loaderCommand != null && (worker == null || worker.getStatus() != AsyncTask.Status.RUNNING)) {
            AuthInfo authInfo = authManager.get(loaderCommand.getLogin());
            worker = new LoaderWorker(fileLoader,
                    authInfo,
                    loaderService::onNotification,
                    this::workerFinished);
            worker.execute();
            return true;
        }
        return false;
    }

    private void workerFinished(String login) {
        LoaderSubscriber loaderSubscriber = subscribers.remove(login);
        if (loaderSubscriber != null) {
            loaderSubscriber.onResult(null);
        }
        FilesAppWidget.callUpdate(loaderService.getApplicationContext());
        if (!startWorker()) {
            loaderService.onNotification(null);
        }
    }

    public boolean taskDownloadFile(LoaderSubscriber loaderSubscriber, FileNode... fileNodes) {
        return task(loaderSubscriber, ACTION_DOWNLOAD, fileNodes);
    }

    public boolean taskUploadFile(LoaderSubscriber loaderSubscriber, FileNode... fileNodes) {
        return task(loaderSubscriber, ACTION_UPLOAD, fileNodes);
    }

    public boolean taskDownloadFolder(LoaderSubscriber loaderSubscriber, FolderNode... folderNodes) {
        return task(loaderSubscriber, ACTION_DOWNLOAD, folderNodes);
    }

    public boolean taskUploadFolder(LoaderSubscriber loaderSubscriber, FolderNode... folderNodes) {
        return task(loaderSubscriber, ACTION_UPLOAD, folderNodes);
    }

    public boolean taskRemoveClient(LoaderSubscriber loaderSubscriber, FileNode... fileNodes) {
        return task(loaderSubscriber, ACTION_DELETE, fileNodes);
    }

    //Ещё добавить путь куда копировать
    public boolean taskCopy(LoaderSubscriber loaderSubscriber, File... files) {
        //return task(loaderSubscriber, ACTION_COPY, files);
        return false;
    }

    private boolean task(LoaderSubscriber loaderSubscriber, int actionType, FileNode... fNodes) {
        if (!isTaskEmpty(loaderSubscriber.getLogin()))
            return false;

        subscribers.put(loaderSubscriber.getLogin(), loaderSubscriber);

        realm.executeTransaction(realm1 -> {
            for (FileNode fNode : fNodes) {
                if (actionType == ACTION_DELETE || !(fNode instanceof FolderNode)) {
                    addFile(actionType, loaderSubscriber.getLogin(), fNode);
                } else {
                    addFolder(actionType, loaderSubscriber.getLogin(), (FolderNode) fNode);
                }
            }
        });

        return startWorker();
    }

    private void addFile(int actionType, String login, FileNode fileNode) {
        realm.insert(new LoaderCommand(actionType, OBJ_FILE, login, fileNode.getFilePath()));
    }

    private void addFolder(int actionType, String login, FolderNode folderNode) {
        realm.insert(new LoaderCommand(actionType, OBJ_FOLDER, login, folderNode.getFilePath()));
        for (FileNode fileNode : folderNode.getFiles()) {
            addFile(actionType, login, fileNode);
        }
        for (FolderNode folderNode1 : folderNode.getFolders()) {
            addFolder(actionType, login, folderNode1);
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
}
