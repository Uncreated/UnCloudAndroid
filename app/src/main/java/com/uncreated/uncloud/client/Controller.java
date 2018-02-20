package com.uncreated.uncloud.client;

import com.uncreated.uncloud.client.requests.RequestHandler;

public abstract class Controller<T extends View>
{
	protected T view;

	private Runnable lastRunnable;

	protected RequestHandler requestHandler;

	public Controller(RequestHandler requestHandler)
	{
		this.requestHandler = requestHandler;
	}

	public synchronized void onAttach(T view)
	{
		this.view = view;
		if (lastRunnable != null)
		{
			view.call(lastRunnable);
			lastRunnable = null;
		}
	}

	public synchronized void onDetach()
	{
		this.view = null;
	}

	protected synchronized void call(Runnable runnable)
	{
		if (view != null)
		{
			view.call(runnable);
		}
		else
		{
			lastRunnable = runnable;
		}
	}

	protected void runThread(Runnable r)
	{
		new Thread(r).start();
	}
}
