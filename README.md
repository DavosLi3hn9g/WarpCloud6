# HarmonyHttpClient

#### Description
鸿蒙上使用的Http网络框架，里面包含纯Java实现的HttpNet，类似okhttp使用，支持同步和异步两种请求方式；还有鸿蒙版retrofit，和Android版Retrofit相似的使用，解放双手搬优雅使用注解、自动解析json


#### 很遗憾，目前没能直接发布bintray，DevEco Studio上传bintray，gradle安装不通过，所以如果要使用，clone下拉，引入module即可

#### HttpNet使用方式

## 构建GET请求:和okhttp类似

```java
RequestParams params = new RequestParams()
                .put("userName","oscer")
                .put("pwd","oschina");

Request request = new Request.Builder().encode("UTF-8")
                .method("GET")
                .timeout(13000)
                .url("http://www.oschina.net")
                .build();
```

## 构建POST请求:
```java

//请求参数
RequestParams params = new RequestParams()
                .put("userName","oscer")
                .putFile("fileName","file")
                .put("pwd","oschina");
//请求对象
Request request = new Request.Builder()
                .encode("UTF-8")
                .method("POST")
                .params(params)
                .timeout(13000)
                .url("http://www.oschina.net")
                .build();
```

## POST JSON 请求构建:

```java
Request request = new Request.Builder()
                .encode("UTF-8")
                .method("POST")
                .content(new JsonContent("json")
                .timeout(13000)
                .url("http://www.oschina.net")
                .build();

```

##执行请求:
```java

HttpNetClient client = new HttpNetClient();
client.setProxy("192.168.1.1",80);//您也可以开启该客户端全局代理

//执行异步请求
client.newCall(request)
                //如果采用上传文件方式，可以在这里开启上传进度监控
                .intercept(new InterceptListener() {
                    @Override
                    public void onProgress(final int index, final long currentLength, final long totalLength) {
                        Log.e("当前进度", "  --  " + ((float) currentLength / totalLength) * 100);
                    }
                })
                .execute(new Callback() {
                    @Override
                    public void onResponse(Response response) {
                        String body = response.getBody();
                        InputStream is = response.toStream();//如果采用下载，可以在这里监听下载进度
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("onFailure", " onFailure " + e.getMessage());
                    }
                });

// 也可以在线程中执行同步请求
try {
     Response response = client.newCall(request).execute();
     String body = response.getBody();
}catch (Exception e){
      e.printStackTrace();
}

```

#### Rerofit使用方式，网络实现基于前面的 HttpNetClient

```java

// 构建请求java接口，采用动态代理+注解实现
public interface LoginService {

    //普通POST
    @Headers({"Cookie:cid=adcdefg;"})
    @POST("api/users/login")
    Call<BaseModel<User>> login(@Form("email") String email,
                                @Form("pwd") String pwd,
                                @Form("versionNum") int versionNum,
                                @Form("dataFrom") int dataFrom);

    // 上传文件
    @POST("action/apiv2/user_edit_portrait")
    @Headers("Cookie:xxx=hbbb;")
    Call<String> postAvatar(@File("portrait") String file);


    //JSON POST
    @POST("action/apiv2/user_edit_portrait")
    @Headers("Cookie:xxx=hbbb;")
    Call<String> postJson(@Json String file);

    //PATCH
    @PATCH("mobile/user/{uid}/online")
    Call<ResultBean<String>> handUp(@Path("uid") long uid);
}
```

##执行请求
```java

public static final String API = "http://www.oschina.net/";
public static Retrofit retrofit = new Retrofit();

static {
    retrofit.registerApi(API);//注册api
}

//执行异步请求
retrofit.from(LoginService.class)
         .login("xxx@qq.com", "123456", 2, 2);
         .withHeaders(Headers...)
         .execute(new CallBack<BaseModel<User>>() {
                 @Override
                 public void onResponse(Response<BaseModel<User>> response) {

                 }

                 @Override
                 public void onFailure(Exception e) {

                 }
 });

//当然也支持同步请求

Response<BaseModel<User>> response = retrofit.from(LoginService.class)
         .login("xxx@qq.com", "123456", 2, 2);
         .withHeaders(Headers...)
         .execute();
```