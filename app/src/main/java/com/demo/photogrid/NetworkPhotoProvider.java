package com.demo.photogrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class NetworkPhotoProvider {
    
    private static final List<Integer> sPhotoResources;
    static {
        List<Integer> photoResources = new ArrayList<Integer>();
        for (int r = R.raw.p001; r<= R.raw.p300; r++) {
            photoResources.add(r);
        }
        sPhotoResources = Collections.unmodifiableList(photoResources);
    }
    
    private final List<PhotoItem> mPhotoItems;
    private final Context mAppContext;
    private final Semaphore mRequestSemaphore = new Semaphore(4, true);
    
    private static final AtomicReference<NetworkPhotoProvider> sInstance = new AtomicReference<NetworkPhotoProvider>();
    
    public static NetworkPhotoProvider getInstance(Context context) {
        synchronized (sInstance) {
            if (sInstance.get() == null) {
                sInstance.set(new NetworkPhotoProvider(context));
            }
            return sInstance.get();
        }
    }
    
    private NetworkPhotoProvider(Context context) {
        mAppContext = context.getApplicationContext();
        mPhotoItems = new ArrayList<NetworkPhotoProvider.PhotoItem>();
        for (int i = 0; i < sPhotoResources.size(); i++) {
            mPhotoItems.add(new PhotoItem(sPhotoResources.get(i), i));
        }
    }
    
    public List<PhotoItem> getAllPhotos() {
        return mPhotoItems;
    }
    
    /**
     * Downloads and returns the bitmap for the given {@link PhotoItem}.
     * This method uses network.
     */
    public Bitmap downloadBitmapForPhoto(PhotoItem item) {
        if (item == null) {
            throw new NullPointerException();
        }
        try {
            mRequestSemaphore.acquire();
            try {
                Thread.sleep(1000);
                Bitmap b = item.getBitmap(mAppContext.getResources());
                Log.d("PhotoProvider", "Loaded item: " + item.mNum);
                return b;
            } catch (InterruptedException e) {
            } finally {
                mRequestSemaphore.release();
            }
        } catch (InterruptedException e) {}
        return null;
    }
    
    public static class PhotoItem {
        
        private final int mRes;
        private final int mNum;
        
        private PhotoItem(int res, int num) {
            mRes = res;
            mNum = num;
        }
        
        private Bitmap getBitmap(Resources resources) {
            return BitmapFactory.decodeStream(resources.openRawResource(mRes));
        }
        
        public String getId() {
            return Integer.toString(mNum);
        }
    }

}
