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
import com.haibin.httpnet.core.call.InterceptListener;
import com.haibin.retrofit.Response;

import java.io.IOException;

/**
 * 请求接口
 *
 * @param <T>
 */
@SuppressWarnings("unused")
public interface Call<T> {
    /**
     * 添加上传进度监听
     * @param listener 监听器
     * @return 请求
     */
    Call<T> intercept(InterceptListener listener);

    /**
     * 拦截添加Header
     * @param headers 请求头
     * @return 请求
     */
    Call<T> withHeaders(Headers.Builder headers);

    /**
     * 异步执行
     *
     * @param callback 回调
     */
    void execute(Callback<T> callback);

    /**
     * 同步执行，必须在线程中执行
     *
     * @return 请求结果
     * @throws IOException 异常
     */
    Response<T> execute() throws IOException;

    /**
     * 取消网络请求
     */
    void cancel();
}
