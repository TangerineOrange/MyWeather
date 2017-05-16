package com.cafe.project.myweather.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

/**
 * Created by cafe on 2017/5/11.
 */

public class PermissionUtil {

    private static onRequestPermission mOnRequestPermission;

    public static final int PERMISSIONS_ACCESS_FINE_LOCATION = 110201;
    public static final int PERMISSIONS_INTERNET = 110202;
    public static final int PERMISSIONS_COMMON = 19940130;
    private static int currentApiVersion = android.os.Build.VERSION.SDK_INT;

    public static boolean isGreaterAPI22() {
        return currentApiVersion >= 23;
    }

    public static void requestPermission(Activity activity, String permission, int requestCode, onRequestPermission onRequestPermission) {
        mOnRequestPermission = onRequestPermission;
        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (activity.checkSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED) {
                mOnRequestPermission.permissionGranted();
            } else {
                activity.requestPermissions(
                        new String[]{permission}, requestCode
                );
                mOnRequestPermission.toRequestPermission();

            }
        } else {
            mOnRequestPermission.notGreaterThan22();
        }
    }


    public interface onRequestPermission {
        void permissionGranted();

        void toRequestPermission();

        void permissionDenied();

        void notGreaterThan22();
    }
}
