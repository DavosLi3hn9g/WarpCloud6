package com.haibin.httpnet.slice;

import com.haibin.httpnet.HttpNetClient;
import com.haibin.httpnet.HttpService;
import com.haibin.httpnet.Log;
import com.haibin.httpnet.ResourceTable;
import com.haibin.httpnet.builder.Request;
import com.haibin.httpnet.core.Response;
import com.haibin.httpnet.core.call.Callback;
import com.haibin.retrofit.Retrofit;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);


        Button button = (Button) findComponentById(ResourceTable.Id_btn_http);
        button.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                retrofitDemo();
            }
        });
    }

    private void onHttoDemo(){
        Request request = new Request.Builder().encode("UTF-8")
                .method("GET")
                .timeout(13000)
                .url("https://www.oschina.net")
                .build();

        HttpNetClient client = new HttpNetClient();

        //异步请求
        client.newCall(request)
                //如果采用上传文件方式，可以在这里开启上传进度监控
                .intercept((index, currentLength, totalLength) -> {

                })
                .execute(new Callback() {
                    @Override
                    public void onResponse(Response response) {
                        String body = response.getBody();
                        //InputStream is = response.toStream();//如果采用下载，可以在这里监听下载进度
                        Log.e("onResponse" + body);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("onFailure " + e.getMessage());
                    }
                });
        //同步等待执行
        try {
            Response response = client.newCall(request).execute();
            String body = response.getBody();
            Log.e("onResponse" + body);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void retrofitDemo(){
        Retrofit retrofit = new Retrofit();
        retrofit.from(HttpService.class)
                .getTweet(1,"")
                .execute(new com.haibin.retrofit.call.Callback<String>() {
                    @Override
                    public void onResponse(com.haibin.retrofit.Response<String> response) {
                        Log.e(Thread.currentThread().getName()+ "  -- onResponse" + response.getBodyString());
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
