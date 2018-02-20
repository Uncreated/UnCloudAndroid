package com.uncreated.uncloud.client.files.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.files.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class FilesAdapter
		extends RecyclerView.Adapter<FilesAdapter.ViewHolder>
{
	private List<FileInfo> files;
	private FilesActivity filesActivity;
	private RecyclerView recyclerView;
	private boolean back;

	public FilesAdapter(FilesActivity filesActivity, RecyclerView recyclerView, ArrayList<FileInfo> files, boolean back)
	{
		this.files = files;

		this.filesActivity = filesActivity;
		this.recyclerView = recyclerView;
		this.back = back;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
		v.setOnClickListener(view ->
		{
			filesActivity.onClickFile(get(v));
		});
		v.setOnLongClickListener(view ->
		{
			filesActivity.onLongClickFile(get(v));
			return true;
		});
		return new ViewHolder(v);
	}

	private FileInfo get(View view)
	{
		int pos = recyclerView.getChildLayoutPosition(view);
		if (back)
		{
			if (pos == 0)
			{
				return null;
			}
			else
			{
				pos--;
			}
		}
		return files.get(pos);
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
		FileInfo fileInfo = files.get(position);
		holder.textView.setText(fileInfo.getName());
		if (fileInfo.isDirectory())
		{
			if (fileInfo.isDownloaded() && fileInfo.isUploaded())
			{
				holder.imageView.setImageResource(R.drawable.client_server_folder);
			}
			else if (fileInfo.isDownloaded())
			{
				holder.imageView.setImageResource(R.drawable.client_folder);
			}
			else if (fileInfo.isUploaded())
			{
				holder.imageView.setImageResource(R.drawable.server_folder);
			}
		}
		else
		{
			if (fileInfo.isDownloaded() && fileInfo.isUploaded())
			{
				holder.imageView.setImageResource(R.drawable.client_server_file);
			}
			else if (fileInfo.isDownloaded())
			{
				holder.imageView.setImageResource(R.drawable.client_file);
			}
			else if (fileInfo.isUploaded())
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

	class ViewHolder
			extends RecyclerView.ViewHolder
	{
		ImageView imageView;
		TextView textView;

		ViewHolder(View itemView)
		{
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView);
			textView = itemView.findViewById(R.id.textView);
		}
	}
}