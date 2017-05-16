package com.cafe.project.myweather.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.cafe.project.myweather.sql.SQLiteCreater;
import com.cafe.project.myweather.sql.SQLiteOperator;

/**
 * Created by cafe on 2017/5/15.
 */

public class CityListUtil {

    public static boolean addCity(@NonNull String cityName) {
        Cursor query = SQLiteOperator.getInstance().query(new String[]{SQLiteCreater.COLUMN_ID, SQLiteCreater.COLUMN_CITY_NAME}, null, "");
        while (query.moveToNext()) {
            if (cityName.equals(query.getString(query.getColumnIndex(SQLiteCreater.COLUMN_CITY_NAME)))) {
                return false;
            }
        }
        ContentValues values = new ContentValues();
        values.put(SQLiteCreater.COLUMN_CITY_NAME, cityName);
        values.put(SQLiteCreater.COLUMN_ID, 1 + query.getCount());
        return SQLiteOperator.getInstance().insert(values) != -1;
    }

    public static void removeCity(@NonNull String cityName) {
         SQLiteOperator.getInstance().delete(SQLiteCreater.COLUMN_CITY_NAME, cityName);
    }

}
