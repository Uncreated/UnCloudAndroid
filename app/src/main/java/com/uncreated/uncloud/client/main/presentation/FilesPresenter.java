package com.uncreated.uncloud.client.main.presentation;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.uncreated.uncloud.client.main.ui.fragment.files.FilesView;
import com.uncreated.uncloud.client.model.Model;
import com.uncreated.uncloud.client.model.api.ApiClient;
import com.uncreated.uncloud.client.model.api.CallbackApi;
import com.uncreated.uncloud.client.model.api.entity.Session;
import com.uncreated.uncloud.client.model.storage.CallbackStorage;
import com.uncreated.uncloud.client.model.storage.FileNode;
import com.uncreated.uncloud.client.model.storage.FolderNode;
import com.uncreated.uncloud.client.model.storage.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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
		storage = Model.getStorage();
		apiClient = Model.getApiClient();

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

	public void cancelAction()
	{
		getViewState().setActionDialog(false, null);
	}

	private void dialogAction()
	{
		getViewState().setActionDialog(false, null);
		getViewState().setLoading(true);
	}

	public void download(FileInfo fileInfo)
	{
		dialogAction();

		CallbackStorage callbackStorage = new CallbackStorage(this::updateFiles, this::showError);

		if (fileInfo.isDirectory())
		{
			storage.downloadFolder(getFileNode(fileInfo), callbackStorage);
		}
		else
		{
			storage.downloadFile(getFileNode(fileInfo), callbackStorage);
		}
	}

	public void upload(FileInfo fileInfo)
	{
		dialogAction();

		CallbackStorage callbackStorage = new CallbackStorage(this::updateFiles, this::showError);

		if (fileInfo.isDirectory())
		{
			storage.uploadFolder(getFileNode(fileInfo), callbackStorage);
		}
		else
		{
			storage.uploadFile(getFileNode(fileInfo), callbackStorage);
		}
	}

	public void deleteFileFromClient(FileInfo fileInfo)
	{
		dialogAction();

		storage.removeFile(Session.current.getLogin(),
				getFileNode(fileInfo).getFilePath(),
				new CallbackStorage(this::updateFiles, this::showError));
	}

	public void deleteFileFromServer(FileInfo fileInfo)
	{
		dialogAction();
		FileNode fileNode = getFileNode(fileInfo);
		if (fileNode != null)
		{
			apiClient.deleteFile(fileNode.getFilePath(), new CallbackApi<Void>()
					.setOnCompleteEvent(body -> updateFiles())
					.setOnFailedEvent(this::showError));
		}
	}

	private void showError(String message)
	{
		getViewState().showError(message);
		getViewState().setLoading(false);
	}

	private void updateFiles()
	{
		apiClient.updateFiles(new CallbackApi<FolderNode>()
				.setOnCompleteEvent(body ->
				{
					try
					{
						FolderNode clientFolder = storage.getFiles(Session.current.getLogin());
						mergedFolder = new FolderNode(clientFolder, body);
						curFolder = mergedFolder.goTo(curFolder != null ? curFolder.getFilePath() : "/");
						sendFileInfo(curFolder);
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
						showError(e.getMessage());
					}
				})
				.setOnFailedEvent(this::showError));
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

		getViewState().showFolder(files, folderNode.getParentFolder() == null);
		getViewState().setLoading(false);
	}

	public void copyFile(File... files)
	{
		getViewState().setLoading(true);
		storage.copyFile(curFolder, new CallbackStorage(this::updateFiles, this::showError), files);
	}

	public void createFolder(String name)
	{
		apiClient.postFolder(curFolder.getFilePath() + name, new CallbackApi<Void>()
				.setOnCompleteEvent(body -> updateFiles())
				.setOnFailedEvent(this::showError));
	}
}
