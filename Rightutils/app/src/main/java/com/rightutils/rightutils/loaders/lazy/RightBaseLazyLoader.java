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
import com.rightutils.rightutils.loaders.lazy.request.LazyCustomRequest;
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

    private String
            messageNoInternet = "No internet",
            messageErrorProceedResponse = "Error occurred! Couldn't proceed servers response",
            messageUnsupportedResponse = "Server sent unsupported response";

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
    private LoaderListener<Boolean> usersLoaderListener = null;

    private LazyBaseRequest request;

    public RightBaseLazyLoader(FragmentActivity fragmentActivity, int loaderId) {
        super(fragmentActivity, loaderId);
        this.listeners = new SparseArrayCompat<>();

        super.setLoaderListener(this);
    }

    public void setMessageUnsupportedResponse(String messageUnsupportedResponse) {
        this.messageUnsupportedResponse = messageUnsupportedResponse;
    }

    public void setMessageErrorProceedResponse(String messageErrorProceedResponse) {
        this.messageErrorProceedResponse = messageErrorProceedResponse;
    }

    public void setMessageNoInternet(String messageNoInternet) {
        this.messageNoInternet = messageNoInternet;
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

    public HttpResponse buildResponse(LazyBaseRequest request) throws Exception
    {
        Header header = null;
        if(request instanceof AddHeaderToLazyRequest) {
            header = ((AddHeaderToLazyRequest) request).getHeader();
        }

        BasicRightRequest brr = new BasicRightRequest();
        if(request instanceof AddPostJsonToLazyRequest){ // POST JSON
            String json = ((AddPostJsonToLazyRequest) request).getPostJson();
            log("POST JSON:" + json);
            return (header == null)?
                    brr.postHttpResponse(request.getUrl(), json)
                    :
                    brr.postHttpResponse(request.getUrl(),header,json);

        }else if(request instanceof AddMultipartEntityBuilderToLazyRequest) { // POST FORM
            MultipartEntityBuilder builder = ((AddMultipartEntityBuilderToLazyRequest) request).getBuilder();
            HttpEntity entity = builder.build();
            log("POST FORM:" + toString(entity));
            return (header == null)?
                    brr.postHttpResponse(request.getUrl(), builder.build())
                    :
                    brr.postHttpResponse(request.getUrl(),header,entity);

        }else{ // GET
            log("GET");
            return (header == null)?
                    brr.getHttpResponse(request.getUrl())
                    :
                    brr.getHttpResponse(request.getUrl(),header);
        }
    }

    private static String toString(HttpEntity entity) throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream((int)entity.getContentLength());
        entity.writeTo(out);
        return new String(out.toByteArray());
    }

    @Override
    public Boolean loadInBackground(){
        try {
            HttpResponse response = (request instanceof LazyCustomRequest)
                    ?
                    ((LazyCustomRequest)request).getResponse()
                    :
                    buildResponse(request);

            log("URL:" + request.getUrl());
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

    @Override
    public BaseLoader<Boolean> setLoaderListener(LoaderListener<Boolean> loaderListener) {
        usersLoaderListener = loaderListener;
        return super.setLoaderListener(this);
    }

    @Override
    public void onLoadFinished(FragmentActivity fragmentActivity, Fragment fragment, Boolean aBoolean, BaseLoader<Boolean> baseLoader) {
        if(usersLoaderListener != null) {
            usersLoaderListener.onLoadFinished(fragmentActivity, fragment, aBoolean, baseLoader);
        }

        // NO INTERNET
        if(statusCodeResponse == NO_INTERNET) {
            if (noInternetListener == null) {
                Toast.makeText(getContext(), messageNoInternet, Toast.LENGTH_LONG).show();
            } else {
                noInternetListener.noInternet();
            }
        }else {
            // USERS LISTENERS
            if (listeners.get(statusCodeResponse) != null) {
                try {
                    listeners.get(statusCodeResponse).response(statusCodeResponse, stringResponse);
                } catch (Exception e) {
                    Toast.makeText(getContext(), messageErrorProceedResponse + " (" + statusCodeResponse + ")", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                // DEFAULT LISTENER
            } else {
                if (defaultListener == null) {
                    Toast.makeText(getContext(), messageUnsupportedResponse +  " (" + statusCodeResponse + ")", Toast.LENGTH_LONG).show();
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