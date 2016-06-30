package com.mohoo.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class OkHttpUtil {
	private static final OkHttpClient client = new OkHttpClient();
	public static final MediaType JSON = MediaType
			.parse("application/json; charset=utf-8");

	static {
		client.setConnectTimeout(30, TimeUnit.SECONDS);
	}

	/**
	 * 该不会开启异步线程。
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static Response execute(Request request) throws IOException {
		return client.newCall(request).execute();
	}

	/**
	 * 开启异步线程访问网络
	 * 
	 * @param request
	 * @param responseCallback
	 */
	public static void enqueue(Request request, Callback responseCallback) {
		client.newCall(request).enqueue(responseCallback);
	}
	/**
	 * 根据url地址获取数据
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String doGetHttpRequest(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			System.out.println("服务端错误：" + response);
			throw new IOException("Unexpected code " + response);
		} 
		return response.body().string();
	}
	/**
	 * 根据url地址和json数据获取数据
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static String doPostHttpRequest(String url, String json)
			throws IOException {
		Request request = new Request.Builder().url(url)
				.post(RequestBody.create(JSON, json)).build();
		
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			System.out.println("服务端错误：" + response);
			throw new IOException("Unexpected code " + response);
		}
		return  response.body().string();
	}
	/**
	 * 根据url地址和json数据获取数据
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static String doPostHttpRequest2(String url, String json)
			throws IOException {
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType,
					json
				);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("content-type", "application/json")
				.build();
		
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			System.out.println("服务端错误：" + response);
			throw new IOException("Unexpected code " + response);
		}
		return  response.body().string();
	}
}
