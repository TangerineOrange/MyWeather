package com.cafe.project.myweather.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cafe on 2017/5/14.
 */

public class SQLiteCreater extends SQLiteOpenHelper {

    private Context mContext;
    private static int mVersion = 1;
    private static String mName = "MyWeather_CityList.db";
    private static String tableName = "cityList";

    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_CITY_NAME = "CITY_NAME";
    public static final String COLUMN_CITY_ID = "CITY_ID";
    public static final String COLUMN_LAST_TMP = "LAST_TMP";

    public String getTableName() {
        return tableName;
    }

    public SQLiteCreater(Context context) {
        super(context, mName, null, mVersion);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + tableName + " (" +
                COLUMN_ID + "         int  PRIMARY KEY  NOT NULL," +
                COLUMN_CITY_NAME + "  text NOT NULL," +
                COLUMN_CITY_ID + "    int," +
                COLUMN_LAST_TMP + "   int);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
