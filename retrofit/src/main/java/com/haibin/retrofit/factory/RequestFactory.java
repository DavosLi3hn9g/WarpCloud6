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
package com.haibin.retrofit.factory;

import com.haibin.httpnet.builder.Headers;
import com.haibin.httpnet.builder.Request;
import com.haibin.retrofit.Retrofit;
import com.haibin.retrofit.call.AsyncCall;
import com.haibin.retrofit.call.Call;

import java.lang.reflect.Type;

/**
 * 请求工厂，用于将代理转换为真正的请求
 *
 * @param <T>
 */
class RequestFactory<T> {
    private Type mReturnType;
    private Request.Builder mBuilder;
    private boolean isToStream;

    RequestFactory(Request.Builder builder, Type returnType, boolean isToStream) {
        this.mReturnType = returnType;
        this.mBuilder = builder;
        this.isToStream = isToStream;
    }

    /**
     * 转换为真正的请求
     *
     * @param retrofit 代理
     * @return 返回对象
     */
    Call<T> convert(Retrofit retrofit, Headers.Builder headers) {
        return new AsyncCall<>(retrofit, mBuilder, headers, mReturnType, isToStream);
    }
}
