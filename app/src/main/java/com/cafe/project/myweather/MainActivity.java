package com.cafe.project.myweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.cafe.project.myweather.base.BaseFragmentActivity;
import com.cafe.project.myweather.module_city.CityFragment;
import com.cafe.project.myweather.module_setting.SettingFragment;
import com.cafe.project.myweather.module_today.TodayFragment;
import com.cafe.project.myweather.utils.LocationUtil;
import com.cafe.project.myweather.utils.PopWindowUtil;

import java.io.IOException;
import java.util.List;

import static com.cafe.project.myweather.utils.PermissionUtil.PERMISSIONS_ACCESS_FINE_LOCATION;
import static com.cafe.project.myweather.utils.PermissionUtil.PERMISSIONS_INTERNET;

public class MainActivity extends BaseFragmentActivity {

    private TodayFragment todayFragment = new TodayFragment();
    private CityFragment cityFragment = new CityFragment();
    private SettingFragment settingFragment = new SettingFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_today:
                    addFragment(todayFragment);
                    return true;
                case R.id.navigation_city:
                    addFragment(cityFragment);
                    return true;
                case R.id.navigation_setting:
                    addFragment(settingFragment);
                    return true;
            }
            return false;
        }

    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean hasTopView() {
        return false;
    }

    @Override
    public void initView() {
        BottomNavigationView bottomNavigationView = f(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getLocationInfo();

    }

    private void getLocationInfo() {
        Location location = LocationUtil.getLoationInfo(this);
        double latitude = 0;
        double longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i("location", latitude + "    latitude");
            Log.i("location", longitude + "    longitude");
        }
        LocationUtil.setLocationListener(this);
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            Log.i("address", addressList + "   addressList");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null && addressList.size() > 0 && addressList.get(0) != null) {
            LocationUtil.setLocationCityName(addressList.get(0).getLocality());
            LocationUtil.setLocationProvince(addressList.get(0).getAdminArea());
            String addressLine = addressList.get(0).getAddressLine(0);
            String borough = addressLine.substring(addressLine.indexOf("市") + 1, addressLine.indexOf("区") + 1);
            Log.i("address", borough + "   community");
            LocationUtil.setLocationBorough(borough);
            Toolbar toolbar = todayFragment.setToolBarInfo(false, LocationUtil.getLocationCityName());
//            toolbar.setSubtitle(borough);
        }
    }


    @Override
    protected int getFragmentId() {
        return R.id.id_fragment;
    }

    @Override
    protected Fragment getFirstFragment() {
        return todayFragment;
    }

    @Override
    protected int getType() {
        return SIDE_BY_SIDE;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocationInfo();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }


    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT > 22) {
            if (thisActivity.checkSelfPermission(Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                thisActivity.requestPermissions(
                        new String[]{Manifest.permission.INTERNET}, PERMISSIONS_INTERNET
                );
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return PopWindowUtil.dismissListPopWindow() || super.onKeyDown(keyCode, event);
    }


    @Override
    public void finish() {
        PopWindowUtil.dismissListPopWindow();
        PopWindowUtil.reset();
        super.finish();
    }
}
