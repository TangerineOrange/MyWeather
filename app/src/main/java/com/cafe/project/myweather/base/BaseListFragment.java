package com.cafe.project.myweather.base;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cafe.demo.library.refresh.IRecyclerView;
import com.cafe.demo.library.refresh.OnRefreshListener;


/**
 * Created by cafe on 2017/5/1.
 */

/**
 * 1、 initialize the RecyclerView
 * 2、 set Adapter and Manager
 * 3、 download net Data.
 */
public abstract class BaseListFragment extends BaseFragment {

    protected IRecyclerView irecyclerView;


    public abstract IRecyclerView getRefreshView(View view);

    public abstract void downloadData();

    public abstract RecyclerView.Adapter getAdapter();

    public abstract RecyclerView.LayoutManager getLayoutManager();

    @CallSuper
    @Override
    public void initView(View view) {
        irecyclerView = getRefreshView(view);
        if (irecyclerView != null) {
            irecyclerView.init(getAdapter(), getLayoutManager());
            irecyclerView.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    downloadData();
                }
            });
        }
    }
}
