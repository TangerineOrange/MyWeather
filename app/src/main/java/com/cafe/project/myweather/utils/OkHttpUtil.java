package com.cafe.project.myweather.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by cafe on 2017/5/12.
 */

public class OkHttpUtil {

    private static OkHttpClient client;

    private OkHttpUtil() {

    }

    public static void initClient(OkHttpClient okHttpClient) {
        if (okHttpClient != null) {
            client = okHttpClient;
        }
    }

    static void newCall(String url, Callback callback) {

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(callback);
    }

}
