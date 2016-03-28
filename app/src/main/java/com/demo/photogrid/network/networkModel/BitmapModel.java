package com.demo.photogrid.network.networkModel;

import android.graphics.Bitmap;

import com.demo.photogrid.network.IResponse;

/**
 * Created by global on 3/27/16.
 */
public class BitmapModel implements IResponse
{
    private Bitmap bitmap;

    private String uniqueID;

    private String errorMessage;

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setUniqueID(String uniqueID)
    {
        this.uniqueID = uniqueID;
    }

    public String getUniqueID()
    {
        return this.uniqueID;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap()
    {
        return this.bitmap;
    }
}
