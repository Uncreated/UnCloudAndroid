package com.uncreated.uncloud.client.old;

import android.content.Context;

public abstract class Controller<VIEW extends View>
{
	protected VIEW view;

	public Controller()
	{
	}

	public void onAttach(VIEW view, Context context)
	{
		this.view = view;
	}

	public void onDetach()
	{
		this.view = null;
	}

	protected void runThread(Runnable r)
	{
		new Thread(r).start();
	}
}
