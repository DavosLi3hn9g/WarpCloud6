package com.haibin.retrofit.factory;

import com.haibin.retrofit.Response;
import com.haibin.retrofit.Retrofit;

import java.lang.reflect.ParameterizedType;

/**
 * 响应转化器
 */
public interface ConverterFactory {
    /**
     * 同步转换响应
     *
     * @param response   拦截转换后的数据，例如接口返回来都是加密的，先转json
     */
    public abstract void convert(Response response);
}
