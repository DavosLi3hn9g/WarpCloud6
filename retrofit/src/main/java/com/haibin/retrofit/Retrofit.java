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
package com.haibin.retrofit;


import com.haibin.httpnet.HttpNetClient;
import com.haibin.httpnet.Interceptor;
import com.haibin.retrofit.factory.ConverterFactory;
import com.haibin.retrofit.factory.MainThreadExecutor;
import com.haibin.retrofit.factory.MethodFactory;
import com.haibin.retrofit.net.Convert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态代理对象
 */
@SuppressWarnings("ALL")
public class Retrofit {
    private HttpNetClient mClient;
    private final MainThreadExecutor mMainExecutor;
    private ConverterFactory mConverterFactory ;
    private String mBaseUrl;

    public MainThreadExecutor getMainExecutor() {
        return mMainExecutor;
    }

    public Retrofit() {
        mClient = new HttpNetClient();
        mMainExecutor = new MainThreadExecutor();
    }

    public ConverterFactory converterFactory() {
        return mConverterFactory;
    }

    /**
     * 添加响应转换器
     * @param convert convert
     */
    public void setConverterFactory(ConverterFactory convert) {
        mConverterFactory = convert;
    }

    public void addInterceptor(Interceptor interceptor) {
        mClient.addInterceptor(interceptor);
    }

    public <T> T from(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return new MethodFactory(Retrofit.this).from(method).invoke(args);
            }
        });
    }

    public HttpNetClient getClient() {
        return mClient;
    }

    public void setClient(HttpNetClient mClient) {
        this.mClient = mClient;
    }

    public void registerApi(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }
}
