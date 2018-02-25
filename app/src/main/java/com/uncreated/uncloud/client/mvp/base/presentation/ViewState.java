package com.uncreated.uncloud.client.mvp.base.presentation;


import com.uncreated.uncloud.client.mvp.base.ui.View;

import java.util.LinkedList;

public abstract class ViewState<V extends View>
{
	private LinkedList<Command> commands;

	private LinkedList<V> views;

	public ViewState()
	{
		this.commands = new LinkedList<>();
		this.views = new LinkedList<>();
	}

	public void attach(V view)
	{
		if (!views.contains(view))
		{
			views.add(view);
			for (Command command : commands)
			{
				command.run(view);
			}
		}
	}

	public void detach(V view)
	{
		views.remove(view);
	}

	protected void append(Command command)
	{
		commands.add(command);
		skip(command);
	}

	protected void appendUnique(Command command)
	{
		for (int i = commands.size() - 1; i >= 0; i--)
		{
			if (commands.get(i).id == command.id)
			{
				commands.remove(i);
			}
		}
		append(command);
	}

	protected void clear(Command command)
	{
		commands.clear();
		append(command);
	}

	protected void skip(Command command)
	{
		for (V view : views)
		{
			command.run(view);
		}
	}

	protected abstract class Command
	{
		private int id;

		public Command(int id)
		{
			this.id = id;
		}

		public int getId()
		{
			return id;
		}

		public abstract void run(V view);
	}
}
