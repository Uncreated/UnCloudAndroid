package com.uncreated.uncloud.client.view.files;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.common.filestorage.FNode;

public class DialogControls
		extends Dialog
{
	private FNode fNode;
	private View.OnClickListener onClickDownload;
	private View.OnClickListener onClickUpload;
	private View.OnClickListener onClickDeleteClient;
	private View.OnClickListener onClickDeleteServer;

	public DialogControls(@NonNull Context context, FNode fNode)
	{
		super(context);
		this.fNode = fNode;
	}

	public void setOnClickDownload(View.OnClickListener onClickDownload)
	{
		this.onClickDownload = onClickDownload;
	}

	public void setOnClickUpload(View.OnClickListener onClickUpload)
	{
		this.onClickUpload = onClickUpload;
	}

	public void setOnClickDeleteClient(View.OnClickListener onClickDeleteClient)
	{
		this.onClickDeleteClient = onClickDeleteClient;
	}

	public void setOnClickDeleteServer(View.OnClickListener onClickDeleteServer)
	{
		this.onClickDeleteServer = onClickDeleteServer;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.dialog_controls);

		TextView textView = findViewById(R.id.title);
		textView.setText(fNode.getName());

		if (!fNode.isOnClient())
		{
			set(R.id.download, R.drawable.download, "Download", onClickDownload);
		}
		else
		{
			remove(R.id.download);
		}

		if (!fNode.isOnServer())
		{
			set(R.id.upload, R.drawable.upload, "Upload", onClickUpload);
		}
		else
		{
			remove(R.id.upload);
		}

		if (fNode.isOnClient())
		{
			set(R.id.deleteClient, R.drawable.delete_client, "Delete from client", onClickDeleteClient);
		}
		else
		{
			remove(R.id.deleteClient);
		}

		if (fNode.isOnServer())
		{
			set(R.id.deleteServer, R.drawable.delete_server, "Delete from server", onClickDeleteServer);
		}
		else
		{
			remove(R.id.deleteServer);
		}
	}

	private void remove(int idView)
	{
		LinearLayout linearLayout = findViewById(R.id.linearLayout);
		linearLayout.removeView(findViewById(idView));
	}

	private void set(int idView, int idImage, String text, View.OnClickListener onClickListener)
	{
		View view = findViewById(idView);
		ImageView imageView = view.findViewById(R.id.imageView);
		imageView.setImageResource(idImage);
		TextView textView = view.findViewById(R.id.textView);
		textView.setText(text);
		view.setOnClickListener(onClickListener);
	}


}
