package com.uncreated.uncloud.client.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.uncreated.uncloud.R;
import com.uncreated.uncloud.client.model.auth.AuthManager;
import com.uncreated.uncloud.client.model.fileloader.FileLoader;

import java.util.Locale;

public class FilesAppWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        AuthManager authManager = new AuthManager(context);
        String login = authManager.getLastName();
        String info;

        if (login != null) {
            FileLoader fileLoader = new FileLoader(context);
            fileLoader.setUser(login, null);
            double size = fileLoader.getLocalFolderSize();
            info = String.format(Locale.US, "%.2f", (size) / (double) 1073741824L);
            info = "Local " + info + "Gb";
        } else {
            login = "";
            info = "";
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.files_app_widget);
        views.setTextViewText(R.id.loginTextView, login);
        views.setTextViewText(R.id.infoTextView, info);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void callUpdate(Context context) {
        Intent intent = new Intent(context, FilesAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, FilesAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}

