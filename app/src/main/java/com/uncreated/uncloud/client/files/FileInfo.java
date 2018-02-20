package com.uncreated.uncloud.client.files;

import com.uncreated.uncloud.common.filestorage.FNode;
import com.uncreated.uncloud.common.filestorage.FileNode;
import com.uncreated.uncloud.common.filestorage.FolderNode;

public class FileInfo
{
	private String name;
	private String info;
	private boolean downloaded;
	private boolean uploaded;
	private boolean directory;
	private boolean downloadAny;
	private boolean uploadAny;
	private boolean deleteAnyClient;
	private boolean deleteAnyServer;

	private FileInfo(FNode fNode)
	{
		this.name = fNode.getName();
		this.info = makeInfo(fNode);
		this.downloaded = fNode.isOnClient();
		this.uploaded = fNode.isOnServer();
	}

	FileInfo(FileNode fileNode)
	{
		this((FNode)fileNode);

		this.directory = false;
		this.downloadAny = !this.downloaded;
		this.uploadAny = !this.uploaded;
		this.deleteAnyClient = this.downloaded;
		this.deleteAnyServer = this.uploaded;
	}

	FileInfo(FolderNode folderNode)
	{
		this((FNode)folderNode);

		this.directory = true;
		this.downloadAny = !folderNode.isFilesOnClient(true);
		this.uploadAny = !folderNode.isFilesOnServer(false);
		this.deleteAnyClient = folderNode.isFilesOnClient(false);
		this.deleteAnyServer = folderNode.isFilesOnServer(false);
	}

	private String makeInfo(FNode fNode)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Name: ");
		builder.append(fNode.getName());
		builder.append('\n');
		builder.append("Path: ");
		if (fNode.getParentFolder() == null)
		{
			builder.append('/');
		}
		else
		{
			builder.append(fNode.getParentFolder().getFilePath());
		}
		builder.append('\n');

		builder.append("Size: ");
		builder.append(fNode.getSizeString());

		builder.append('\n');
		builder.append("Location: ");
		if (fNode.isOnClient())
		{
			builder.append("client");
		}
		if (fNode.isOnClient() && fNode.isOnServer())
		{
			builder.append(", ");
		}
		if (fNode.isOnServer())
		{
			builder.append("server");
		}
		return builder.toString();
	}

	public String getName()
	{
		return name;
	}

	public String getInfo()
	{
		return info;
	}

	public boolean isDownloaded()
	{
		return downloaded;
	}

	public boolean isUploaded()
	{
		return uploaded;
	}

	public boolean isDirectory()
	{
		return directory;
	}

	public boolean isDownloadAny()
	{
		return downloadAny;
	}

	public boolean isUploadAny()
	{
		return uploadAny;
	}

	public boolean isDeleteAnyClient()
	{
		return deleteAnyClient;
	}

	public boolean isDeleteAnyServer()
	{
		return deleteAnyServer;
	}
}
