package com.demo.photogrid.customUI;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.demo.photogrid.NetworkPhotoProvider;
import com.demo.photogrid.network.AbstractRequest;
import com.demo.photogrid.network.IResponse;
import com.demo.photogrid.network.RequestHandler;
import com.demo.photogrid.network.RequestListener;
import com.demo.photogrid.network.RequestPolicy;
import com.demo.photogrid.network.networkModel.BitmapModel;
import com.demo.photogrid.network.requestImpl.BitmapNetworkRequest;

import java.util.HashSet;

/**
 * Created by global on 3/24/16.
 */
public class CustomImageView extends ImageView
{

    private BitmapNetworkRequest networkRequest;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private String uniqueID = null;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setNetworkURL(String url)
    {
        this.uniqueID = url;
        networkRequest = new BitmapNetworkRequest(new ImageRequestListener(),190,150);
        networkRequest.setServerUrl(url);
        networkRequest.setRequestType(AbstractRequest.GET);

        RequestPolicy requestPolicy = new RequestPolicy();
        requestPolicy.setRetryCount(2);
        HashSet<String> errorCodes = new HashSet<>();
        errorCodes.add("2002");
        requestPolicy.addHttpErrorCodes(errorCodes);
        networkRequest.setRequestPolicy(requestPolicy);

        RequestHandler.getInstance().putRequest(networkRequest);
    }

    public void setPhotoItem(NetworkPhotoProvider.PhotoItem photoItem)
    {
        if(photoItem.getId().equalsIgnoreCase("0"))
        {
            setNetworkURL("https://pbs.twimg.com/profile_images/488102557282095104/1T5rPh7g_400x400.jpeg");
        }
        else {
            this.uniqueID = photoItem.getId();
            networkRequest = new BitmapNetworkRequest(new ImageRequestListener(), getHeight(), getWidth(), true, photoItem, getContext());
            RequestHandler.getInstance().putRequest(networkRequest);
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if(networkRequest != null)
        {
            networkRequest.cancelRequest();
        }
    }


    class ImageRequestListener implements RequestListener{

        @Override
        public void onSuccess(final IResponse dataResponse, boolean fromCache) {
            final BitmapModel bitmapModel = (BitmapModel) dataResponse;

            if(networkRequest != null && networkRequest.isRequestCancelled())
            {
                return;
            }
            if(uniqueID != null && uniqueID.equalsIgnoreCase(bitmapModel.getUniqueID())) {
                if (fromCache) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            onSuccess(dataResponse, false);
                        }
                    });
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setImageBitmap(bitmapModel.getBitmap());
                        }
                    });
                }
            }
            networkRequest = null;
        }

        @Override
        public void onFailure(Error err) {
            networkRequest = null;
        }

        @Override
        public Error onPayloadError(IResponse dataResponse) {
            if(dataResponse == null)
            {
                Error error = new Error("No data");
                return error;
            }
            BitmapModel bitmapModel = (BitmapModel)dataResponse;
            if(bitmapModel.getErrorMessage() != null)
            {
                Error  error = new Error(bitmapModel.getErrorMessage());
                return error;
            }
            return null;
        }

        @Override
        public void onCancelled() {
            networkRequest = null;
        }
    }
}
