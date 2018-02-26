package com.uncreated.uncloud.client.main.ui.fragment.files;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.uncreated.uncloud.client.BaseView;
import com.uncreated.uncloud.client.main.presentation.FileInfo;

import java.util.ArrayList;

public interface FilesView
		extends BaseView
{
	@StateStrategyType(SingleStateStrategy.class)
	void showFolder(ArrayList<FileInfo> files, boolean rootFolder);

	@StateStrategyType(AddToEndSingleStrategy.class)
	void setActionDialog(boolean show, FileInfo fileInfo);
}
