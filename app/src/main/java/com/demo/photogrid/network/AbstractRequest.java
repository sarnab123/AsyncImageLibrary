package com.demo.photogrid.network;

import android.os.Looper;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by global on 3/26/16.
 */
public abstract class AbstractRequest {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    private RequestListener requestListener;
    private boolean isSimulated;

    private boolean isCancelled = false;

    private String requestType;

    private int tryCount = 0;

    private Class<? extends IResponse> responseClass;

    private RequestPolicy requestPolicy = new RequestPolicy();

    private HashMap<String,String> requestHeader;

    private HashMap<String,String> urlParameters;

    private String requestBody;
    private String requestURI;
    private String serverURL;

    public AbstractRequest(RequestListener requestListener)
    {
        this.requestListener = requestListener;
    }


    public AbstractRequest(RequestListener requestListener,boolean isSimulated)
    {
        this(requestListener);
        this.isSimulated = isSimulated;
    }

    protected boolean isSimulated()
    {
        return this.isSimulated;
    }

    protected void incrementTryCount()
    {
        this.tryCount++;
    }

    protected int getTryCount()
    {
        return this.tryCount;
    }

    public void cancelRequest()
    {
        isCancelled = true;
    }

    protected RequestListener getRequestListener()
    {
        return this.requestListener;
    }

    public  String getRequestType()
    {
        return this.requestType;
    }

    public Map<String,String> getRequestHeader()
    {
        if(requestHeader != null && requestHeader.size() > 0) {
            return Collections.unmodifiableMap(requestHeader);
        }
        return null;
    }

    public void setRequestHeader(HashMap<String,String> requestHeader)
    {
        this.requestHeader = requestHeader;
    }

    public String getRequestBody()
    {
        return this.requestBody;
    }

    public void setRequestBody(String requestBody)
    {
        this.requestBody = requestBody;
    }

    public String getRequestURI()
    {
        return this.requestURI;
    }

    public String getServerURL()
    {
        return this.serverURL;
    }

    public void setRequestURI(String requestURI)
    {
        this.requestURI = requestURI;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverURL = serverUrl;
    }

    public void setUrlParameters(HashMap<String,String> urlParameters)
    {
        this.urlParameters = urlParameters;
    }

    public Map<String,String> getURLParameters()
    {
        if(urlParameters != null) {
            return Collections.unmodifiableMap(urlParameters);
        }
        return null;
    }

    public String getAbsoluteRequest()
    {
        return getServerURL()+getRequestURI();
    }

    public boolean isRequestCancelled()
    {
        return this.isCancelled;
    }

    protected abstract boolean isCacheable();

    public  RequestPolicy getRequestPolicy()
    {
        return this.requestPolicy;
    }


    public void setRequestPolicy(RequestPolicy requestPolicy)
    {
        this.requestPolicy = requestPolicy;
    }

    public void setRequestType(String requestType)
    {
        this.requestType = requestType;
    }

    public Class<? extends IResponse> getResponseClass(){
        return this.responseClass;
    }

    public abstract String getCacheKey();

    public abstract IResponse handleResponse(InputStream inputStream);

    protected void handleOnMainThread(){
        if(Looper.myLooper() == Looper.getMainLooper())
            throw new IllegalStateException("handle request must not be on UI thread");
    }

    protected void handleCaching(IResponse response)
    {
        if(isCacheable())
        {
            cacheAndRespond(response);
        }
    }

    protected abstract void cacheAndRespond(IResponse response);


}
