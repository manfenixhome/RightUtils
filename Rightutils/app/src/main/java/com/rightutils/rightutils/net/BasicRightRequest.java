package com.rightutils.rightutils.net;

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
 * Created by Anton on 3/23/2015.
 */
public class BasicRightRequest implements RightRequest {

	private static final String TAG = BasicRightRequest.class.getSimpleName();
	protected static final int DEFAULT_MAX_TOTAL = 3;
	protected static final int DEFAULT_MAX_PER_ROUTE = 2;

	protected int maxTotal = DEFAULT_MAX_TOTAL;
	protected int maxPerRoute = DEFAULT_MAX_PER_ROUTE;
	protected Registry<ConnectionSocketFactory> socketFactoryRegistry;

	private BasicRightRequest() {
		initSocketFactory();
	}

	public BasicRightRequest(int maxTotal, int maxPerRoute) {
		this.maxTotal = maxTotal;
		this.maxPerRoute = maxPerRoute;
	}

	private void initSocketFactory() {
		socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", TlsSniSocketFactory.INSTANCE)
				.build();
	}

	//GET METHODS
	@Override
	public HttpResponse getHttpResponse(String url) throws Exception {
		HttpGet get = getGet(url);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(get);
	}

	@Override
	public HttpResponse getHttpResponse(String url, RequestConfig config) throws Exception {
		HttpGet get = getGet(url);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(get);
	}

	@Override
	public HttpResponse getHttpResponse(String url, Header[] headers) throws Exception {
		HttpGet get = getGetHeaderToken(url, headers);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(get);
	}

	@Override
	public HttpResponse getHttpResponse(String url, Header header) throws Exception {
		HttpGet get = getGetHeaderToken(url, header);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(get);
	}


	//POST methods
	@Override
	public HttpResponse postHttpResponse(String url, List<NameValuePair> nameValuePairs) throws Exception {
		HttpPost post = getPost(url);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	@Override
	public HttpResponse postHttpResponse(String url, Header header, List<NameValuePair> nameValuePairs) throws Exception {
		HttpPost post = getPostHeaderToken(url, header);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	@Override
	public HttpResponse postHttpResponse(String url, Header[] headers, List<NameValuePair> nameValuePairs) throws Exception {
		HttpPost post = getPostHeaderToken(url, headers);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	@Override
	public HttpResponse postHttpResponse(String url, HttpEntity entity) throws Exception {
		HttpPost post = getPost(url);
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	@Override
	public HttpResponse postHttpResponse(String url, Header header, HttpEntity entity) throws Exception {
		HttpPost post = getPostHeaderToken(url, header);
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	@Override
	public HttpResponse postHttpResponse(String url, StringEntity entity) throws Exception {
		HttpPost post = getPost(url);
		post.setHeader("Content-Type", "application/json");
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	@Override
	public HttpResponse postHttpResponse(String url, String json) throws Exception {
		StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		HttpPost post = getPost(url);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	@Override
	public HttpResponse postHttpResponse(String url, Header header, String json) throws Exception {
		StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		HttpPost post = getPostHeaderToken(url, header);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(entity);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(post);
	}

	//DELETE methods

	@Override
	public HttpResponse deleteHttpResponse(String url) throws Exception {
		HttpDelete delete = getDelete(url);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(delete);
	}

	@Override
	public HttpResponse deleteHttpResponse(String url, Header header) throws Exception {
		HttpDelete delete = getDeleteHeaderToken(url, header);
		HttpClient httpClient = getHttpClient();
		return httpClient.execute(delete);
	}

	//inner methods
	private HttpClient getHttpClient() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL);
		connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
		return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
	}

	private HttpPost getPost(String url) {
		HttpPost post = new HttpPost(url);
		return post;
	}

	private HttpPost getPostHeaderToken(String url, Header[] headers) {
		HttpPost post = new HttpPost(url);
		post.setHeaders(headers);
		return post;
	}

	private HttpPost getPostHeaderToken(String url, Header header) {
		HttpPost post = new HttpPost(url);
		post.setHeader(header);
		return post;
	}

	private HttpGet getGetHeaderToken(String url, Header[] headers) {
		HttpGet get = getGet(url);
		get.setHeaders(headers);
		return get;
	}

	private HttpGet getGetHeaderToken(String url, Header header) {
		HttpGet get = getGet(url);
		get.setHeader(header);
		return get;
	}

	private HttpGet getGet(String url) {
		HttpGet get = new HttpGet(url);
		return get;
	}

	private HttpDelete getDelete(String url) {
		HttpDelete delete = new HttpDelete(url);
		return delete;
	}

	private HttpDelete getDeleteHeaderToken(String url, Header header) {
		HttpDelete delete = new HttpDelete(url);
		delete.setHeader(header);
		return delete;
	}
}
