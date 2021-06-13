/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com>
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haibin.retrofit.call;


import com.haibin.httpnet.builder.Headers;
import com.haibin.httpnet.builder.Request;
import com.haibin.httpnet.core.call.InterceptListener;
import com.haibin.retrofit.Retrofit;
import com.haibin.retrofit.Response;
import com.haibin.retrofit.factory.GsonConvert;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 真正的请求
 *
 * @param <T>
 */
public class AsyncCall<T> implements Call<T> {
    private Retrofit mRetrofit;
    private Request.Builder mBuilder;
    private ParameterizedType mReturnType;
    private Headers.Builder mHeaders;
    private InterceptListener mListener;
    private com.haibin.httpnet.core.call.Call mCall;
    private boolean isToStream;

    public AsyncCall(Retrofit retrofit,
                     Request.Builder builder,
                     Headers.Builder headers,
                     Type mReturnType,
                     boolean isToStream) {
        this.mRetrofit = retrofit;
        this.mBuilder = builder;
        this.mHeaders = headers;
        this.isToStream = isToStream;
        if (!isToStream)
            this.mReturnType = (ParameterizedType) mReturnType;

    }

    @Override
    public Call<T> withHeaders(Headers.Builder headers) {
        if (mHeaders == null) mHeaders = new Headers.Builder();
        Map<String, List<String>> map = headers.build().getHeaders();
        Set<String> set = map.keySet();
        for (String key : set) {
            List<String> values = map.get(key);
            for (String h : values) {
                this.mHeaders.addHeader(key, h);
            }
        }
        mBuilder.headers(mHeaders);
        return this;
    }

    /**
     * 封装了HttpNet，在这里执行请求，并将返回json解析，切换到UI线程
     *
     * @param callback callback
     */
    @Override
    public void execute(final Callback<T> callback) {
        if (mHeaders != null) {
            this.mBuilder.headers(mHeaders);
        }
        mCall = mRetrofit.getClient().newCall(mBuilder.build());
        mCall.intercept(mListener)
                .execute(new com.haibin.httpnet.core.call.Callback() {
                    @Override
                    public void onResponse(com.haibin.httpnet.core.Response response) {
                        new GsonConvert<T>(isToStream).convert(response, mRetrofit, callback, mReturnType);
                    }

                    @Override
                    public void onFailure(final Exception e) {
                        mRetrofit.getMainExecutor().runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(e);
                            }
                        });
                    }
                });
    }

    @Override
    public Call<T> intercept(InterceptListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public Response<T> execute() throws IOException {
        mCall = mRetrofit.getClient().newCall(mBuilder.build());
        mCall.intercept(mListener);
        return new GsonConvert<T>(isToStream)
                .convert(mCall.execute(),
                        mRetrofit, mReturnType);
    }

    @Override
    public void cancel() {
        if (mCall != null)
            mCall.cancel();
    }
}
