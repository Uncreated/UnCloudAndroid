package com.uncreated.uncloud.client.model.api;

import android.util.SparseArray;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallbackApi<T> implements Callback<T> {
    private SparseArray<Runnable> handlers;
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

    public OnCompleteEvent<T> getOnCompleteEvent() {
        return onCompleteEvent;
    }

    public OnFailedEvent getOnFailedEvent() {
        return onFailedEvent;
    }

    public CallbackApi<T> add(Integer code, Runnable handler) {
        handlers.put(code, handler);
        return this;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful() && response.body() != null) {
            if (onCompleteEvent != null) {
                onCompleteEvent.onComplete(response.body());
            }
        } else {
            Runnable runnable = handlers.get(response.code());
            if (runnable != null) {
                runnable.run();
            } else {
                if (onFailedEvent != null) {
                    String msg;
                    try {
                        msg = response.errorBody().string();
                    } catch (NullPointerException | IOException e) {
                        e.printStackTrace();
                        msg = "Request error (" + response.code() + ")";
                    }
                    onFailedEvent.onFailed(msg);
                }
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailedEvent.onFailed(t.getMessage());
    }

    public interface OnCompleteEvent<T> {
        void onComplete(T body);
    }

    public interface OnFailedEvent {
        void onFailed(String message);
    }
}