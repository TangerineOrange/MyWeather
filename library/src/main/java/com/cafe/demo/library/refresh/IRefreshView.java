package com.cafe.demo.library.refresh;

/**
 * Created by cafe on 2017/5/6.
 */

public interface IRefreshView {

    void setOnRefreshListener(OnRefreshListener listener);

    void setAutoRefresh(boolean isAutoRefresh);

    void setRefreshEnable(boolean isRefreshEnable);

    void completeRefresh();

}
