package com.uncreated.uncloud.client.auth.ui.activity;


import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.uncreated.uncloud.client.BaseView;

import java.util.Set;

public interface AuthView extends BaseView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void addNames(Set<String> names);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setPassword(boolean lock);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setLogin(String login, boolean lock);

    @StateStrategyType(SkipStrategy.class)
    void switchMainActivity();
}
