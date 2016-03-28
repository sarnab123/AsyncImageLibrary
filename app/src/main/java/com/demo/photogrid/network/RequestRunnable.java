package com.demo.photogrid.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by global on 3/27/16.
 */
public class RequestRunnable implements Runnable {

    private AbstractRequest request;

    public RequestRunnable(AbstractRequest request)
    {
        this.request = request;
    }
    @Override
    public void run()
    {
        if(!request.isRequestCancelled()) {

            if(request.isSimulated())
            {
                IResponse response = request.handleResponse(null);
                request.handleCaching(response);
                request.getRequestListener().onSuccess(response,false);
            }
            else {
                StringBuilder urlString = new StringBuilder(request.getAbsoluteRequest());
                if (request.getURLParameters() != null) {
                    urlString = appendURLParameters(urlString, request.getURLParameters());
                }

                try {
                    URL connectionURL = new URL(urlString.toString());

                    HttpURLConnection httpURLConnection = (HttpURLConnection) connectionURL.openConnection();

                    httpURLConnection.setRequestMethod(request.getRequestType());

                    httpURLConnection.setReadTimeout((int) request.getRequestPolicy().getTimeOut());

                    httpURLConnection.setConnectTimeout((int) request.getRequestPolicy().getTimeOut());

                    httpURLConnection.setDoInput(true);

                    if (request.getRequestHeader() != null && request.getRequestHeader().size() > 0) {
                        Iterator headerIterator = request.getRequestHeader().keySet().iterator();

                        while (headerIterator.hasNext()) {
                            String key = (String) headerIterator.next();
                            String value = request.getRequestHeader().get(key).toString();
                            httpURLConnection.setRequestProperty(key, value);
                        }
                    }

                    if (request.getRequestType() != "GET") {
                        httpURLConnection.setDoOutput(true);
                    }

                    if (request.getRequestBody() != null) {
                        DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());

                        outputStream.writeBytes(request.getRequestBody());

                        outputStream.flush();

                        outputStream.close();
                    }

                    httpURLConnection.connect();

                    if (httpURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        InputStream inputStream = (InputStream) httpURLConnection.getInputStream();

                        IResponse response = request.handleResponse(inputStream);

                        Error error = request.getRequestListener().onPayloadError(response);

                        if (error == null && response != null) {
                            request.handleCaching(response);

                            request.getRequestListener().onSuccess(response, false);
                        } else if (request.getRequestPolicy().containsErrorCode(error.getMessage()) &&
                                request.getTryCount() < request.getRequestPolicy().getRetryCount()) {
                            // custom retry logic
                            request.incrementTryCount();
                            RequestHandler.getInstance().putRequest(request);

                        } else {
                            request.getRequestListener().onFailure(error);
                        }
                    } else if (request.getRequestPolicy().containsErrorCode(String.valueOf(httpURLConnection.getResponseCode()))
                            &&
                            request.getTryCount() < request.getRequestPolicy().getRetryCount()) {
                        // custom retry logic
                        request.incrementTryCount();
                        RequestHandler.getInstance().putRequest(request);
                    } else {
                        request.getRequestListener().onFailure(null);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            request.getRequestListener().onCancelled();
        }

    }

    private StringBuilder appendURLParameters(StringBuilder urlString, Map<String, String> urlParameters) {

        StringBuilder tempString = new StringBuilder(urlString);

        if(urlParameters != null && urlParameters.size() > 0) {

            tempString.append("?");

            Iterator tempIterator = urlParameters.keySet().iterator();

            boolean first = true;

            while (tempIterator.hasNext()) {
                String key = (String) tempIterator.next();
                String value = urlParameters.get(key).toString();
                try {
                    if(!first)
                    {
                        tempString.append(URLEncoder.encode("&","UTF-8"));
                    }
                    tempString.append(URLEncoder.encode(key,"UTF-8"));
                    tempString.append(URLEncoder.encode("=","UTF-8"));
                    tempString.append(URLEncoder.encode(value,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }
        return tempString;
    }
}
