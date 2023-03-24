package com.ilg.myapplication;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class Interceptor implements okhttp3.Interceptor {

    String cookie = "";
    List<String> cookies;

    String size;

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        cookies = request.headers("set-cookie");
        for(String cookie: cookies){
            this.cookie += cookie;
        }
        Response response = chain.proceed(request);
        cookies = response.headers("set-cookie");
        for(String cookie: cookies){
            this.cookie += cookie + "; ";
        }
        size = response.header("Content-Length");
        return response;
    }

    public String getCookie() {
        return cookie;
    }

    public String getSize() {
        return size;
    }
}
