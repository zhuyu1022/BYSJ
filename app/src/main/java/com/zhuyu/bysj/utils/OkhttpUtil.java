package com.zhuyu.bysj.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by ZHUYU on 2017/2/11 0011.
 */

public class OkhttpUtil {
    public static void sendOkhttpRequest(final String url, final Callback callback){
                OkHttpClient client=new OkHttpClient();
                final Request request=new Request.Builder()
                        .url(url)
                        .build();

                client.newCall(request).enqueue(callback);
            }
}
