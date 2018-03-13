package com.uncreated.uncloud.client;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.uncreated.uncloud.R;

public class Base implements BaseView {
    private final AlertDialog alertDialog;
    private final Context context;

    Base(Context context) {
        this.context = context;

        alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.loading_dialog)
                .setOnKeyListener(null)
                .setCancelable(false)
                .create();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMessage(String message) {
        showError(message);//dbg
    }

    @Override
    public void setLoading(boolean show) {
        Log.e("setLoading", show + "");
        if (show) {
            alertDialog.show();
            Log.e("setLoading", "show");
        } else {
            alertDialog.hide();
            Log.e("setLoading", "hide");
        }
    }

    public void onDestroy() {
        alertDialog.dismiss();
    }
}
