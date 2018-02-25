package com.uncreated.uncloud.client.mvp.base.presentation;


import com.uncreated.uncloud.client.mvp.base.ui.View;

public abstract class Presenter<V extends View>
{
	protected ViewState<V> viewState;

	public Presenter(ViewState<V> viewState)
	{
		this.viewState = viewState;
	}

	public void attach(V view)
	{
		viewState.attach(view);
	}

	public void detach(V view)
	{
		viewState.detach(view);
	}
}
