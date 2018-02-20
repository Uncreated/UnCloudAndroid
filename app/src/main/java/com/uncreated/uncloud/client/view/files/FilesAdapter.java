package com.uncreated.uncloud.client.view.files;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.common.filestorage.FNode;
import com.uncreated.uncloud.common.filestorage.FileNode;
import com.uncreated.uncloud.common.filestorage.FolderNode;

import java.util.LinkedList;
import java.util.List;

public class FilesAdapter
		extends RecyclerView.Adapter<FilesAdapter.ViewHolder>
{
	private List<FNode> files;
	private FilesActivity filesActivity;
	private RecyclerView recyclerView;
	private boolean back;

	public FilesAdapter(FilesActivity filesActivity, RecyclerView recyclerView, FolderNode folderNode)
	{
		files = new LinkedList<>();
		for (FolderNode folder : folderNode.getFolders())
		{
			files.add(folder);
		}
		for (FileNode file : folderNode.getFiles())
		{
			files.add(file);
		}
		this.filesActivity = filesActivity;
		this.recyclerView = recyclerView;
		this.back = folderNode.getParentFolder() != null;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
		v.setOnClickListener(view ->
		{
			int pos = recyclerView.getChildLayoutPosition(v);
			if (back)
			{
				if (pos == 0)
				{
					filesActivity.goBack();
					return;
				}
				else
				{
					pos--;
				}
			}
			FNode fNode = files.get(pos);
			filesActivity.onClickFNode(fNode);
		});
		v.setOnLongClickListener(view ->
		{
			int pos = recyclerView.getChildLayoutPosition(v);
			if (back)
			{
				if (pos == 0)
				{
					return false;
				}
				else
				{
					pos--;
				}
			}
			FNode fNode = files.get(pos);
			filesActivity.openDialog(fNode);
			return true;
		});
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		if (back)
		{
			if (position == 0)
			{
				holder.textView.setText("..");
				holder.imageView.setImageResource(R.drawable.back_folder);
				return;
			}
			position--;
		}
		FNode fNode = files.get(position);
		holder.textView.setText(fNode.getName());
		if (fNode instanceof FolderNode)
		{
			if (fNode.isOnClient() && fNode.isOnServer())
			{
				holder.imageView.setImageResource(R.drawable.client_server_folder);
			}
			else if (fNode.isOnClient())
			{
				holder.imageView.setImageResource(R.drawable.client_folder);
			}
			else if (fNode.isOnServer())
			{
				holder.imageView.setImageResource(R.drawable.server_folder);
			}
		}
		else if (fNode instanceof FileNode)
		{
			if (fNode.isOnClient() && fNode.isOnServer())
			{
				holder.imageView.setImageResource(R.drawable.client_server_file);
			}
			else if (fNode.isOnClient())
			{
				holder.imageView.setImageResource(R.drawable.client_file);
			}
			else if (fNode.isOnServer())
			{
				holder.imageView.setImageResource(R.drawable.server_file);
			}
		}
	}

	@Override
	public int getItemCount()
	{
		if (back)
		{
			return files.size() + 1;
		}
		else

		{
			return files.size();
		}
	}

	public class ViewHolder
			extends RecyclerView.ViewHolder
	{
		public ImageView imageView;
		public TextView textView;

		public ViewHolder(View itemView)
		{
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView);
			textView = itemView.findViewById(R.id.textView);
		}

	}


}
