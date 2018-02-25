package com.uncreated.uncloud.client.old.main;

import android.content.Context;

import com.uncreated.uncloud.client.mvp.model.api.files.FilesApiClient;
import com.uncreated.uncloud.client.mvp.model.storage.FileNode;
import com.uncreated.uncloud.client.mvp.model.storage.FileTransfer;
import com.uncreated.uncloud.client.mvp.model.storage.FolderNode;
import com.uncreated.uncloud.client.mvp.model.storage.Storage;
import com.uncreated.uncloud.client.old.Controller;
import com.uncreated.uncloud.client.ui.fragment.files.FilesView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class FilesController
		extends Controller<FilesView>
{
	private Storage storage;
	private FolderNode mergedFolder;
	private String login;

	private FolderNode curFolder;

	private boolean firstRequest = true;

	private FilesApiClient filesApiClient;

	//реализуй mvp по принципу moxy
	//View -
	//.... - ViewState - Presenter - Model
	//View -
	public FilesController(Retrofit retrofit, String rootFolder)
	{
		storage = new Storage(rootFolder);

		filesApiClient = new FilesApiClient(retrofit);
	}

	@Override
	public synchronized void onAttach(FilesView filesView, Context context)
	{
		super.onAttach(filesView, context);

		if (firstRequest)
		{
			updateFiles();
			firstRequest = false;
		}
	}

	public void updateFiles()
	{
		filesApiClient.updateFiles(body ->
		{
			try
			{
				FolderNode clientFolder = storage.getFiles(login);
				mergedFolder = new FolderNode(clientFolder, body);
				mergedFolder.sort();
				curFolder = mergedFolder.goTo(curFolder != null ? curFolder.getFilePath() : "/");
				sendFileInfo(curFolder);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				view.onFailRequest(e.getMessage());
			}
		}, view);
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

		view.showFolder(files, folderNode.getParentFolder() == null);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////OLD

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
				view.onFailRequest(e.getMessage());
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
				FileTransfer fileTransfer = filesApiClient.getFile(path, i);
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

	public void download(FileInfo fileInfo)
	{
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
				if (!filesApiClient.postFile(fileTransfer))
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
				if (!filesApiClient.postFolder(folderNode.getFilePath()))
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

	public void upload(FileInfo fileInfo)
	{
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
		runThread(() ->
		{
			try
			{
				storage.removeFile(login, getFileNode(fileInfo).getFilePath());
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
		FileNode fileNode = getFileNode(fileInfo);
		if (fileNode != null)
		{
			filesApiClient.deleteFile(fileNode.getFilePath(), body -> updateFiles(), view);
		}
	}

	public void createFolder(String name)
	{
		filesApiClient.createFolder(curFolder.getFilePath() + name, body -> updateFiles(), view);
	}
}
