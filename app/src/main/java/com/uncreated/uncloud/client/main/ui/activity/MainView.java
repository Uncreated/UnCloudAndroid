package com.uncreated.uncloud.client.main.ui.activity;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.uncreated.uncloud.client.BaseView;

public interface MainView
		extends BaseView
{
	@StateStrategyType(SkipStrategy.class)
	void switchFiles();

	@StateStrategyType(SkipStrategy.class)
	void switchSettings();

	@StateStrategyType(SkipStrategy.class)
	void switchAbout();

	@StateStrategyType(SkipStrategy.class)
	void logout();
}
