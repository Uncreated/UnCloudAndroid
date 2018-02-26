package com.uncreated.uncloud.client.model;

import android.content.Context;
import android.util.Base64;

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
import com.uncreated.uncloud.client.model.auth.AuthManager;
import com.uncreated.uncloud.client.model.storage.Storage;

import java.lang.reflect.Type;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Model
{
	private static final String SERVER_URL = "http://10.0.2.2:8080/";

	private static AuthManager authManager;
	private static ApiClient apiClient;
	private static Storage storage;
	private static Gson gson = new GsonBuilder()
			.registerTypeHierarchyAdapter(byte[].class,
					new ByteArrayToBase64TypeAdapter()).create();

	public static void init(Context context)
	{
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(SERVER_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.build();

		//GsonConverterFactory.

		authManager = new AuthManager(context);
		apiClient = new ApiClient(retrofit);
		storage = new Storage(context.getFilesDir().getAbsolutePath());
	}

	public static Gson getGson()
	{
		return gson;
	}

	public static AuthManager getAuthManager()
	{
		return authManager;
	}

	public static ApiClient getApiClient()
	{
		return apiClient;
	}

	public static Storage getStorage()
	{
		return storage;
	}

	private static class ByteArrayToBase64TypeAdapter
			implements JsonSerializer<byte[]>, JsonDeserializer<byte[]>
	{
		public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			return Base64.decode(json.getAsString(), Base64.NO_WRAP);
		}

		public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context)
		{
			return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
		}
	}
}
