package com.uncreated.uncloud.client.model.storage;


import com.uncreated.uncloud.client.model.Model;
import com.uncreated.uncloud.client.model.api.entity.Session;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Storage {
    private String rootFolder;

    public Storage(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    private String makeFullPath(String... sequence) {
        StringBuilder stringBuilder = new StringBuilder(rootFolder);
        stringBuilder.append(Session.current.getLogin());
        for (String seq : sequence) {
            stringBuilder.append(seq);
        }
        return stringBuilder.toString();
    }

    public FolderNode getFiles(String login) throws FileNotFoundException {
        File userFolder = new File(rootFolder + login);
        if (!userFolder.exists()) {
            userFolder.mkdir();
        }

        return new FolderNode(userFolder);
    }

    public void copyFile(FolderNode curFolder, CallbackStorage callback, File... files) {
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

    public void write(FileTransfer fileTransfer) throws IOException {
        fileTransfer.write(makeFullPath());
    }

    public void createPath(String folderPath) {
        new File(makeFullPath(folderPath)).mkdirs();
    }

    public void read(FileTransfer fileTransfer, String path) throws IOException {
        fileTransfer.read(new File(makeFullPath(path)));
    }

    public void removeFile(String login, String filePath, CallbackStorage callback) {
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

    public void createFolder(String login, String path) throws IOException {
        File file = new File(rootFolder + login + path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException("Can not create folder " + path);
            }
        }
    }

    public void downloadFile(FileNode fileNode, CallbackStorage callback) {
        new Thread(() ->
        {
            if (downloadFile(fileNode)) {
                callback.onCompletePost();
            } else {
                callback.onFailedPost("Download failed:\n" + fileNode.getFilePath());
            }
        }).start();
    }

    private boolean downloadFile(FileNode fileNode) {
        String path = fileNode.getFilePath();
        int parts = fileNode.getParts();
        for (int i = 0; i < parts || i == 0; i++) {
            try {
                FileTransfer fileTransfer = Model.getApiClient().getFile(path, i);
                if (fileTransfer == null) {
                    return false;
                }
                write(fileTransfer);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void downloadFolder(FolderNode folderNode, CallbackStorage callback) {
        new Thread(() ->
        {
            if (downloadFolder(folderNode)) {
                callback.onCompletePost();
            } else {
                callback.onFailedPost("Download failed:\n" + folderNode.getFilePath());
            }
        }).start();
    }

    private boolean downloadFolder(FolderNode folderNode) {
        if (!folderNode.isOnClient()) {
            createPath(folderNode.getFilePath());
        }
        for (FolderNode folder : folderNode.getFolders()) {
            if (!downloadFolder(folder)) {
                return false;
            }
        }

        for (FileNode fileNode : folderNode.getFiles()) {
            if (!fileNode.isOnClient()) {
                if (!downloadFile(fileNode)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void uploadFile(FileNode fileNode, CallbackStorage callback) {
        new Thread(() ->
        {
            if (uploadFile(fileNode)) {
                callback.onCompletePost();
            } else {
                callback.onFailedPost("Upload failed:\n" + fileNode.getFilePath());
            }
        }).start();
    }

    private boolean uploadFile(FileNode fileNode) {
        String path = fileNode.getFilePath();
        int szi = fileNode.getParts();
        for (int i = 0; i < szi || i == 0; i++) {
            try {
                FileTransfer fileTransfer = new FileTransfer(path, i, FileTransfer.getSizeOfPart(fileNode.getSize(), i));
                read(fileTransfer, path);
                if (!Model.getApiClient().postFile(fileTransfer)) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void uploadFolder(FolderNode folderNode, CallbackStorage callback) {
        new Thread(() ->
        {
            if (uploadFolder(folderNode)) {
                callback.onCompletePost();
            } else {
                callback.onFailedPost("Upload failed:\n" + folderNode.getFilePath());
            }
        }).start();
    }

    private boolean uploadFolder(FolderNode folderNode) {
        if (!folderNode.isOnServer()) {
            try {
                if (!Model.getApiClient().postFolder(folderNode.getFilePath())) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (FolderNode folder : folderNode.getFolders()) {
            if (!uploadFolder(folder)) {
                return false;
            }
        }

        for (FileNode fileNode : folderNode.getFiles()) {
            if (!fileNode.isOnServer()) {
                if (!uploadFile(fileNode)) {
                    return false;
                }
            }
        }

        return true;
    }
}
