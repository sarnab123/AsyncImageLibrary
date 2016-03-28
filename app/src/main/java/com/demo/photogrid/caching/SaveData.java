package com.demo.photogrid.caching;

import android.util.LruCache;

import com.demo.photogrid.network.IResponse;

/**
 * Created by global on 3/13/16.
 */
public class SaveData extends LruCache<String,IResponse>{


    private static SaveData instance;

    private SaveData()
    {
        super(getLRUSize());
    }

    private static int getLRUSize() {

        return 300;
    }

    public static SaveData getInstance() {
        if(instance == null)
        {
            instance = new SaveData();
        }
        return instance;
    }


}
