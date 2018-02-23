package com.uncreated.uncloud.client.main.view;

import com.uncreated.uncloud.client.View;
import com.uncreated.uncloud.client.main.FileInfo;

import java.util.ArrayList;

public interface FilesView
		extends View
{
	void showFolder(ArrayList<FileInfo> files, boolean rootFolder);

	void onFailRequest(String message);
}
