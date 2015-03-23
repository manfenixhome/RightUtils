package com.rightutils.rightutils.net;

import java.util.List;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.config.RequestConfig;
import ch.boye.httpclientandroidlib.entity.StringEntity;

/**
 * Created by Anton on 3/23/2015.
 */
public interface RightRequest {

	HttpResponse getHttpResponse(String url) throws Exception;

	HttpResponse getHttpResponse(String url, RequestConfig config) throws Exception;

	HttpResponse getHttpResponse(String url, Header[] headers) throws Exception;

	HttpResponse getHttpResponse(String url, Header header) throws Exception;

	HttpResponse postHttpResponse(String url, List<NameValuePair> nameValuePairs) throws Exception;

	HttpResponse postHttpResponse(String url, Header header, List<NameValuePair> nameValuePairs) throws Exception;

	HttpResponse postHttpResponse(String url, Header[] headers, List<NameValuePair> nameValuePairs) throws Exception;

	HttpResponse postHttpResponse(String url, HttpEntity entity) throws Exception;

	HttpResponse postHttpResponse(String url, Header header, HttpEntity entity) throws Exception;

	HttpResponse postHttpResponse(String url, StringEntity entity) throws Exception;

	HttpResponse postHttpResponse(String url, String json) throws Exception;

	HttpResponse postHttpResponse(String url, Header header, String json) throws Exception;

	HttpResponse deleteHttpResponse(String url) throws Exception;

	HttpResponse deleteHttpResponse(String url, Header header) throws Exception;
}
