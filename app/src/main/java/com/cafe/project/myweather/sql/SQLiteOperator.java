package com.cafe.project.myweather.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by cafe on 2017/5/14.
 */

public class SQLiteOperator {

    private static SQLiteOperator sqLiteOperator;
    private static SQLiteDatabase sqLiteDatabase;
    private SQLiteCreater mSQLiteCreater;

    public static SQLiteOperator getInstance() {
        if (sqLiteOperator == null)
            sqLiteOperator = new SQLiteOperator();
        return sqLiteOperator;
    }

    public void init(SQLiteCreater SQLiteCreater) {
        mSQLiteCreater = SQLiteCreater;
        if (sqLiteDatabase == null) {
            sqLiteDatabase = SQLiteCreater.getReadableDatabase();
        }
    }

    public long insert(ContentValues values) {
        return sqLiteDatabase.insert(mSQLiteCreater.getTableName(), null, values);
    }

    /**
     * @param values    待更新的数据
     * @param where     choose 字段
     * @param whereArgs 该字段等于多少
     *                  如果没有后两个参数 则全部都更新为values
     *                  <p>
     *                  example:db.update(mTUserPhoto, cv, "photoId=?","1","2");
     */
    public int update(ContentValues values, String where, String... whereArgs) {
        return sqLiteDatabase.update(mSQLiteCreater.getTableName(), values, where, whereArgs);
    }


    public Cursor query(String[] columns, String selection,
                        String... selectionArgs) {
        return sqLiteDatabase.query(mSQLiteCreater.getTableName(), columns, selection, selectionArgs, null, null, null);
    }

    public int delete(String whereClause, String... whereArgs) {
       return sqLiteDatabase.delete(mSQLiteCreater.getTableName(), whereClause, whereArgs);
    }

}
