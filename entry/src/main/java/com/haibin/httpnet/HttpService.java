package com.haibin.httpnet;

import com.haibin.retrofit.call.Call;
import com.haibin.retrofit.net.Form;
import com.haibin.retrofit.net.GET;
import com.haibin.retrofit.net.POST;
import com.haibin.retrofit.net.Proxy;

public interface HttpService {

    //Call内填写返回json对应的格式Java bean实体类
    @Proxy(host = "192.168.1.1", port = 80)
    @POST("http://xxx/api/Users/PostLogin")
    Call<String> login(@Form("email") String email,
                       @Form("pwd") String pwd,
                       @Form("versionNum") int versionNum,
                       @Form("dataFrom") int dataFrom);


    //Call内填写返回json对应的格式Java bean实体类
    @GET("https://www.oschina.net/action/apiv2/tweets")
    Call<String> getTweet(@Form("type") int type,
                          @Form("pageToken") String token);
}
