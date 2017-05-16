package com.cafe.project.myweather.module_setting;

import android.view.View;

import com.cafe.project.myweather.R;
import com.cafe.project.myweather.base.BaseFragment;

/**
 * Created by cafe on 2017/5/10.
 */

public class SettingFragment extends BaseFragment {
    @Override
    public boolean hasTopView() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initView(View view) {
        setToolBarInfo(false, "Setting");
    }
}
