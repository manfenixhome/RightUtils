package com.rightutils.rightutils.loaders.lazy.request;

import org.json.JSONException;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpResponse;

/**
 * Created by victor on 11.08.15.
 */

public abstract class LazyRequest {
    abstract public String getUrl();
    public Header getHeader(){
        return null;
    }
    public String getPostJson() throws JSONException{
        return null;
    }
    public HttpResponse getCustomResponse() throws Exception{
        return null;
    }
}
