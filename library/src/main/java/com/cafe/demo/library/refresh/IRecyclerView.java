package com.cafe.demo.library.refresh;

import android.support.v7.widget.RecyclerView;

/**
 * Created by cafe on 2017/5/6.
 */

public interface IRecyclerView extends IRefreshView {
    void init(RecyclerView.Adapter adapter, RecyclerView.LayoutManager manager);

    RecyclerView getRecyclerView();
}
