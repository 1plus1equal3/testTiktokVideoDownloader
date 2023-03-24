package com.ilg.myapplication;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface TiktokService {
    @Headers({
            "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
/*            "charset: utf-8",
            "Content-Type: html/text"*/
    })
    @GET
    Observable<ResponseBody> getURL(@Url String url);

    @Headers({
            "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36",
            "referer: https://www.tiktok.com/"
    })
    @GET
    Observable<ResponseBody> getVideo(@Url String url, @Header("cookie") String cookies);
}


