package com.cafe.demo.library.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by WangKun on 2016/11/10.
 * Time : 2016/11/10,15:57.
 */

public abstract class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private ArrayList<String> datas;
    private int layoutId;
    private final LayoutInflater inflater;

    public MyRecyclerAdapter(Context context, ArrayList<String> datas, int layoutId) {
        this.datas = datas;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        bindVH(holder, position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public abstract void findViews(SparseArray<View> viewArry);

    public abstract void bindVH(MyViewHolder holder, int position);


    class MyViewHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> viewArray;

        public MyViewHolder(View itemView) {

            super(itemView);
            viewArray = new SparseArray<>();

            findViews(viewArray);
        }
    }
}
