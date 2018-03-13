package com.uncreated.uncloud.client.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.uncreated.uncloud.client.model.api.ApiClient;
import com.uncreated.uncloud.client.model.api.entity.Session;
import com.uncreated.uncloud.client.model.auth.AuthManager;
import com.uncreated.uncloud.client.model.storage.Storage;
import com.uncreated.uncloud.client.service.LoaderService;
import com.uncreated.uncloud.client.service.LoaderTaskManager;

import java.lang.reflect.Type;

public class Model {
    private static AuthManager authManager;
    private static ApiClient apiClient;
    private static Storage storage;
    private static LoaderTaskManager loaderTaskManager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(byte[].class,
                    new ByteArrayToBase64TypeAdapter()).create();

    public static void init(Context context) {
        authManager = new AuthManager(context);
        apiClient = new ApiClient();
        storage = new Storage(context.getFilesDir().getAbsolutePath());

        Intent intent = new Intent(context, LoaderService.class);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                LoaderService.ServiceBinder sb = (LoaderService.ServiceBinder) iBinder;
                loaderTaskManager = sb.getLoaderTaskManager();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Toast.makeText(context, "Can't bind service", Toast.LENGTH_LONG).show();
            }
        }, Context.BIND_AUTO_CREATE);
    }

    public static Gson getGson() {
        return gson;
    }

    public static AuthManager getAuthManager() {
        return authManager;
    }

    public static ApiClient getApiClient() {
        return apiClient;
    }

    public static Storage getStorage() {
        return storage;
    }

    public static LoaderTaskManager getLoaderTaskManager() {
        return loaderTaskManager;
    }

    public static void onAuthorized(Session session) {
        storage.setLogin(session.getLogin());
        apiClient.setAccessToken(session.getAccessToken());
    }

    private static class ByteArrayToBase64TypeAdapter
            implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
        }
    }
}
