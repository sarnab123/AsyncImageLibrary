package com.demo.photogrid.utility;

import com.demo.photogrid.network.IResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by global on 3/27/16.
 */
public class CommonUtility {

    public static IResponse getModelFromJson(String json,Class modelClass)
    {
        Gson gson = new GsonBuilder().create();

        return (IResponse) gson.fromJson(json, modelClass);
    }

}
