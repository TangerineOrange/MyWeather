package com.cafe.project.myweather.base;

import android.support.annotation.CallSuper;
import android.view.View;

import com.cafe.demo.library.refresh.IRefreshView;
import com.cafe.demo.library.refresh.OnRefreshListener;
import com.google.gson.Gson;


/**
 * Created by cafe on 2017/5/11.
 */

public abstract class BaseRefreshFragment extends BaseFragment {
    protected IRefreshView iRefreshView;

    public abstract IRefreshView getRefreshView(View view);

    public abstract void downloadData();

    public Gson gson;


    @CallSuper
    @Override
    public void initView(View view) {
        gson = new Gson();
        iRefreshView = getRefreshView(view);
        if (iRefreshView != null) {
            iRefreshView.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    downloadData();
                }
            });
        }
    }
}
