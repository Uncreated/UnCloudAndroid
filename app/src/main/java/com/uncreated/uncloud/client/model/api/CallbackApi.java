package com.uncreated.uncloud.client.model.api;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallbackApi<T> implements Callback<T> {
    private final SparseArray<Runnable> handlers;
    private OnCompleteEvent<T> onCompleteEvent;
    private OnFailedEvent onFailedEvent;

    public CallbackApi() {
        handlers = new SparseArray<>();
    }

    public CallbackApi<T> setOnCompleteEvent(OnCompleteEvent<T> onCompleteEvent) {
        this.onCompleteEvent = onCompleteEvent;
        return this;
    }

    public CallbackApi<T> setOnFailedEvent(OnFailedEvent onFailedEvent) {
        this.onFailedEvent = onFailedEvent;
        return this;
    }

    OnCompleteEvent<T> getOnCompleteEvent() {
        return onCompleteEvent;
    }

    public CallbackApi<T> add(Integer code, Runnable handler) {
        handlers.put(code, handler);
        return this;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            if (onCompleteEvent != null) {
                onCompleteEvent.onComplete(response.body());
            }
        } else {
            Runnable runnable = handlers.get(response.code());
            if (runnable != null) {
                runnable.run();
            } else if (onFailedEvent != null) {
                try {
                    ResponseBody errorBody = response.errorBody();
                    if (errorBody != null) {
                        onFailedEvent.onFailed(errorBody.string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                onFailedEvent.onFailed("Request error (" + response.code() + ")");
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        onFailedEvent.onFailed(t.getMessage());
    }

    public interface OnCompleteEvent<T> {
        void onComplete(T body);
    }

    public interface OnFailedEvent {
        void onFailed(String message);
    }
}