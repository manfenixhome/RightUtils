package com.rightutils.rightutils.loaders.lazy.request;

import java.io.File;

import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;

/**
 * Created by victorpaul on 13/08/15.
 */
public abstract class AddMultipartEntityBuilderToLazyRequest implements LazyBaseRequest {
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    public AddMultipartEntityBuilderToLazyRequest addPart(String key,int part){
        return addPart(key, Integer.toString(part));
    }

    public AddMultipartEntityBuilderToLazyRequest addPart(String key,String part){
        builder.addPart(key, new StringBody(part, ContentType.TEXT_PLAIN));
        return this;
    }

    public AddMultipartEntityBuilderToLazyRequest addPart(String key,File file){
        builder.addPart(key, new FileBody(file));
        return this;
    }

    public MultipartEntityBuilder getBuilder() {
        this.builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        return builder;
    }
}
