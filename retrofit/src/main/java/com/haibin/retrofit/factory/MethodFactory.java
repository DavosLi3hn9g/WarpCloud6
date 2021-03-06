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


import com.haibin.httpnet.builder.Request;
import com.haibin.httpnet.builder.RequestParams;
import com.haibin.httpnet.core.io.JsonContent;
import com.haibin.retrofit.Retrofit;
import com.haibin.retrofit.net.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 方法解析类，解析方法返回类型与注解信息
 * parse the method Annotation and return type
 */
public class MethodFactory {
    private Retrofit mRetrofit;
    private Request.Builder mRequest;
    private RequestParams mParams;
    private Method mMethod;
    private Type mReturnType;
    private com.haibin.httpnet.builder.Headers.Builder mHeaders;
    private String mHttpUrl;
    private boolean isToStream;

    public MethodFactory(Retrofit mRetrofit) {
        this.mRetrofit = mRetrofit;
    }

    /**
     * 动态代理转换为真正的执行
     *
     * @param args args
     * @return Call<T>
     */
    public Object invoke(Object... args) {
        parseMethodParamsAnnotation(mMethod.getParameterAnnotations(), args);
        if (mParams != null) {
            mRequest.params(mParams);
        }
        return new RequestFactory(mRequest.url(convertUrl()), mReturnType, isToStream).convert(mRetrofit, mHeaders);
    }

    private String convertUrl() {
        return isEmpty(mRetrofit.getBaseUrl()) ? mHttpUrl : mRetrofit.getBaseUrl() + mHttpUrl;
    }

    private boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

    /**
     * 解析方法
     *
     * @param method method
     * @return MethodFactory
     */
    public MethodFactory from(Method method) {
        this.mMethod = method;
        mReturnType = method.getGenericReturnType();
//        if (mReturnType instanceof Class<?>)
//            return this;
//        if (mReturnType instanceof ParameterizedType) {
//            mRequest = new Request.Builder();
//            parseMethodAnnotation(method.getAnnotations());
//        }
        mRequest = new Request.Builder();
        parseMethodAnnotation(method.getAnnotations());
        return this;
    }

    /**
     * 获取并解析方法注解
     *
     * @param annotations 方法注解
     */
    private void parseMethodAnnotation(Annotation[] annotations) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Stream) {
                    isToStream = true;
                }
                if (annotation instanceof POST) {
                    mRequest.method("POST");
                    mHttpUrl = ((POST) annotation).value();
                } else if (annotation instanceof GET) {
                    mRequest.method("GET");
                    mHttpUrl = ((GET) annotation).value();
                } else if (annotation instanceof PUT) {
                    mRequest.method("PUT");
                    mHttpUrl = ((PUT) annotation).value();
                } else if (annotation instanceof PATCH) {
                    mRequest.method("PATCH");
                    mHttpUrl = ((PATCH) annotation).value();
                } else if (annotation instanceof DELETE) {
                    mRequest.method("DELETE");
                    mHttpUrl = ((DELETE) annotation).value();
                } else if (annotation instanceof Encode) {
                    mRequest.encode(((Encode) annotation).value());
                } else if (annotation instanceof Proxy) {
                    Proxy proxy = (Proxy) annotation;
                    mRequest.proxy(proxy.host(), proxy.port());
                } else if (annotation instanceof Headers) {
                    String[] headers = ((Headers) annotation).value();
                    mHeaders = new com.haibin.httpnet.builder.Headers.Builder();
                    for (String header : headers) {
                        if (!isEmpty(header) && header.contains(":")) {
                            String[] values = header.split(":");
                            mHeaders.addHeader(values[0], values[1]);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取并解析方法参数的注解
     *
     * @param annotations 参数注解
     * @param args        实际参数
     */
    private void parseMethodParamsAnnotation(Annotation[][] annotations, Object[] args) {
        if (annotations != null && annotations.length > 0) {
            mParams = new RequestParams();
            for (int i = 0; i < annotations.length; i++) {
                Annotation annotation = annotations[i][0];
                if (annotation instanceof Form) {
                    Form form = (Form) annotation;
                    mParams.put(form.value(), args[i].toString());
                } else if (annotation instanceof File) {
                    File file = (File) annotation;
                    mParams.putFile(file.value(), args[i].toString());
                } else if (annotation instanceof Json) {
                    mRequest.content(new JsonContent(args[i].toString()));
                    mParams = null;
                } else if (annotation instanceof Path) {
                    Path path = (Path) annotation;
                    String value = path.value();
                    mHttpUrl = mHttpUrl.replace(String.format("{%s}", value), args[i].toString());
                }
            }
        }
    }
}
