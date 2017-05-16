package com.cafe.demo.library;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.cafe.demo.library.refresh.IRefreshView;

import java.lang.reflect.Field;

/**
 * Created by cafe on 2017/5/12.
 */

public class MyRefreshLayout extends SwipeRefreshLayout implements IRefreshView {

    private com.cafe.demo.library.refresh.OnRefreshListener mListener;
    private boolean isAutoRefresh = true;
    private boolean isRefreshEnable = true;

    public MyRefreshLayout(Context context) {
        this(context, null);
    }

    public MyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListener.onRefresh();
            }
        });
    }

    @Override
    public void setOnRefreshListener(com.cafe.demo.library.refresh.OnRefreshListener listener) {
        mListener = listener;
    }

    @Override
    public void setAutoRefresh(boolean isAutoRefresh) {
        this.isAutoRefresh = isAutoRefresh;

    }

    @Override
    public void setRefreshEnable(boolean isRefreshEnable) {
        this.isRefreshEnable = isRefreshEnable;
    }

    @Override
    public void completeRefresh() {
        setRefreshing(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoRefresh();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isRefreshEnable && super.onInterceptTouchEvent(ev);
    }

    /**
     * 设置自动刷新
     * 1、移动到correct位置
     * 2、调用放大动画
     * 3、调用加载动画
     * 4、调用setRefresh(true)方法
     * 5、完成刷新
     */
    private void autoRefresh() {
        if (android.os.Build.VERSION.SDK_INT > 15) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.i("setAutoRefresh", "onGlobalLayout    " + isAutoRefresh);

                    if (!isAutoRefresh) {
                        if (android.os.Build.VERSION.SDK_INT > 15)
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        return;
                    }
                    setRefreshing(true);
                    setNotifyTrue();
                    if (android.os.Build.VERSION.SDK_INT > 15)
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);

                }


            });
        }

    }

    private void setNotifyTrue() {
        try {
            Field shiftingMode =  SwipeRefreshLayout.class.getDeclaredField("mNotify");
            shiftingMode.setAccessible(true);
            Log.i("MyRefreshLayout", "mNotify   " + shiftingMode.getBoolean(this));
            shiftingMode.setBoolean(this, true);

            Log.i("MyRefreshLayout", "mNotify   " + shiftingMode.toString());
            Log.i("MyRefreshLayout", "mNotify   " + shiftingMode.getBoolean(this));

            shiftingMode.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
