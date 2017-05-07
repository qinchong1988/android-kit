android-kit
===========================

## retrofit cache:

design for mobile app. use customize retrofit `CallAdapterFactory`.

```java
public enum CacheMode {
    DEFAULT,                    // default okhttp cache
    NO_CACHE,                   // non't use cache
    REQUEST_FAILED_READ_CACHE,  // first request network, if failed read from cache
    IF_NONE_CACHE_REQUEST,      // first read from cache, if none cache then request network
    FIRST_CACHE_THEN_REQUEST;   // first read from cache, if success callback,then request network if content change refresh cache and invoke call back,so this mode may callback twice.
}
```

cache have two layer Momery&disk.you can customize the cache key by implements `CacheKeyFactory`.

```java
 public interface CacheKeyFactory {
    String createCacheKey(Request request);
 }
```

default key implements is md5("http method "+"url\n"+"headers").

```java
public interface Callback<T> {

    /**
     * Invoked for a received HTTP response.
     * <p>
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call {@link Response#isSuccessful()} to determine if the response indicates success.
     */
    void onResponse(Call<T> call, Response<T> response, boolean fromCache);

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     */
    void onFailure(Call<T> call, Throwable t);
}

public interface CacheCall<T> {

    void enqueue(boolean careCache, Callback<T> callback);

    void enqueue(Callback<T> callback); // careCache=true.
...
}

// Service
public interface APIService {
    @GET("/")
    @Cache(CacheMode.FIRST_CACHE_THEN_REQUEST)
    CacheCall<Home.Resp> home();
}

// Api Manager
public enum API {
    INST;
   public void home(boolean careCache, Callback<Home.Resp> callback) {
          mAPIService.home().enqueue(careCache, callback);
   }
}
```

use `@Cache` active cache mode，then invoke `CacheCall.enqueue(boolean careCache, Callback<T> callback)` or `CacheCall.enqueue(Callback<T> callback)` will care cache, if you don't wan't use cache then you should invoke `CacheCall.enqueue(false, Callback<T> callback)`.this can be useful when load page cache at page1 but not cache at page2.
 
thanks for there awesome project & blog.

- http://dimitrovskif.github.io/SmartCache/
- https://blog.piasy.com/2016/06/25/Understand-Retrofit/
- http://www.jianshu.com/p/45cb536be2f4
- http://www.jianshu.com/p/b58ef6b0624b

 
## Create multi channel apk:

```shell
./build.sh
```

Install the release apk:

```shell
./install.sh
```
