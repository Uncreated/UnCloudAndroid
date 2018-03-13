package com.uncreated.uncloud.client.model.storage;

import android.support.annotation.NonNull;

import java.io.File;
import java.text.DecimalFormat;

public class FileNode implements Comparable<FileNode> {
    protected String name;

    FolderNode parentFolder;

    boolean onClient;
    boolean onServer;

    private Long size;

    FileNode(File file) {
        this(file.getName(), file.length());
    }

    FileNode(String name, Long size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public FolderNode getParentFolder() {
        return parentFolder;
    }

    public boolean isOnServer() {
        return onServer;
    }

    public boolean isOnClient() {
        return onClient;
    }

    public void setLoc(boolean onClient, boolean onServer) {
        this.onClient = onClient;
        this.onServer = onServer;
    }

    public Long getSize() {
        return size;
    }

    public String getFilePath() {
        StringBuilder builder = new StringBuilder(name);
        builder.insert(0, "/");
        FolderNode parent = parentFolder;
        while (parent.getParentFolder() != null) {
            builder.insert(0, parent.name);
            builder.insert(0, "/");
            parent = parent.getParentFolder();
        }
        return builder.toString();
    }

    @Override
    public int compareTo(@NonNull FileNode o) {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }

    public String getSizeString() {
        long size = getSize();
        if (size <= 0) {
            return "0B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
