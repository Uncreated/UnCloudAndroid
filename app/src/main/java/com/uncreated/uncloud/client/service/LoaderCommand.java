package com.uncreated.uncloud.client.service;


import io.realm.RealmObject;

/*
Задачи пользователя (напр. скачать папку с файлами)
разбиваются на команды (напр. скачать файл)
чтобы не потерять прогресс выполнения
 */
public class LoaderCommand extends RealmObject {
    static final int FILE_DOWNLOAD = 1;
    static final int FOLDER_DOWNLOAD = 2;
    static final int FILE_UPLOAD = 3;
    static final int FOLDER_UPLOAD = 4;

    private int type;
    private String login;
    private String path;//file or folder path

    public LoaderCommand() {
    }

    public LoaderCommand(int type, String login, String path) {
        this.type = type;
        this.login = login;
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
