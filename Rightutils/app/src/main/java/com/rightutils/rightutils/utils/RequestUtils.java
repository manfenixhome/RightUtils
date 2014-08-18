package com.rightutils.rightutils.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.util.List;

/**
 * Created by Anton Maniskevich on 25.07.2014.
 */
public class RequestUtils {

	private static final String TAG = RequestUtils.class.getSimpleName();

	public static HttpResponse postHttpResponse(String url, List<NameValuePair> nameValuePairs) throws Exception {
		HttpPost post = new HttpPost(url);
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		return httpClient.execute(post);
	}

	public static HttpResponse getHttpResponse(String url) throws Exception {
		HttpGet get = new HttpGet(url);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		return httpClient.execute(get);
	}

	private RequestUtils() {
	}
}
