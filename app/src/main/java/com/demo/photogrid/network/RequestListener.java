package com.demo.photogrid.network;

/**
 * Created by global on 3/27/16.
 */
public interface RequestListener
{
    public void onSuccess(IResponse dataResponse, boolean fromCache);

    public void onFailure(Error err);

    public Error onPayloadError(IResponse dataResponse);

    public void onCancelled();
}
