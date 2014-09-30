package com.rightutils.rightutils.net;

import java.io.IOException;
import java.util.List;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.config.Registry;
import ch.boye.httpclientandroidlib.config.RegistryBuilder;
import ch.boye.httpclientandroidlib.conn.socket.ConnectionSocketFactory;
import ch.boye.httpclientandroidlib.conn.socket.PlainConnectionSocketFactory;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.impl.client.HttpClientBuilder;
import ch.boye.httpclientandroidlib.impl.conn.PoolingHttpClientConnectionManager;
import ch.boye.httpclientandroidlib.protocol.HTTP;

/**
 * Created by Anton Maniskevich on 25.07.2014.
 */
public class RequestUtils {

	private static final String TAG = RequestUtils.class.getSimpleName();

	private RequestUtils() {
	}
	private final static Registry<ConnectionSocketFactory> socketFactoryRegistry;
	static {
		socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", TlsSniSocketFactory.INSTANCE)
				.build();
	}

	public static HttpResponse postHttpResponse(String url, List<NameValuePair> nameValuePairs) throws Exception {
		HttpPost post = getPost(url);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(3);
		connectionManager.setDefaultMaxPerRoute(2);
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
		return httpClient.execute(post);
	}

	public static HttpResponse getHttpResponse(String url) throws Exception {
		HttpGet get = getGet(url);
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(3);
		connectionManager.setDefaultMaxPerRoute(2);
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
		return httpClient.execute(get);
	}

	public static HttpResponse postHttpResponse(String url, HttpEntity entity) throws IOException {
		HttpPost post = getPost(url);
		post.setEntity(entity);
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(3);
		connectionManager.setDefaultMaxPerRoute(2);
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
		return httpClient.execute(post);
	}

	public static HttpResponse postHttpResponse(String url, StringEntity entity) throws IOException {
		HttpPost post = getPost(url);
		post.setHeader("Content-Type", "application/json");
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		post.setEntity(entity);
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(3);
		connectionManager.setDefaultMaxPerRoute(2);
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
		return httpClient.execute(post);
	}

	private static HttpPost getPostHeaderToken(String url, Header[] headers) {
		HttpPost post = getPost(url);
		post.setHeaders(headers);
		return post;
	}

	private static HttpPost getPost(String url) {
		HttpPost post = new HttpPost(url);
		return post;
	}

	private static HttpGet getGetHeaderToken(String url, Header[] headers) {
		HttpGet get = getGet(url);
		get.setHeaders(headers);
		return get;
	}

	private static HttpGet getGet(String url) {
		HttpGet get = new HttpGet(url);
		return get;
	}

}
