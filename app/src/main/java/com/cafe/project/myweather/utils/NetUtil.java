package com.cafe.project.myweather.utils;

import android.os.Handler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by cafe on 2017/5/11.
 */

public class NetUtil {
    private static String url = "";
    private static NetUtil netUtil;

    private Handler handler = new Handler();

    private NetUtil() {

    }

    private static void initNetUtil() {
        if (netUtil == null) {
            netUtil = new NetUtil();
        }
    }

    private void urlWeather(String param) {
        StringBuilder stringBuffer = new StringBuilder(UrlUtil.WEATHER_URL);
        url = stringBuffer.append(param).append(UrlUtil.KEY_URL).toString();
    }

    private void urlCityId(String param) {
        StringBuilder stringBuffer = new StringBuilder(UrlUtil.CITY_ID_URL);
        url = stringBuffer.append(param).append(UrlUtil.KEY_URL).toString();
    }


    public void execute(final StringCallBack stringCallBack) {
        OkHttpUtil.newCall(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                stringCallBack.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stringCallBack.onResponse(string);
                    }
                });
            }
        });
    }

    public interface StringCallBack {
        void onFailure(IOException e);

        void onResponse(String response);
    }

    public static NetUtil url(String param, int requestType) {
        initNetUtil();
        switch (requestType) {
            case UrlUtil.REQUEST_TYPE_WEATHER:
                netUtil.urlWeather(param);
                break;
            case UrlUtil.REQUEST_TYPE_CITY_INFO:
                netUtil.urlCityId(param);
                break;
        }
        return netUtil;
    }
}
