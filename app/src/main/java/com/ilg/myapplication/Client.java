package com.ilg.myapplication;

import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    public static Client client = null;

    private static TiktokService tiktokService;

    final String baseUrl = "https://www.tiktok.com/";
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    Interceptor cookieInterceptor = new Interceptor();
    public Client(){
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(cookieInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createAsync())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tiktokService = retrofit.create(TiktokService.class);
    }

    public static Client getInstance() {
        if (client == null) client = new Client();
        return client;
    }

    public TiktokService getApi() {
        return tiktokService;
    }
}
