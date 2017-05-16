package com.cafe.project.myweather.app;

import android.app.Application;

import com.cafe.project.myweather.sql.SQLiteCreater;
import com.cafe.project.myweather.sql.SQLiteOperator;
import com.cafe.project.myweather.utils.OkHttpUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by cafe on 2017/5/12.
 */

public class MyApplication extends Application {

    private final int TIME_OUT = 5000;

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtil.initClient(okHttpClient);

        SQLiteCreater creater = new SQLiteCreater(this);
        SQLiteOperator.getInstance().init(creater);

    }
}
