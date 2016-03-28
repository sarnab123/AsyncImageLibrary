package com.demo.photogrid.network;

import com.demo.photogrid.caching.SaveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by global on 3/26/16.
 */
public class RequestHandler {

    private int maxThreadPoolSize = 16;

    private ExecutorService executorService = null;

    private static RequestHandler instance = null;


    private RequestHandler()
    {
        executorService = Executors.newFixedThreadPool(maxThreadPoolSize);
    }

    public static RequestHandler getInstance()
    {
        if(instance == null)
        {
            instance = new RequestHandler();
        }
        return instance;
    }

    public void shutDown()
    {
        if(executorService != null)
        {
            executorService.shutdown();
        }
    }

    public void putRequest(AbstractRequest request)
    {
        IResponse cacheResponse = SaveData.getInstance().get(request.getCacheKey());
        if(cacheResponse != null)
        {
                request.getRequestListener().onSuccess(cacheResponse,true);
        }
        else {
            if(!request.isSimulated()) {
                checkRequest(request);
            }
            executorService.execute(new RequestRunnable(request));
        }
    }

    private void checkRequest(AbstractRequest request)
    {
        if(request == null)
            throw new IllegalStateException("Null Request");

        if(request.getRequestType() == null)
            throw new IllegalStateException("Request Type missing");

        if(request.getServerURL() == null)
            throw new IllegalStateException("Basic Http Url missing");

    }

}
