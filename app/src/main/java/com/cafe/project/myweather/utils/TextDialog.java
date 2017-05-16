package com.cafe.project.myweather.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by cafe on 2017/5/15.
 */

public class TextDialog {

    public static AlertDialog singleBtnDialog;

    public void showDoubleBtnDialog() {

    }

    public static void showSingleBtnDialog(Context context, String message) {
        if (singleBtnDialog == null)
            singleBtnDialog = new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        else
            singleBtnDialog.setMessage(message);
        singleBtnDialog.show();
    }
}
