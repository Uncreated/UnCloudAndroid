package com.uncreated.uncloud.client.auth.ui.activity;


import android.transition.Slide;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.Set;

public interface AuthView
		extends MvpView
{
	@StateStrategyType(AddToEndSingleStrategy.class)
	void addNames(Set<String> names);

	@StateStrategyType(SkipStrategy.class)
	void showError(String message);

	@StateStrategyType(SkipStrategy.class)
	void showMessage(String message);

	@StateStrategyType(AddToEndSingleStrategy.class)
	void setPassword(boolean lock);

	@StateStrategyType(AddToEndSingleStrategy.class)
	void setLogin(String login, boolean lock);

	@StateStrategyType(AddToEndSingleStrategy.class)
	void setLoading(boolean show);

	@StateStrategyType(SkipStrategy.class)
	void switchMainActivity();
}
