package com.rightutils.rightutils.net;

import java.io.IOException;
import java.util.List;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.config.RequestConfig;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.config.Registry;
import ch.boye.httpclientandroidlib.config.RegistryBuilder;
import ch.boye.httpclientandroidlib.conn.socket.ConnectionSocketFactory;
import ch.boye.httpclientandroidlib.conn.socket.PlainConnectionSocketFactory;
import ch.boye.httpclientandroidlib.entity.ContentType;
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

	//GET METHODS
	public static HttpResponse getHttpResponse(String url) throws Exception {
		HttpGet get = getGet(url);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(get);
	}

	public static HttpResponse getHttpResponse(String url, RequestConfig config) throws Exception {
		HttpGet get = getGet(url);
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(3);
		connectionManager.setDefaultMaxPerRoute(2);
		HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).setConnectionManager(connectionManager).build();
		return httpClient.execute(get);
	}

	public static HttpResponse getHttpResponse(String url, Header[] headers) throws Exception {
		HttpGet get = getGetHeaderToken(url, headers);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(get);
	}

	public static HttpResponse getHttpResponse(String url, Header header) throws Exception {
		HttpGet get = getGetHeaderToken(url, header);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(get);
	}


	//POST methods
	public static HttpResponse postHttpResponse(String url, List<NameValuePair> nameValuePairs) throws Exception {
		HttpPost post = getPost(url);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	public static HttpResponse postHttpResponse(String url, Header header, List<NameValuePair> nameValuePairs) throws IOException {
		HttpPost post = getPostHeaderToken(url, header);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	public static HttpResponse postHttpResponse(String url, Header[] headers, List<NameValuePair> nameValuePairs) throws IOException {
		HttpPost post = getPostHeaderToken(url, headers);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	public static HttpResponse postHttpResponse(String url, HttpEntity entity) throws IOException {
		HttpPost post = getPost(url);
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	public static HttpResponse postHttpResponse(String url, Header header, HttpEntity entity) throws IOException {
		HttpPost post = getPostHeaderToken(url, header);
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	public static HttpResponse postHttpResponse(String url, StringEntity entity) throws IOException {
		HttpPost post = getPost(url);
		post.setHeader("Content-Type", "application/json");
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	public static HttpResponse postHttpResponse(String url, String json) throws IOException {
		StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		HttpPost post = getPost(url);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	public static HttpResponse postHttpResponse(String url, Header header, String json) throws IOException {
		StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		HttpPost post = getPostHeaderToken(url, header);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	//DELETE methods

	public static HttpResponse deleteHttpResponse(String url) throws IOException {
		HttpDelete delete = getDelete(url);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(delete);
	}

	public static HttpResponse deleteHttpResponse(String url, Header header) throws IOException {
		HttpDelete delete = getDeleteHeaderToken(url, header);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(delete);
	}

	//inner methods
	private static HttpClient getHttpClient() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(3);
		connectionManager.setDefaultMaxPerRoute(2);
		return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
	}

	private static HttpPost getPost(String url) {
		HttpPost post = new HttpPost(url);
		return post;
	}

	private static HttpPost getPostHeaderToken(String url, Header[] headers) {
		HttpPost post = new HttpPost(url);
		post.setHeaders(headers);
		return post;
	}

	private static HttpPost getPostHeaderToken(String url, Header header) {
		HttpPost post = new HttpPost(url);
		post.setHeader(header);
		return post;
	}

	private static HttpGet getGetHeaderToken(String url, Header[] headers) {
		HttpGet get = getGet(url);
		get.setHeaders(headers);
		return get;
	}

	private static HttpGet getGetHeaderToken(String url, Header header) {
		HttpGet get = getGet(url);
		get.setHeader(header);
		return get;
	}

	private static HttpGet getGet(String url) {
		HttpGet get = new HttpGet(url);
		return get;
	}

	private static HttpDelete getDelete(String url) {
		HttpDelete delete = new HttpDelete(url);
		return delete;
	}

	private static HttpDelete getDeleteHeaderToken(String url, Header header) {
		HttpDelete delete = new HttpDelete(url);
		delete.setHeader(header);
		return delete;
	}

}
