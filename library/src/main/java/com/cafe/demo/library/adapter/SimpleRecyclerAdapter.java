package com.cafe.demo.library.adapter;


import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by cafe on 2017/5/1.
 */

public abstract class SimpleRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {

    public SimpleRecyclerAdapter(Context context, int layoutId, List<T> data) {
        super(context, layoutId,data);
    }

    @Override
    protected int getType(int position) {
        return ITEM_NORMAL;
    }

    @Override
    protected ViewHolder onCreate(Context context, ViewGroup parent, int layoutId, int mViewType) {
        return ViewHolder.get(context, parent, layoutId, mViewType);
    }
}
