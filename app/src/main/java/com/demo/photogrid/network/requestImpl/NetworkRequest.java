package com.demo.photogrid.network.requestImpl;

import com.demo.photogrid.network.AbstractRequest;
import com.demo.photogrid.network.IResponse;
import com.demo.photogrid.network.RequestListener;
import com.demo.photogrid.utility.CommonUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by global on 3/26/16.
 */
public class NetworkRequest extends AbstractRequest {

    public NetworkRequest(RequestListener requestListener)
    {
        super(requestListener);
    }

    @Override
    protected boolean isCacheable() {
        return false;
    }

    @Override
    public String getCacheKey() {
        return null;
    }

    @Override
    public IResponse handleResponse(InputStream inputStream) {

        handleOnMainThread();

        StringBuffer response = new StringBuffer();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String input;
        try {
            while((input = bufferedReader.readLine()) != null)
            {
                response.append(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(response.length() > 0)
        {
            IResponse responseObject = CommonUtility.getModelFromJson(response.toString(),getResponseClass());
            return responseObject;
        }

        return null;

    }

    @Override
    protected void cacheAndRespond(IResponse response) {
        return;
    }
}
