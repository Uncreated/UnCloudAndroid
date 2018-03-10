package com.uncreated.uncloud.client.model.storage;

import com.uncreated.uncloud.client.model.fileloader.CallbackLoader;

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

    public String makeFullPath(String... sequence) {
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

    public void copyFile(FolderNode curFolder, CallbackLoader callback, File... files) {
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
    }

    public boolean createPath(String folderPath) {
        return new File(makeFullPath(folderPath)).mkdirs();
    }

    public void removeFile(String filePath, CallbackLoader callback) {
        new Thread(() ->
        {
            try {
                File file = new File(rootFolder + login + filePath);

                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                    callback.onCompletePost();
                    return;
                } else if (file.delete()) {
                    callback.onCompletePost();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            callback.onFailedPost("Can not delete:\n" + filePath);
        }).start();
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

    public long getFileSize(String path){
        File file = new File(makeFullPath(path));
        if(file!=null){
            return file.length();
        }
        return 0;
    }
}
