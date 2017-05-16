package com.cafe.demo.library.adapter;

/**
 * Created by WangKun on 2016/11/15.
 * Time : 2016/11/15,12:19.
 */

public interface MultiItemTypeSupport<T> {
    //根据item类型获取不同的layoutId
    int getLayoutId(int itemType);

    //根据postion 和 t 获取不同的item类型
    int getItemViewType(int position, T t);
}
