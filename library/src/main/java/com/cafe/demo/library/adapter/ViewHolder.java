package com.cafe.demo.library.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by WangKun on 2016/11/13.
 * Time : 2016/11/13,15:05.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViewArray;
    private int mViewType;

    /**
     * 初始化ViewHolder
     *
     * @param itemView  item的view
     * @param mViewType view 类型
     */
    public ViewHolder(View itemView, int mViewType) {
        super(itemView);
        mViewArray = new SparseArray<>();
        this.mViewType = mViewType;
    }

    public ViewHolder(View itemView) {
        super(itemView);
        mViewArray = new SparseArray<>();
    }


    public void setItemOnClickListener(View.OnClickListener listener) {
        itemView.setTag(getLayoutPosition());
        itemView.setOnClickListener(listener);
    }


    /**
     * 创建viewholder
     *
     * @return
     */
    public static ViewHolder getHeader(View header) {
        return new ViewHolder(header);
    }

    /**
     * 创建viewholder
     *
     * @param context
     * @param parent
     * @param layoutId
     * @return
     */
    public static ViewHolder get(Context context, ViewGroup parent, int layoutId, int mViewType) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);

        return new ViewHolder(itemView, mViewType);
    }

    public int getviewType() {
        return mViewType;
    }


    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViewArray.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViewArray.put(viewId, view);
        }
        return (T) view;
    }

    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        if (view != null)
            view.setText(text);
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        if (view != null)
            view.setImageResource(resId);
        return this;
    }

    public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null)
            view.setOnClickListener(listener);
        return this;
    }
}
