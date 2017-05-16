package com.cafe.project.myweather.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by cafe on 2017/4/26.
 */

public abstract class BaseFragmentActivity extends BaseActivity {


    private static final String LAST_FRAGMENT = "lastFragment";
    private FragmentManager fragmentManager;
    private Fragment lastFragment;


    protected static final int SHOW_BY_PROCESS = 1;
    protected static final int SIDE_BY_SIDE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        Log.i(TAG, "savedInstanceState   " + savedInstanceState);
        if (savedInstanceState == null) {

            Fragment firstFragment = getFirstFragment();
            if (firstFragment != null) {
                switch (getType()) {
                    case SHOW_BY_PROCESS:
                        addFragment(firstFragment);
                        break;
                    case SIDE_BY_SIDE:
                        fragmentManager.beginTransaction().add(getFragmentId(), firstFragment, firstFragment.getClass().getSimpleName()).commit();
                        break;
                }
                lastFragment = firstFragment;
            }
        } else {
            String fragmentName = savedInstanceState.getString(LAST_FRAGMENT);
            lastFragment = fragmentManager.findFragmentByTag(fragmentName);
            Log.i(TAG, "recover last   " + lastFragment);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_FRAGMENT, lastFragment.getClass().getSimpleName());
    }

    //布局中Fragment的ID
    protected abstract int getFragmentId();

    // 获取 第一个要被加载的fragment
    protected abstract Fragment getFirstFragment();

    // 设置fragment 在activity中的展示方式 1、并列式 2、 流程式
    protected abstract int getType();//布局中Fragment的ID


    //add fragment
    public void addFragment(Fragment fragment) {
        Log.i(TAG, "last   " + lastFragment);
        if (fragment != null) {

            switch (getType()) {
                case SHOW_BY_PROCESS:
                    replaceFragment(fragment);
                    break;
                case SIDE_BY_SIDE:
                    showFragment(fragment);
                    break;
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(getFragmentId(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();

    }

    //remove fragment
    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            Toast.makeText(thisActivity, "remove", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }


    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment.isAdded())
            transaction.hide(lastFragment).show(fragment).commit();
        else
            transaction.hide(lastFragment).add(getFragmentId(), fragment, fragment.getClass().getSimpleName()).commit();
        lastFragment = fragment;
    }

    //back button back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && getType() == SHOW_BY_PROCESS) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            removeFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
