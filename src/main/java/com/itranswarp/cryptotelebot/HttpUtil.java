package com.itranswarp.cryptotelebot;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtil {

	public static <T> T getJson(Class<T> clazz, String url) throws IOException {
		String json = wget(url);
		return objectMapper.readValue(json, clazz);
	}

	public static String wget(String url) throws IOException {
		Request request = new Request.Builder().url(url).header("Accept", "*/*").build();
		try (Response response = okhttpClient.newCall(request).execute()) {
			if (response.code() != 200) {
				throw new IOException("Bad response code: " + response.code() + " when fetch url: " + url);
			}
			try (ResponseBody body = response.body()) {
				String json = body.string();
				if (json == null || json.isEmpty()) {
					throw new IOException("Empty response body.");
				}
				return json;
			}
		}
	}

	private static ObjectMapper objectMapper = createObjectMapper();

	private static OkHttpClient okhttpClient = new OkHttpClient.Builder()
			// set connect timeout:
			.connectTimeout(3, TimeUnit.SECONDS)
			// set read timeout:
			.readTimeout(3, TimeUnit.SECONDS)
			// set connection pool:
			.connectionPool(new ConnectionPool(20, 60, TimeUnit.SECONDS))
			// do not retry:
			.retryOnConnectionFailure(false).build();

	private static ObjectMapper createObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return om;
	}
}
