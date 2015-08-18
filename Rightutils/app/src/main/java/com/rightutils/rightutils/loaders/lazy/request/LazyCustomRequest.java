package com.rightutils.rightutils.loaders.lazy.request;

import ch.boye.httpclientandroidlib.HttpResponse;

/**
 * Created by victorpaul on 18/08/15.
 */
public interface LazyCustomRequest {
    HttpResponse getResponse() throws Exception;
}
