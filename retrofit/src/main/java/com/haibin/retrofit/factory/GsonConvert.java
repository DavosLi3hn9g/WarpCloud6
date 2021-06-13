package com.haibin.retrofit.factory;

import com.google.gson.Gson;
import com.haibin.retrofit.Retrofit;
import com.haibin.retrofit.Response;
import com.haibin.retrofit.call.Callback;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * GSON转换为代理的对象
 */
@SuppressWarnings("all")
public class GsonConvert<T> extends Convert<T> {
    public GsonConvert(boolean isToStream) {
        super(isToStream);
    }

    @Override
    public void convert(com.haibin.httpnet.core.Response response, Retrofit retrofit, final Callback<T> callback, ParameterizedType returnType) {
        final Response res = new Response();
        res.setCode(response.getCode());
        res.setContentLength(response.getContentLength());
        res.setHeaders(response.getHeaders());
        try {
            Type[] types = returnType.getActualTypeArguments();
            if (!isToStream) {
                res.setBodyString(response.getBody());
                res.setBody(new Gson().fromJson(res.getBodyString(), types[0]));
                retrofit.getMainExecutor().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(res);
                    }
                });
            } else {
                res.setInputStream(res.toStream());
                callback.onResponse(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!isToStream)
                retrofit.getMainExecutor().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(res);
                    }
                });
            else
                callback.onResponse(res);
        }
    }

    @Override
    public com.haibin.retrofit.Response<T> convert(com.haibin.httpnet.core.Response response, Retrofit retrofit, ParameterizedType returnType) {
        final Response res = new Response();
        res.setContentLength(response.getContentLength());
        res.setCode(response.getCode());
        res.setHeaders(response.getHeaders());
        try {
            if (!isToStream) {
                res.setBodyString(response.getBody());
                Type[] types = returnType.getActualTypeArguments();
                res.setBody(new Gson().fromJson(res.getBodyString(), types[0]));
            } else {
                res.setInputStream(response.toStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
