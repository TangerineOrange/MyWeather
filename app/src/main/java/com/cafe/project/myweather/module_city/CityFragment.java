package com.cafe.project.myweather.module_city;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cafe.demo.library.adapter.SimpleRecyclerAdapter;
import com.cafe.demo.library.adapter.ViewHolder;
import com.cafe.demo.library.refresh.IRecyclerView;
import com.cafe.project.myweather.R;
import com.cafe.project.myweather.base.BaseListFragment;
import com.cafe.project.myweather.utils.LocationUtil;
import com.cafe.project.myweather.utils.PopWindowUtil;
import com.cafe.project.myweather.utils.TextDialog;

import java.util.ArrayList;

/**
 * Created by cafe on 2017/5/10.
 */

public class CityFragment extends BaseListFragment {

    ArrayList<String> cityList = new ArrayList<>();
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_search_btn:
                    if ("".equals(inpSearch.getText().toString())) {
                        TextDialog.showSingleBtnDialog(mContext, "请输入要查找的城市");
                    }
                    break;
                case R.id.id_search:
                    if (!PopWindowUtil.isShowing())
                        PopWindowUtil.showListPopWindow(mContext, cityList, inpSearch);
                    break;
            }
        }
    };
    private EditText inpSearch;

    @Override
    public boolean hasTopView() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_city;
    }

    @Override
    public IRecyclerView getRefreshView(View view) {
        return (IRecyclerView) view.findViewById(R.id.id_city_list);
    }

    @Override
    public void downloadData() {
        String locationCity = LocationUtil.getLocationCityName();
        new Thread(new Runnable() {
            @Override
            public void run() {
                cityList.add("西安");
                cityList.add("运城");
                cityList.add("纽约");
                cityList.add("太原");
                cityList.add("天津");
                cityList.add("北京");
                cityList.add("长沙");
                cityList.add("广州");
                cityList.add("深圳");
                cityList.add("海南");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        irecyclerView.completeRefresh();
                    }
                }, 1000);

            }
        }).start();
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new SimpleRecyclerAdapter<String>(mContext, R.layout.item_recycler_city_fragment, cityList) {
            @Override
            protected void onBind(ViewHolder holder, String cityName, int position) {
                holder.setText(R.id.id_city_name, cityName);
            }
        };
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        inpSearch = (EditText) view.findViewById(R.id.id_search);
        //需要注意的是 setOnEditorActionListener这个方法，
        // 并不是在我们点击EditText的时候触发，也不是在我们对
        // EditText进行编辑时触发，而是在我们编辑完之后点击
        // 软键盘上的回车键才会触发。
        // inpSearch.setOnEditorActionListener();
        inpSearch.setOnClickListener(mListener);
        inpSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("popwindow123", mContext + "   mContext");
                if (hasFocus)
                    PopWindowUtil.showListPopWindow(mContext, cityList, inpSearch);
                else
                    PopWindowUtil.dismissListPopWindow();
            }
        });
        ImageButton btnSearch = (ImageButton) view.findViewById(R.id.id_search_btn);
        btnSearch.setOnClickListener(mListener);
        RecyclerView recyclerView = irecyclerView.getRecyclerView();
//        ((SimpleRecyclerAdapter) recyclerView.getAdapter()).setHeaderView(LayoutInflater.from(mContext).inflate(R.layout.head_view_recycler_city_fragment, irecyclerView.getRecyclerView(), false));
    }


}


