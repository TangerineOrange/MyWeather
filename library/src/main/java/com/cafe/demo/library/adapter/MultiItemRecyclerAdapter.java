package com.cafe.demo.library.adapter;


import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by WangKun on 2016/11/15.
 * Time : 2016/11/15,12:22.
 */

public abstract class MultiItemRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {


    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public MultiItemRecyclerAdapter(Context context, MultiItemTypeSupport<T> multiItemTypeSupport,List<T> data) {
        super(context, -1,data);
        mMultiItemTypeSupport = multiItemTypeSupport;
    }


    @Override
    public int getType(int position) {
        return mMultiItemTypeSupport.getItemViewType(position, mData.get(position));
    }


    @Override
    protected ViewHolder onCreate(Context context, ViewGroup parent, int mLayoutId, int mViewType) {
        int layoutId = mMultiItemTypeSupport.getLayoutId(mViewType);
        return ViewHolder.get(mContext, parent, layoutId, mViewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        onBind(holder, mData.get(position), position);
    }
}
