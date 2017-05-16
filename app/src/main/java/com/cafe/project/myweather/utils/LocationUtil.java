package com.cafe.project.myweather.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.cafe.project.myweather.sql.SQLiteCreater;
import com.cafe.project.myweather.sql.SQLiteOperator;

import java.util.List;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.location.LocationManager.PASSIVE_PROVIDER;
import static com.cafe.project.myweather.utils.PermissionUtil.PERMISSIONS_ACCESS_FINE_LOCATION;

/**
 * Created by cafe on 2017/5/11.
 */

public class LocationUtil {

    private static LocationManager locationManager;
    //市
    private static String locationCityName;
    //省
    private static String locationProvince;
    //区
    private static String locationBorough;

    public static String getLocationBorough() {
        return locationBorough;
    }

    public static void setLocationBorough(String locationBorough) {
        LocationUtil.locationBorough = locationBorough;
    }

    private static int locationCityId;

    public static int getLocationCityId(String locationCityName) {

        return locationCityId;
    }

    public static String getLocationProvince() {
        return locationProvince;
    }

    public static void setLocationProvince(String locationProvince) {
        LocationUtil.locationProvince = locationProvince;
    }

    public static String getLocationCityName() {
        return locationCityName;
    }

    public static void setLocationCityName(String locationCityName) {
        LocationUtil.locationCityName = locationCityName;
        ContentValues values = new ContentValues();
        values.put(SQLiteCreater.COLUMN_CITY_NAME, locationCityName);
        values.put(SQLiteCreater.COLUMN_ID, 1);
        int update = SQLiteOperator.getInstance().update(values, SQLiteCreater.COLUMN_ID + "=?", "1");
        Log.i("sqlite123", "update  " + update);
        long insert = -1;
        if (update == 0) {
            insert = SQLiteOperator.getInstance().insert(values);
        }
    }

    private static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.i("location", latitude + "    latitude  locationListener");
                Log.i("location", longitude + "    longitude  locationListener");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public static LocationManager getLocationManager(Activity activity) {
        if (locationManager == null) {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }

    private static String getEnableProviders() {
        if (locationManager == null) {
            return "";
        }
        List<String> providers = locationManager.getProviders(true);
        Log.i("providers", providers + "    providers");

        for (int i = 0; i < providers.size(); i++) {

            if (providers.contains(NETWORK_PROVIDER)) {
                return NETWORK_PROVIDER;
            }
            if (providers.contains(GPS_PROVIDER)) {
                return GPS_PROVIDER;
            }
            if (providers.contains(PASSIVE_PROVIDER)) {
                return PASSIVE_PROVIDER;
            }
        }
        return "";
    }

    public static Location getLoationInfo(Activity activity) {
        locationManager = getLocationManager(activity);
        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("providers", getEnableProviders() + "    providers");
                return locationManager.getLastKnownLocation(getEnableProviders());
            } else {
                activity.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_ACCESS_FINE_LOCATION
                );
                return null;
            }
        } else {
            return locationManager.getLastKnownLocation(getEnableProviders());
        }
    }


    public static void setLocationListener(Activity activity) {
        locationManager = getLocationManager(activity);
        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(getEnableProviders(), 1000, 1000, locationListener);
            } else {
                activity.requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_ACCESS_FINE_LOCATION
                );
            }
        } else {
            locationManager.requestLocationUpdates(getEnableProviders(), 1000, 1000, locationListener);
        }
    }
}
