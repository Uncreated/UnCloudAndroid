package com.uncreated.uncloud.client.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.uncreated.uncloud.R;

public class LoaderService extends Service {
    private static final String CHANNEL_LOADING = "loadingChannel";

    private final ServiceBinder binder = new ServiceBinder();
    private LoaderTaskManager loaderTaskManager;

    @Override
    public void onCreate() {
        super.onCreate();

        loaderTaskManager = new LoaderTaskManager(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onNotification(LoaderWorker.Progress progress) {
        if (progress == null) {
            stopForeground(true);
        } else {
            //debug
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_LOADING)
                    .setSmallIcon(R.drawable.uncloud)
                    .setContentTitle(progress.getLogin())
                    .setContentText(progress.getPath())
                    .setProgress(progress.getMax(), progress.getCur(), false)
                    .build();
            startForeground(123, notification);
        }
    }

    public class ServiceBinder extends Binder {
        public LoaderTaskManager getLoaderTaskManager() {
            return loaderTaskManager;
        }
    }
}
