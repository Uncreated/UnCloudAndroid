package com.uncreated.uncloud.client.mvp.model.storage;


import com.uncreated.uncloud.client.mvp.model.api.auth.Session;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Storage
{
	private String rootFolder;

	public Storage(String rootFolder)
	{
		this.rootFolder = rootFolder;
	}

	private String makeFullPath(String... sequence)
	{
		StringBuilder stringBuilder = new StringBuilder(rootFolder);
		stringBuilder.append(Session.current.getLogin());
		for (String seq : sequence)
		{
			stringBuilder.append(seq);
		}
		return stringBuilder.toString();
	}

	public FolderNode getFiles(String login) throws FileNotFoundException
	{
		File userFolder = new File(rootFolder + login);
		if (!userFolder.exists())
		{
			userFolder.mkdir();
		}

		return new FolderNode(userFolder);
	}

	public void copyFile(FolderNode curFolder, File... files) throws IOException
	{
		for (File source : files)
		{
			File dest = new File(makeFullPath(curFolder.getFilePath(), source.getName()));
			dest.getParentFile().mkdirs();
			if (source.isDirectory())
			{
				FileUtils.copyDirectory(source, dest);
			}
			else
			{
				FileUtils.copyFile(source, dest);
			}
		}
	}

	public void write(FileTransfer fileTransfer) throws IOException
	{
		fileTransfer.write(makeFullPath());
	}

	public void createPath(String folderPath)
	{
		new File(makeFullPath(folderPath)).mkdirs();
	}

	public void read(FileTransfer fileTransfer, String path) throws IOException
	{
		fileTransfer.read(new File(makeFullPath(path)));
	}

	public FileTransfer getFilePart(String login, String filePath, int part) throws IOException
	{
		File file = new File(rootFolder + login + filePath);
		if (!file.exists())
		{
			throw new FileNotFoundException(filePath);
		}

		int size = FileTransfer.getSizeOfPart(file.length(), part);

		FileTransfer fileTransfer = new FileTransfer(filePath, part, size);
		fileTransfer.read(rootFolder + login);

		return fileTransfer;
	}

	public boolean removeFile(String login, String filePath) throws IOException
	{
		File file = new File(rootFolder + login + filePath);
		if (!file.exists())
		{
			throw new FileNotFoundException(filePath);
		}

		if (file.isDirectory())
		{
			FileUtils.deleteDirectory(file);
			return true;
		}
		else
		{
			return file.delete();
		}
	}

	public void setFilePart(String login, FileTransfer fileTransfer) throws IOException
	{
		File file = new File(rootFolder + login + fileTransfer.getPath());
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			if (!file.createNewFile())
			{
				throw new IOException("Can not create file " + file.getPath());
			}
		}

		fileTransfer.write(file);
	}

	public void createFolder(String login, String path) throws IOException
	{
		File file = new File(rootFolder + login + path);
		if (!file.exists())
		{
			if (!file.mkdirs())
			{
				throw new IOException("Can not create folder " + path);
			}
		}
	}

}
