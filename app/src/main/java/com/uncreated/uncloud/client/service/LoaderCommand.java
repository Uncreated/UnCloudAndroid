package com.uncreated.uncloud.client.service;


import io.realm.RealmObject;

public class LoaderCommand extends RealmObject {
    static final int OBJ_FILE = 1;
    static final int OBJ_FOLDER = 2;
    static final int ACTION_DOWNLOAD = 1;
    static final int ACTION_UPLOAD = 2;
    static final int ACTION_COPY = 3;
    static final int ACTION_DELETE = 4;

    private int actionType;
    private int objType;
    private String login;
    private String pathSrc;
    private String pathDst;

    public LoaderCommand() {
    }

    public LoaderCommand(int actionType, int objType, String login, String pathSrc) {
        this.actionType = actionType;
        this.objType = objType;
        this.login = login;
        this.pathSrc = pathSrc;
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

    public String getPathSrc() {
        return pathSrc;
    }

    public void setPathSrc(String pathSrc) {
        this.pathSrc = pathSrc;
    }

    public String getPathDst() {
        return pathDst;
    }

    public void setPathDst(String pathDst) {
        this.pathDst = pathDst;
    }
}
