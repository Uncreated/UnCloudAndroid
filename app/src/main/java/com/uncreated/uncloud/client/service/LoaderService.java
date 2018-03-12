package com.uncreated.uncloud.client.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class LoaderService extends Service {
    private ServiceBinder binder = new ServiceBinder();
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

    public void onNotification(String login, String path) {
        if (login == null) {
            stopForeground(true);
        } else {
            //debug
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentText(login + " " + path)
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
