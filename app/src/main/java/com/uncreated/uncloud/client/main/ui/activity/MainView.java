package com.uncreated.uncloud.client.main.ui.activity;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface MainView
		extends MvpView
{
	@StateStrategyType(SingleStateStrategy.class)
	void switchFiles();

	@StateStrategyType(SingleStateStrategy.class)
	void switchSettings();

	@StateStrategyType(SingleStateStrategy.class)
	void switchAbout();

	@StateStrategyType(SkipStrategy.class)
	void logout();
}
