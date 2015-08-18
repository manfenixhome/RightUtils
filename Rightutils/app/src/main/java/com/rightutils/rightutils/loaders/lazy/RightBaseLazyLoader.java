package com.rightutils.rightutils.loaders.lazy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.widget.Toast;
import com.rightutils.rightutils.loaders.BaseLoader;
import com.rightutils.rightutils.loaders.LoaderListener;
import com.rightutils.rightutils.loaders.lazy.request.AddMultipartEntityBuilderToLazyRequest;
import com.rightutils.rightutils.loaders.lazy.request.LazyRequest;
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

    public static String
            messageNoInternet = "No internet",
            messageErrorProceedResponse = "Error occurred! Couldn't proceed servers response",
            messageUnsupportedResponse = "Server sent unsupported response";

    public interface CallbackResponse {
        void response(int pageCode,String response,FragmentActivity fragmentActivity, Fragment fragment) throws Exception;
    }
    private interface OtherCallbacks {
        public void onCanceled();
        public void onDefaultResponse(int pageCode, String response, FragmentActivity fragmentActivity, Fragment fragment) throws Exception;
        public void noInternet(FragmentActivity fragmentActivity, Fragment fragment);
    }
    public static abstract class OtherOptionalCallback implements OtherCallbacks{
        @Override
        public void onCanceled() {
            throw new UnsupportedOperationException();
        }
        @Override
        public void onDefaultResponse(int pageCode, String response, FragmentActivity fragmentActivity, Fragment fragment) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void noInternet(FragmentActivity fragmentActivity, Fragment fragment) {
            throw new UnsupportedOperationException();
        }
    }

    public static boolean debug = true;
    private int statusCodeResponse = NO_INTERNET;
    private String stringResponse;

    private SparseArrayCompat<CallbackResponse> listeners;
    private OtherOptionalCallback otherOptionalCallbacks = null;
    private LoaderListener<Boolean> usersLoaderListener = null;

    private LazyRequest request;

    public RightBaseLazyLoader(FragmentActivity fragmentActivity, int loaderId) {
        super(fragmentActivity, loaderId);
        this.listeners = new SparseArrayCompat<>();
        super.setLoaderListener(this);
    }

    public RightBaseLazyLoader setRequest(LazyRequest request){
        this.request = request;
        return this;
    }

    public RightBaseLazyLoader setResponseListener(int page, CallbackResponse listener){
        listeners.put(page, listener);
        return this;
    }

    public RightBaseLazyLoader setOtherListener(OtherOptionalCallback otherOptionalCallbacks){
        this.otherOptionalCallbacks = otherOptionalCallbacks;
        return this;
    }

    private HttpResponse buildResponse(LazyRequest request) throws Exception{
        BasicRightRequest brr = new BasicRightRequest();

        Header header = request.getHeader();
        String json = request.getPostJson();
        String url = request.getUrl();

        if(json != null){ // POST JSON
            log("POST JSON:" + json);
            return (header == null)?
                    brr.postHttpResponse(url,json)
                    :
                    brr.postHttpResponse(url,header,json);

        }else if(request instanceof AddMultipartEntityBuilderToLazyRequest) { // POST FORM
            MultipartEntityBuilder builder = ((AddMultipartEntityBuilderToLazyRequest) request).getBuilder();
            HttpEntity entity = builder.build();
            log("POST FORM:" + toString(entity));
            return (header == null)?
                    brr.postHttpResponse(url, builder.build())
                    :
                    brr.postHttpResponse(url, header, entity);
        }

        log("GET");
        return (header == null)?
                brr.getHttpResponse(url)
                :
                brr.getHttpResponse(url,header);
    }

    private static String toString(HttpEntity entity) throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream((int)entity.getContentLength());
        entity.writeTo(out);
        return new String(out.toByteArray());
    }

    @Override
    public Boolean loadInBackground(){
        try {
            HttpResponse response = request.getCustomResponse() != null
                    ?
                    request.getCustomResponse()
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
            if (otherOptionalCallbacks != null) {
                try {
                    otherOptionalCallbacks.noInternet(fragmentActivity,fragment);
                    return;
                }catch (UnsupportedOperationException e){
                    log(".noInternet(fragmentActivity,fragment) is not override");
                }
            }
            Toast.makeText(getContext(), messageNoInternet, Toast.LENGTH_LONG).show();
            return;
        }

        // STATUS CODE LISTENER
        try {
            if (listeners.get(statusCodeResponse) != null) {
                listeners.get(statusCodeResponse).response(statusCodeResponse, stringResponse, fragmentActivity, fragment);
            } else {
                if (otherOptionalCallbacks != null) {
                    try {
                        otherOptionalCallbacks.onDefaultResponse(statusCodeResponse, stringResponse, fragmentActivity, fragment);
                        return;
                    }catch (UnsupportedOperationException e){
                        log(".onDefaultResponse() is not override");
                    }
                }
                Toast.makeText(getContext(), messageUnsupportedResponse + " (" + statusCodeResponse + ")", Toast.LENGTH_LONG).show();
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), messageErrorProceedResponse + " (" + statusCodeResponse + ")", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCancelLoad() {
        cancelLoad();
        if(otherOptionalCallbacks != null){
            try {
                otherOptionalCallbacks.onCanceled();
            }catch (UnsupportedOperationException e){
                log(".onCanceled() is not override");
            }
        }
    }

    private void log(String log){
        if(debug) {
            Log.i(TAG + " " + request.getClass().getSimpleName(), log);
        }
    }
}