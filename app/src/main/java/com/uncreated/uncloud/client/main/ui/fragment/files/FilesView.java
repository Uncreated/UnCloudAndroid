package com.uncreated.uncloud.client.main.ui.fragment.files;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.uncreated.uncloud.client.main.presentation.FileInfo;

import java.util.ArrayList;

public interface FilesView
		extends MvpView
{
	@StateStrategyType(SingleStateStrategy.class)
	void showFolder(ArrayList<FileInfo> files, boolean rootFolder);

	@StateStrategyType(SkipStrategy.class)
	void onFailRequest(String msg);

	@StateStrategyType(AddToEndSingleStrategy.class)
	void setActionDialog(boolean show, FileInfo fileInfo);

	@StateStrategyType(AddToEndSingleStrategy.class)
	void setLoading(boolean show);
}
