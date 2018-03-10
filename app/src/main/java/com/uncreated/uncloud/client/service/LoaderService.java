package com.uncreated.uncloud.client.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class LoaderService extends Service {
    public static final String KEY_LOGIN = "keyLogin";
    public static final String KEY_ACCESS_TOKEN = "keyAccessToken";

    private ServiceBinder binder = new ServiceBinder();
    private LoaderTaskManager loaderTaskManager;

    @Override
    public void onCreate() {
        super.onCreate();

        loaderTaskManager = new LoaderTaskManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            //Restart


        } else {

        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
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
