# HarmonyHttpClient

### 简介
鸿蒙上使用的Http网络框架，里面包含纯Java实现的HttpNet，类似okhttp使用，支持同步和异步两种请求方式；还有鸿蒙版retrofit，和Android版Retrofit相似的使用，解放双手般优雅使用注解、自动解析json


### 很遗憾，目前没能直接发布bintray，DevEco Studio上传bintray，gradle安装不通过，所以如果要使用，clone下来，引入module即可

## HttpNet基本和进阶使用方式，可以进行合适的封装，简化请求逻辑

#### 构建GET请求:和okhttp类似

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

#### 构建POST请求:
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

#### POST JSON 请求构建:

```java
Request request = new Request.Builder()
                .encode("UTF-8")
                .method("POST")
                .content(new JsonContent("json")
                .timeout(13000)
                .url("http://www.oschina.net")
                .build();

```

#### 执行请求:
```java
//构建Http客户端，这里可以进行全局static final
HttpNetClient client = new HttpNetClient();
client.setProxy("192.168.1.1",80);//您也可以开启该客户端全局代理
client.addInterceptor(new Interceptor() {
            /* 拦截器在执行请求前都会走到这一步，如果是同步的，就是当前线程，如果是异步，就是子线程
            * 因此可以在这里动态添加全局Cookie或其它Header之类的
            * 进阶使用：如果要求对所有接口Form表单进行全局加密，也可以在这里执行
            */
            @Override
            public void intercept(Request request) {
                Log.e("请求拦截器当前线程： " + Thread.currentThread().getName() + "  --  " + request.url());

            }
        });
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
                        String body = response.getBody();//getBody()和toStream()是互斥的
                        InputStream is = response.toStream();//如果采用下载，可以在这里监听下载进度
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("onFailure " + e.getMessage());
                    }
                });

// 也可以在子线程中执行同步请求，如果有几个接口需要进行顺序请求，此方法最佳
try {
     Response response = client.newCall(request).execute();
     String body = response.getBody();
}catch (Exception e){
      e.printStackTrace();
}

```

#### Retrofit使用方式，底层网络实现基于前面的 HttpNetClient，基于运行时注解添加请求配置，UI切换使用鸿蒙EventHandler

```java

// 构建请求java接口，采用动态代理+注解实现，服务器返回什么，Call<服务器返回json对应的Java bean>即可
public interface LoginService {

    //普通POST，方法名添加请求方法注解POST、GET、DELETE、Header等，方法参数添加Form表单注解
    @Headers({"Cookie:cid=adcdefg;"})//静态Header
    @POST("api/users/login")
    Call<BaseModel<User>> login(@Form("email") String email,
                                @Form("pwd") String pwd,
                                @Form("versionNum") int versionNum,
                                @Form("dataFrom") int dataFrom);

    // 上传文件
    @POST("action/apiv2/user_edit_portrait")
    @Headers("Cookie:xxx=hbbb;")//上传文件注解
    Call<String> postAvatar(@File("portrait") String file);


    //JSON POST
    @POST("action/apiv2/user_edit_portrait")
    @Headers("Cookie:xxx=hbbb;")
    Call<String> postJson(@Json String file);//如果是Json POST，这么使用即可

    //PATCH
    @PATCH("mobile/user/{uid}/online")//动态修改url路径
    Call<ResultBean<String>> handUp(@Path("uid") long uid);
}
```

#### 执行请求

```java

public static final String API = "http://www.oschina.net/";
public static Retrofit retrofit = new Retrofit();
retrofit.registerApi(API);//注册api

//进阶使用，假设服务器返回来的json内容是aes加密的，那么可以添加转化器，拦截响应，aes解密后再返回，此方法一定在子线程执行，直接执行耗时操作
retrofit.setConverterFactory(new ConverterFactory() {
            @Override
            public void convert(com.haibin.retrofit.Response response) {
                response.setBodyString("{json}");//拦截响应数据，修改内容，如aes解密后再返回
                Log.e("响应转换器当前线程： " + Thread.currentThread().getName());
            }
        });

//执行异步请求，异步请求可以直接在UI线程执行
retrofit.from(LoginService.class)
         .login("xxx@qq.com", "123456", 2, 2);
         .withHeaders(Headers...)//动态添加某些Header
         .execute(new Callback<BaseModel<User>>() {
                 @Override
                 public void onResponse(Response<BaseModel<User>> response) {
					  //回调是切换在UI线程，可直接更新界面，自动解析body，就是BaseModel<User>，需要判断body为不为null
                 }

                 @Override
                 public void onFailure(Exception e) {

                 }
 });

//当然也支持同步请求，顺序请求N个接口的最佳方法，解决逻辑嵌套，这里只能在子线程执行

Response<BaseModel<User>> response = retrofit.from(LoginService.class)
         .login("xxx@qq.com", "123456", 2, 2);
         .withHeaders(Headers...)
         .execute();

```