package com.cafe.project.myweather.utils;

/**
 * Created by cafe on 2017/5/12.
 */

public class UrlUtil {

    private static final String ROOT_URL = "https://free-api.heweather.com/v5/";


    static final String WEATHER_URL = ROOT_URL + "weather?city=";
    static final String CITY_ID_URL = ROOT_URL + "search?city=";
    static final String KEY_URL = "&key=188b63cdd65b43728078c3b163e5a38e";


    public static final int REQUEST_TYPE_WEATHER = 0x01;
    public static final int REQUEST_TYPE_CITY_INFO = 0x02;
    public static final int REQUEST_TYPE_CITY_LIST = 0x03;
}
