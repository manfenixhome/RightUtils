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
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import ch.boye.httpclientandroidlib.util.EntityUtils;

/**
 * Created by victorpaul on 14/08/15.
 */
public class RightLazyLoader extends BaseLoader<Boolean> implements LoaderListener<Boolean> {
    public final static String TAG = RightLazyLoader.class.getSimpleName();

    public interface CallbackResponse {
        void response(int pageCode,String response) throws Exception;
    }
    public interface CallbackCanceled {
        void run();
    }

    public static boolean debug = true;
    private int statusCodeResponse;
    private String stringResponse;
    private SparseArrayCompat<CallbackResponse> listeners;
    private CallbackResponse defaultListener = null;
    private CallbackCanceled cancelListener = null;

    public LazyBaseRequest request;

    public RightLazyLoader(FragmentActivity fragmentActivity, int loaderId) {
        super(fragmentActivity, loaderId);
        this.listeners = new SparseArrayCompat<>();
        setLoaderListener(this);
    }

    private void log(String log){
        if(debug) {
            Log.i(TAG + " " + request.getClass().getSimpleName(), log);
        }
    }

    public RightLazyLoader setRequest(LazyBaseRequest request){
        this.request = request;
        return this;
    }

    public RightLazyLoader setResponseListener(int page, CallbackResponse listener){
        listeners.put(page, listener);
        return this;
    }

    public RightLazyLoader setDefaultResponseListner(CallbackResponse defaultListener) {
        this.defaultListener = defaultListener;
        return this;
    }

    public RightLazyLoader setOnCancelListener(CallbackCanceled cancelListener){
        this.cancelListener = cancelListener;
        return this;
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
            log("FORM:" + entity.toString());

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

    @Override
    public void onLoadFinished(FragmentActivity fragmentActivity, Fragment fragment, Boolean aBoolean, BaseLoader<Boolean> baseLoader) {
        if(listeners.get(statusCodeResponse) != null){
            try {
                listeners.get(statusCodeResponse).response(statusCodeResponse, stringResponse);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error occurred! Server response is invalid.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            if(defaultListener == null) {
                Toast.makeText(getContext(),"Server sent unsupported response (" + statusCodeResponse + ")",Toast.LENGTH_LONG).show();
            }else{
                try {
                    defaultListener.response(statusCodeResponse, stringResponse);
                } catch (Exception e) {
                    e.printStackTrace();
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