package com.uncreated.uncloud.client.model.fileloader;

import android.os.Handler;
import android.os.Looper;


public class CallbackLoader {
    private OnCompleteEvent onCompleteEvent;
    private OnFailedEvent onFailedEvent;

    public CallbackLoader(OnCompleteEvent onCompleteEvent, OnFailedEvent onFailedEvent) {
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