package com.demo.photogrid.network;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by global on 3/26/16.
 */
public class RequestPolicy
{
    private int MAX_RETRY_COUNT = 0;

    private long MAX_TIMEOUT = 30000; // milliseconds

    private Set<String> errorCodes = new HashSet<>();

    public void setRetryCount(int retryCount)
    {
        MAX_RETRY_COUNT = retryCount;
    }

    public void setRequestTimeout(long timeout)
    {
        MAX_TIMEOUT  = timeout;
    }

    public int getRetryCount()
    {
        return MAX_RETRY_COUNT;
    }

    public long getTimeOut()
    {
        return MAX_TIMEOUT;
    }

    public void addHttpErrorCodes(HashSet<String> errorCodes)
    {
        this.errorCodes.addAll(errorCodes);
    }

    protected boolean containsErrorCode(String errorCode)
    {
        if(errorCodes != null && errorCodes.size() > 0)
        {
            if(errorCode != null && errorCodes.contains(errorCode))
            {
                return true;
            }
            return false;
        }
        return false;
    }

}
