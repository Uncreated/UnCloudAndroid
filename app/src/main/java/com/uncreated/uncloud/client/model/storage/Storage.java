package com.uncreated.uncloud.client.model.storage;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Storage {
    private String rootFolder;
    private String login;

    public Storage(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    private String makeFullPath(String... sequence) {
        StringBuilder stringBuilder = new StringBuilder(rootFolder);
        stringBuilder.append(login);
        for (String seq : sequence) {
            stringBuilder.append(seq);
        }
        return stringBuilder.toString();
    }

    public FolderNode getFiles() throws FileNotFoundException {
        File userFolder = new File(rootFolder + login);
        if (!userFolder.exists()) {
            userFolder.mkdir();
        }

        return new FolderNode(userFolder);
    }

    /*public void copyFile(FolderNode curFolder, CallbackLoader callback, File... files) {
        try {
            for (File source : files) {
                File dest = new File(makeFullPath(curFolder.getFilePath(), source.getName()));
                dest.getParentFile().mkdirs();
                if (source.isDirectory()) {
                    FileUtils.copyDirectory(source, dest);
                } else {
                    FileUtils.copyFile(source, dest);
                }
            }
            callback.onCompletePost();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public boolean createPath(String folderPath) {
        File folder = new File(makeFullPath(folderPath));
        return folder.exists() || folder.mkdirs();
    }

    public boolean remove(String filePath) throws IOException {
        File file = new File(rootFolder + login + filePath);

        if (file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        } else {
            file.delete();
        }
        return file.exists();
    }

    public void read(FileTransfer fileTransfer) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(getFile(fileTransfer), "r");
        randomAccessFile.seek((long) fileTransfer.getPart() * (long) FileTransfer.PART_SIZE);
        randomAccessFile.read(fileTransfer.getData());
        randomAccessFile.close();
    }

    public void write(FileTransfer fileTransfer) throws IOException {
        File file = getFile(fileTransfer);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.seek((long) fileTransfer.getPart() * (long) FileTransfer.PART_SIZE);
        randomAccessFile.write(fileTransfer.getData());
        randomAccessFile.close();
    }

    private File getFile(FileTransfer fileTransfer) {
        return new File(makeFullPath(fileTransfer.getPath()));
    }

    public long getFileSize(String path) {
        File file = new File(makeFullPath(path));
        return file.length();
    }
}
