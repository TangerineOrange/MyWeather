package com.cafe.project.myweather.utils;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;

import com.cafe.project.myweather.adapter.MyListAdapter;

import java.util.ArrayList;

/**
 * Created by cafe on 2017/5/15.
 */

public class PopWindowUtil {

    private static ListPopupWindow listPopupWindow;

    public static void showListPopWindow(Context context, ArrayList<String> data, View view) {
        if (listPopupWindow != null) {
            listPopupWindow.show();
            return;
        }

        listPopupWindow = new ListPopupWindow(context);
        MyListAdapter adapter = new MyListAdapter(context, data);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setAnchorView(view);
        listPopupWindow.setModal(false);
        listPopupWindow.show();
    }

    public static boolean dismissListPopWindow() {
        if (listPopupWindow != null && listPopupWindow.isShowing()) {
            listPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    public static boolean isShowing() {
        return listPopupWindow != null && listPopupWindow.isShowing();
    }

    public static void reset() {
        listPopupWindow = null;
    }
}
