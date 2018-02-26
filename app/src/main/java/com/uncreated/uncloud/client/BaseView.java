package com.uncreated.uncloud.client;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface BaseView
		extends MvpView
{
	@StateStrategyType(SkipStrategy.class)
	void showError(String message);

	@StateStrategyType(SkipStrategy.class)
	void showMessage(String message);

	@StateStrategyType(AddToEndSingleStrategy.class)
	void setLoading(boolean show);
}
