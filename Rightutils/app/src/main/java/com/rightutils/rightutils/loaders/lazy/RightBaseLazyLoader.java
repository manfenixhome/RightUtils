package com.rightutils.rightutils.loaders.lazy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.widget.Toast;
import com.rightutils.rightutils.loaders.BaseLoader;
import com.rightutils.rightutils.loaders.LoaderListener;
import com.rightutils.rightutils.loaders.lazy.request.AddHeaderToLazyRequest;
import com.rightutils.rightutils.loaders.lazy.request.AddMultipartEntityBuilderToLazyRequest;
import com.rightutils.rightutils.loaders.lazy.request.AddPostJsonToLazyRequest;
import com.rightutils.rightutils.loaders.lazy.request.LazyBaseRequest;
import com.rightutils.rightutils.loaders.lazy.request.PostToPutLazyRequest;
import com.rightutils.rightutils.net.BasicRightRequest;

import java.io.IOException;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.util.EntityUtils;

/**
 * Created by victorpaul on 14/08/15.
 */
abstract public class RightBaseLazyLoader extends BaseLoader<Boolean> implements LoaderListener<Boolean> {
    public final static String TAG = RightBaseLazyLoader.class.getSimpleName();
    public final static int NO_INTERNET = 0;

    public interface CallbackResponse {
        void response(int pageCode,String response) throws Exception;
    }
    public interface CallbackCanceled {
        void run();
    }
    public interface CallbackNoInternet {
        void noInternet();
    }

    public static boolean debug = true;
    private int statusCodeResponse = NO_INTERNET;
    private String stringResponse;
    private SparseArrayCompat<CallbackResponse> listeners;
    private CallbackResponse defaultListener = null;
    private CallbackCanceled cancelListener = null;
    private CallbackNoInternet noInternetListener = null;

    private LoaderListener<Boolean> usersLoaderListener;

    public LazyBaseRequest request;

    public RightBaseLazyLoader(FragmentActivity fragmentActivity, int loaderId) {
        super(fragmentActivity, loaderId);
        this.listeners = new SparseArrayCompat<>();

        super.setLoaderListener(this);
    }

    @Override
    public BaseLoader<Boolean> setLoaderListener(LoaderListener<Boolean> loaderListener) {
        usersLoaderListener = loaderListener;
        return super.setLoaderListener(this);
    }

    private void log(String log){
        if(debug) {
            Log.i(TAG + " " + request.getClass().getSimpleName(), log);
        }
    }

    public RightBaseLazyLoader setRequest(LazyBaseRequest request){
        this.request = request;
        return this;
    }

    public RightBaseLazyLoader setResponseListener(int page, CallbackResponse listener){
        listeners.put(page, listener);
        return this;
    }

    public RightBaseLazyLoader setDefaultResponseListener(CallbackResponse defaultListener) {
        this.defaultListener = defaultListener;
        return this;
    }

    public RightBaseLazyLoader setOnCancelListener(CallbackCanceled cancelListener){
        this.cancelListener = cancelListener;
        return this;
    }

    public void setNoInternetListener(CallbackNoInternet noInternetListener) {
        this.noInternetListener = noInternetListener;
    }

    @Override
    public Boolean loadInBackground()
    {
        if(this.request == null){
            return false;
        }

        try {
            HttpResponse response = getResponse(request);

            if(response == null){
                return false;
            }

            statusCodeResponse = response.getStatusLine().getStatusCode();
            log("PAGE CODE:" + String.valueOf(statusCodeResponse));
            stringResponse = EntityUtils.toString(response.getEntity());
            log("BODY:" + stringResponse);
            if (statusCodeResponse == HttpStatus.SC_OK) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public HttpResponse getResponse(LazyBaseRequest request) throws Exception
    {
        Header header = null;
        if(request instanceof AddHeaderToLazyRequest) {
            header = ((AddHeaderToLazyRequest) request).getHeader();
        }

        BasicRightRequest brr = new BasicRightRequest();
        if(request instanceof AddPostJsonToLazyRequest){ // POST/PUT JSON
            String json = ((AddPostJsonToLazyRequest) request).getPostJson();

            if(request instanceof PostToPutLazyRequest) {
                log("PUT:" + request.getUrl());
            }else{
                log("POST:" + request.getUrl());
            }
            log("JSON:" + json);

            if(header != null) {
                if(request instanceof PostToPutLazyRequest) {
                    return brr.putHttpResponse(request.getUrl(),header,json);
                }
                return brr.postHttpResponse(request.getUrl(),header,json);
            }else{
                if(request instanceof PostToPutLazyRequest) {
                    return brr.putHttpResponse(request.getUrl(), json);
                }
                return brr.postHttpResponse(request.getUrl(), json);
            }

        }else if(request instanceof AddMultipartEntityBuilderToLazyRequest) { // POST/PUT FORM
            MultipartEntityBuilder builder = ((AddMultipartEntityBuilderToLazyRequest) request).getBuilder();
            HttpEntity entity = builder.build();

            if(request instanceof PostToPutLazyRequest) {
                log("PUT:" + request.getUrl());
            }else{
                log("POST:" + request.getUrl());
            }
            log("FORM:" + toString(entity));

            if(header != null) {
                if(request instanceof PostToPutLazyRequest) {
                    return brr.putHttpResponse(request.getUrl(),header, builder.build());
                }
                return brr.postHttpResponse(request.getUrl(),header,entity);
            }else{
                if(request instanceof PostToPutLazyRequest) {
                    return brr.putHttpResponse(request.getUrl(), builder.build());
                }
                return brr.postHttpResponse(request.getUrl(), builder.build());
            }
        }else{ // GET
            log("GET " + request.getUrl());
            if(header != null){
                return brr.getHttpResponse(request.getUrl(),header);
            }else{
                return brr.getHttpResponse(request.getUrl());
            }
        }
    }

    static String toString(HttpEntity entity) throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream((int)entity.getContentLength());
        entity.writeTo(out);
        //byte[] entityContentAsBytes = out.toByteArray();
        return new String(out.toByteArray());
    }

    @Override
    public void onLoadFinished(FragmentActivity fragmentActivity, Fragment fragment, Boolean aBoolean, BaseLoader<Boolean> baseLoader) {
        if(usersLoaderListener != null) {
            usersLoaderListener.onLoadFinished(fragmentActivity, fragment, aBoolean, baseLoader);
        }

        // NO INTERNET
        if(statusCodeResponse == NO_INTERNET) {
            if (noInternetListener == null) {
                Toast.makeText(getContext(), "No internet", Toast.LENGTH_LONG).show();
            } else {
                noInternetListener.noInternet();
            }
        }else {
            // USERS LISTENERS
            if (listeners.get(statusCodeResponse) != null) {
                try {
                    listeners.get(statusCodeResponse).response(statusCodeResponse, stringResponse);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error occurred! Couldn't proceed servers response (" + statusCodeResponse + ")", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                // DEFAULT LISTENER
            } else {
                if (defaultListener == null) {
                    Toast.makeText(getContext(), "Server sent unsupported response (" + statusCodeResponse + ")", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        defaultListener.response(statusCodeResponse, stringResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onCancelLoad() {
        cancelLoad();
        if(cancelListener != null){
            cancelListener.run();
        }
    }
}