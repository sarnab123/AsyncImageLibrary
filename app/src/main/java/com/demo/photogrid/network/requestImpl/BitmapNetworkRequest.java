package com.demo.photogrid.network.requestImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.demo.photogrid.NetworkPhotoProvider;
import com.demo.photogrid.caching.SaveData;
import com.demo.photogrid.network.AbstractRequest;
import com.demo.photogrid.network.IResponse;
import com.demo.photogrid.network.RequestListener;
import com.demo.photogrid.network.networkModel.BitmapModel;

import java.io.InputStream;

/**
 * Created by global on 3/27/16.
 */
public class BitmapNetworkRequest extends AbstractRequest
{
    private int desiredHeight,desiredWidth;

    private NetworkPhotoProvider.PhotoItem photoItem;

    private Context mContext;

    private int inSampleSize = -1;

    public BitmapNetworkRequest(RequestListener responseListener,int desiredHeight,int desiredWidth)
    {
        super(responseListener);
        this.desiredHeight = desiredHeight;
        this.desiredWidth = desiredWidth;
    }


    public BitmapNetworkRequest(RequestListener responseListener,int desiredHeight,int desiredWidth,
                                boolean simulated, NetworkPhotoProvider.PhotoItem photoItem,Context mContext)
    {
        super(responseListener,simulated);
        this.desiredHeight = desiredHeight;
        this.photoItem = photoItem;
        this.mContext = mContext;
        this.desiredWidth = desiredWidth;
    }

    public void setInSampleSize(int inSampleSize)
    {
        this.inSampleSize = inSampleSize;
    }

    @Override
    public String getAbsoluteRequest()
    {
        return getServerURL();
    }

    @Override
    protected boolean isCacheable() {
        return true;
    }

    @Override
    public String getCacheKey() {
        if(isSimulated())
        {
            return photoItem.getId();
        }
        return getServerURL()+desiredWidth+"-"+desiredHeight;
    }

    @Override
    public synchronized IResponse handleResponse(InputStream inputStream)
    {
        handleOnMainThread();

        BitmapModel responseModel = new BitmapModel();

        if(isSimulated())
        {
            Bitmap sampleBitmap = NetworkPhotoProvider.getInstance(mContext).downloadBitmapForPhoto(photoItem);

            responseModel.setUniqueID(photoItem.getId());
            responseModel.setBitmap(sampleBitmap);
            return responseModel;
        }
        else if(inputStream != null)
        {
            responseModel.setUniqueID(getServerURL());
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap sampleBitmap = null;

            if(inSampleSize > 0)
            {
                options.inJustDecodeBounds = false;
                options.inSampleSize = inSampleSize;

                sampleBitmap = BitmapFactory.decodeStream(inputStream,null,options);

                if(sampleBitmap != null) {
                    responseModel.setBitmap(sampleBitmap);

                    return responseModel;
                }
                return null;
            }

            else {

                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = 1;

                sampleBitmap = BitmapFactory.decodeStream(inputStream, null, options);

                int inSampleSize = 1;

                if (options.outWidth > desiredWidth || options.outHeight > desiredHeight) {
                    int halfWidth = options.outWidth / 2;
                    int halfHeight = options.outHeight / 2;

                    while (halfWidth / inSampleSize > desiredWidth && halfHeight / inSampleSize > desiredHeight) {
                        inSampleSize *= 2;

                    }

                }

                if(options.outWidth > 0)
                {
                    setInSampleSize(inSampleSize);
                    responseModel.setErrorMessage("2002");
                    return responseModel;
                }
                return null;
            }
        }
        return null;


    }

    @Override
    protected void cacheAndRespond(IResponse response) {
        SaveData.getInstance().put(getCacheKey(),response);
    }
}
