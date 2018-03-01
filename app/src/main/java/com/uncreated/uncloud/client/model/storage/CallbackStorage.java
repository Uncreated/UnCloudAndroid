package com.uncreated.uncloud.client.model.storage;

import android.os.Handler;
import android.os.Looper;


public class CallbackStorage {
    private OnCompleteEvent onCompleteEvent;
    private OnFailedEvent onFailedEvent;

    public CallbackStorage(OnCompleteEvent onCompleteEvent, OnFailedEvent onFailedEvent) {
        this.onCompleteEvent = onCompleteEvent;
        this.onFailedEvent = onFailedEvent;
    }

    public void onCompletePost() {
        new Handler(Looper.getMainLooper()).post(onCompleteEvent::run);
    }

    public void onFailedPost(String message) {
        new Handler(Looper.getMainLooper()).post(() -> onFailedEvent.run(message));
    }

    public interface OnCompleteEvent {
        void run();
    }

    public interface OnFailedEvent {
        void run(String message);
    }
}