package com.haibin.httpnet;

import com.haibin.httpnet.builder.Request;

import java.io.IOException;

/**
 * 请求拦截器
 */
public interface Interceptor {
    void intercept(Request request);
}
