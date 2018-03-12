package com.uncreated.uncloud.client.service;


import io.realm.RealmObject;

/*
Задачи пользователя (напр. скачать папку с файлами)
разбиваются на команды (напр. скачать файл)
чтобы не потерять прогресс выполнения
 */
public class LoaderCommand extends RealmObject {
    static final int OBJ_FILE = 1;
    static final int OBJ_FOLDER = 2;
    static final int ACTION_DOWNLOAD = 1;
    static final int ACTION_UPLOAD = 2;

    private int actionType;
    private int objType;
    private String login;
    private String path;

    public LoaderCommand() {
    }

    public LoaderCommand(int actionType, int objType, String login, String path) {
        this.actionType = actionType;
        this.objType = objType;
        this.login = login;
        this.path = path;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getObjType() {
        return objType;
    }

    public void setObjType(int objType) {
        this.objType = objType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
