package com.uncreated.uncloud.client.main.presentation;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.uncreated.uncloud.client.main.ui.fragment.files.FilesView;
import com.uncreated.uncloud.client.model.Model;
import com.uncreated.uncloud.client.model.api.ApiClient;
import com.uncreated.uncloud.client.model.api.entity.Session;
import com.uncreated.uncloud.client.model.storage.FileNode;
import com.uncreated.uncloud.client.model.storage.FileTransfer;
import com.uncreated.uncloud.client.model.storage.FolderNode;
import com.uncreated.uncloud.client.model.storage.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@InjectViewState
public class FilesPresenter
		extends MvpPresenter<FilesView>
{
	//Model
	private Storage storage;
	private ApiClient apiClient;

	private FolderNode mergedFolder;
	private FolderNode curFolder;

	public FilesPresenter()
	{
		storage = Model.getInstance().getStorage();
		apiClient = Model.getInstance().getApiClient();

		getViewState().setLoading(true);
		updateFiles();
	}

	public void openFolder(FileInfo fileInfo)
	{
		if (fileInfo == null)
		{
			if (curFolder.getParentFolder() != null)
			{
				curFolder = curFolder.getParentFolder();
				sendFileInfo(curFolder);
			}
		}
		else if (fileInfo.isDirectory())
		{
			FolderNode folderNode = getFileNode(fileInfo);
			if (folderNode != null)
			{
				curFolder = folderNode;
				sendFileInfo(curFolder);
			}
		}
	}

	public void actionFile(FileInfo fileInfo)
	{
		getViewState().setActionDialog(true, fileInfo);
	}

	public void download(FileInfo fileInfo)
	{
		getViewState().setActionDialog(false, null);
		getViewState().setLoading(true);
		runThread(() ->
		{
			if (fileInfo.isDirectory())
			{
				downloadFolder(getFileNode(fileInfo));
			}
			else
			{
				downloadFile(getFileNode(fileInfo));
			}

			updateFiles();
		});
	}

	public void upload(FileInfo fileInfo)
	{
		getViewState().setActionDialog(false, null);
		getViewState().setLoading(true);
		runThread(() ->
		{
			if (fileInfo.isDirectory())
			{
				uploadFolder(getFileNode(fileInfo));
			}
			else
			{
				uploadFile(getFileNode(fileInfo));
			}

			updateFiles();
		});
	}

	public void deleteFileFromClient(FileInfo fileInfo)
	{
		getViewState().setActionDialog(false, null);
		getViewState().setLoading(true);
		runThread(() ->
		{
			try
			{
				storage.removeFile(Session.current.getLogin(), getFileNode(fileInfo).getFilePath());
			}
			catch (IOException | NullPointerException e)
			{
				e.printStackTrace();
			}
			updateFiles();
		});
	}

	public void deleteFileFromServer(FileInfo fileInfo)
	{
		getViewState().setActionDialog(false, null);
		getViewState().setLoading(true);
		FileNode fileNode = getFileNode(fileInfo);
		if (fileNode != null)
		{
			apiClient.deleteFile(fileNode.getFilePath(), body -> updateFiles(), getViewState());
		}
	}

	private void updateFiles()
	{
		apiClient.updateFiles(body ->
		{
			try
			{
				FolderNode clientFolder = storage.getFiles(Session.current.getLogin());
				mergedFolder = new FolderNode(clientFolder, body);
				mergedFolder.sort();
				curFolder = mergedFolder.goTo(curFolder != null ? curFolder.getFilePath() : "/");
				sendFileInfo(curFolder);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				getViewState().onFailRequest(e.getMessage());
			}
		}, getViewState());
	}

	private <T extends FileNode> T getFileNode(FileInfo fileInfo)
	{
		if (fileInfo.isDirectory())
		{
			for (FolderNode folderNode : curFolder.getFolders())
			{
				if (folderNode.getName().equals(fileInfo.getName()))
				{
					return (T) folderNode;
				}
			}
		}
		else
		{
			for (FileNode fileNode : curFolder.getFiles())
			{
				if (fileNode.getName().equals(fileInfo.getName()))
				{
					return (T) fileNode;
				}
			}
		}
		return null;
	}

	private void sendFileInfo(FolderNode folderNode)
	{
		ArrayList<FileInfo> files = new ArrayList<>();
		for (FolderNode folder : folderNode.getFolders())
		{
			files.add(new FileInfo(folder));
		}
		for (FileNode file : folderNode.getFiles())
		{
			files.add(new FileInfo(file));
		}

		getViewState().setLoading(false);
		getViewState().showFolder(files, folderNode.getParentFolder() == null);
	}

	public void copyFile(List<File> fileList)
	{
		if (fileList != null && fileList.size() > 0)
		{
			copyFile(fileList.toArray(new File[fileList.size()]));
		}
	}

	public void copyFile(File... files)
	{
		runThread(() ->
		{
			try
			{
				storage.copyFile(curFolder, files);
				updateFiles();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				getViewState().onFailRequest(e.getMessage());
			}
		});
	}

	private boolean downloadFile(FileNode fileNode)
	{
		String path = fileNode.getFilePath();
		int parts = fileNode.getParts();
		for (int i = 0; i < parts || i == 0; i++)
		{
			try
			{
				FileTransfer fileTransfer = apiClient.getFile(path, i);
				if (fileTransfer == null)
				{
					return false;
				}
				storage.write(fileTransfer);

			}
			catch (IOException e)
			{
				e.printStackTrace();
				//delete file
				return false;
			}
		}
		return true;
	}

	private boolean downloadFolder(FolderNode folderNode)
	{
		if (!folderNode.isOnClient())
		{
			storage.createPath(folderNode.getFilePath());
		}
		for (FolderNode folder : folderNode.getFolders())
		{
			if (!downloadFolder(folder))
			{
				return false;
			}
		}

		for (FileNode fileNode : folderNode.getFiles())
		{
			if (!fileNode.isOnClient())
			{
				if (!downloadFile(fileNode))
				{
					return false;
				}
			}
		}

		return true;
	}

	private boolean uploadFile(FileNode fileNode)
	{
		String path = fileNode.getFilePath();
		int szi = fileNode.getParts();
		for (int i = 0; i < szi || i == 0; i++)
		{
			try
			{
				FileTransfer fileTransfer = new FileTransfer(path, i, FileTransfer.getSizeOfPart(fileNode.getSize(), i));
				storage.read(fileTransfer, path);
				if (!apiClient.postFile(fileTransfer))
				{
					return false;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private boolean uploadFolder(FolderNode folderNode)
	{
		if (!folderNode.isOnServer())
		{
			try
			{
				if (!apiClient.postFolder(folderNode.getFilePath()))
				{
					return false;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		for (FolderNode folder : folderNode.getFolders())
		{
			if (!uploadFolder(folder))
			{
				return false;
			}
		}

		for (FileNode fileNode : folderNode.getFiles())
		{
			if (!fileNode.isOnServer())
			{
				if (!uploadFile(fileNode))
				{
					return false;
				}
			}
		}

		return true;
	}

	public void createFolder(String name)
	{
		apiClient.createFolder(curFolder.getFilePath() + name, body -> updateFiles(), getViewState());
	}

	private void runThread(Runnable r)
	{
		new Thread(r).start();
	}
}
