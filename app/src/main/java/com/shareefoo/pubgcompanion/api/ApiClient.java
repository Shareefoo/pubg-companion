package com.shareefoo.pubgcompanion.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shareefoo
 */

public class ApiClient {

    public static final String BASE_URL = "https://api.pubg.com/shards/pc-eu/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        //
        if (retrofit == null) {

            // Define the interceptor, add authentication headers
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader("Accept", "application/vnd.api+json");
//                    if (isUserLoggedIn()) {
                    ongoing.addHeader("Authorization", getToken());
//                    }
                    return chain.proceed(ongoing.build());
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(1, TimeUnit.MINUTES)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    private static String getToken() {
        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJkZDM2Y2E5MC02NWJiLTAxMzYtNzU5NS0zNTRhYTk3YWEzOTAiLCJpc3MiOiJnYW1lbG9ja2VyIiwiaWF0IjoxNTMxMTUwNjI4LCJwdWIiOiJibHVlaG9sZSIsInRpdGxlIjoicHViZyIsImFwcCI6InB1YmctY29tcGFuaW9uLTk4MTg1ZmQ5LWE5MzAtNGI1My05OTRmLWE5NGI0OGMxMWI2NCJ9.y3i4h4QIAfSSKXX7vJmnfJRdhTvwUVxwpYepFKk8G-k";
    }

}
