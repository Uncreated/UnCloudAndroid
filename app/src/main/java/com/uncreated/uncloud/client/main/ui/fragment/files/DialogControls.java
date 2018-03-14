package com.uncreated.uncloud.client.main.ui.fragment.files;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.main.presentation.FileInfo;

class DialogControls extends Dialog {
    private final FileInfo fileInfo;
    private final View.OnClickListener onClickDownload;
    private final View.OnClickListener onClickUpload;
    private final View.OnClickListener onClickDeleteClient;
    private final View.OnClickListener onClickDeleteServer;

    DialogControls(@NonNull Context context, FileInfo fileInfo,
                   View.OnClickListener onClickDownload,
                   View.OnClickListener onClickUpload,
                   View.OnClickListener onClickDeleteClient,
                   View.OnClickListener onClickDeleteServer) {
        super(context);

        this.fileInfo = fileInfo;
        this.onClickDownload = onClickDownload;
        this.onClickUpload = onClickUpload;
        this.onClickDeleteClient = onClickDeleteClient;
        this.onClickDeleteServer = onClickDeleteServer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_controls);

        TextView textView = findViewById(R.id.title);
        textView.setText(fileInfo.getName());

        if (fileInfo.isDownloadAny()) {
            set(R.id.download, R.drawable.download, "Download", onClickDownload);
        } else {
            remove(R.id.download);
        }

        if (fileInfo.isUploadAny()) {
            set(R.id.upload, R.drawable.upload, "Upload", onClickUpload);
        } else {
            remove(R.id.upload);
        }

        if (fileInfo.isDeleteAnyClient()) {
            set(R.id.deleteClient, R.drawable.delete_client, "Delete from client", onClickDeleteClient);
        } else {
            remove(R.id.deleteClient);
        }

        if (fileInfo.isDeleteAnyServer()) {
            set(R.id.deleteServer, R.drawable.delete_server, "Delete from server", onClickDeleteServer);
        } else {
            remove(R.id.deleteServer);
        }
    }

    private void remove(int idView) {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeView(findViewById(idView));
    }

    private void set(int idView, int idImage, String text, View.OnClickListener onClickListener) {
        View view = findViewById(idView);
        ImageView imageView = view.findViewById(R.id.image_view);
        imageView.setImageResource(idImage);
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(text);
        view.setOnClickListener(onClickListener);
    }
}
