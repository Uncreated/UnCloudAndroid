package com.uncreated.uncloud.client.ui.fragment.files;

import com.uncreated.uncloud.client.old.View;
import com.uncreated.uncloud.client.old.main.FileInfo;

import java.util.ArrayList;

public interface FilesView
		extends View
{
	void showFolder(ArrayList<FileInfo> files, boolean rootFolder);

	void onFailRequest(String msg);
}
