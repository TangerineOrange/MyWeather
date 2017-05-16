package com.cafe.demo.library.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by WangKun on 2016/11/10.
 * Time : 2016/11/10,16:37.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    //item的类型 --头部
    static final int ITEM_HEADER = 0;
    //item的类型 --普通
    static final int ITEM_NORMAL = 1;
    final List<T> mData;

    private boolean isEnableClick = false;

    Context mContext;
    private int mLayoutId;
    private LayoutInflater mInflater;

    //头部视图
    private View mHeaderView;

    //item 点击监听
    private OnItemClickListener onItemClickListener;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("click", "setOnItemClickListener " + onItemClickListener);
            Log.i("click", "setOnItemClickListener " + v);

            if (v.getTag() != null)
                onItemClickListener.onItemClick(v, (Integer) v.getTag());
            else
                onItemClickListener.onItemClick(v, -1);

        }
    };


    public View getmHeaderView() {
        return mHeaderView;
    }

    public void addDatas(ArrayList<T> datas) {
        mData.addAll(datas);
        notifyDataSetChanged();
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }


    BaseRecyclerAdapter(Context context, int layoutId, List<T> data) {
        this.mLayoutId = layoutId;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mData = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == ITEM_HEADER)
            return ViewHolder.getHeader(mHeaderView);
        return onCreate(mContext, parent, mLayoutId, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0)
            return ITEM_HEADER;
        return getType(position);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (getItemViewType(position) == ITEM_HEADER) return;
        Log.i("bind", "bind " + position);

        final int pos = getRealPosition(holder);
        onBind(holder, mData.get(pos), pos);

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == ITEM_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(holder.getLayoutPosition() == 0);
        }
    }

    protected abstract void onBind(ViewHolder holder, T t, int position);

    protected abstract ViewHolder onCreate(Context context, ViewGroup parent, int layoutId, int mViewType);

    protected abstract int getType(int position);


    @Override
    public int getItemCount() {
        return mHeaderView == null ? mData.size() : mData.size() + 1;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<T> getData() {
        return mData;
    }

    public void replaceData(List<T> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }


}
