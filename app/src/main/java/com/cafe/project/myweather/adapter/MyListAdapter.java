package com.cafe.project.myweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cafe.project.myweather.R;

import java.util.ArrayList;

/**
 * Created by cafe on 2017/5/16.
 */

public class MyListAdapter extends BaseAdapter {

    private ArrayList<String> data;
    private Context context;
    private final LayoutInflater inflater;

    public MyListAdapter(Context context, ArrayList<String> data) {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_list_popwindow_city_search, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.id_history_text);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textView.setText(data.get(position));

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
    }

}
