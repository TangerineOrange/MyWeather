package com.cafe.project.myweather.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cafe.project.myweather.R;

/**
 * Created by cafe on 2017/5/1.
 */

public abstract class BaseFragment extends Fragment {

    protected Activity mContext;

    protected static Handler handler = new Handler();
    protected View rootView;
    protected String Tag = this.getClass().getSimpleName();

    /**
     * 是否使用BaseFragment提供的统一的actionBar
     *
     * @return
     */
    public abstract boolean hasTopView();

    @Override
    public void onAttach(Context context) {
        Log.i(Tag, "  onAttach");
        super.onAttach(context);
        this.mContext = (Activity) context;
        Log.i(Tag, "  mContext" + mContext);
        Log.i(Tag, "  context" + context);
        Log.i(Tag, "  is same" + (context == mContext));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(Tag, "   onCreateView");
        if (!hasTopView()) {
            return inflater.inflate(getLayoutId(), container, false);

        } else {
            View inflate = inflater.inflate(R.layout.fragment_base, container, false);

            inflater.inflate(getLayoutId(), (ViewGroup) inflate);
            return inflate;

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(Tag, "   onViewCreated");

        this.rootView = view;

        //TODO
//        view.setFitsSystemWindows(true);

        initView(view);
    }

    @LayoutRes
    public abstract int getLayoutId();

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Tag, "   onResume");
    }

    public abstract void initView(View view);

    public Toolbar setToolBarInfo(boolean hasBackBtn, String title) {
        if (hasTopView() && rootView != null) {
            Toolbar toolBar = (Toolbar) rootView.findViewById(R.id.toolbar);
            toolBar.setTitle(title);
            if (hasBackBtn)
                toolBar.setNavigationIcon(R.drawable.ic_action_back);
            return toolBar;
        }

        return null;
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        Log.i("fragment111", " onHiddenChanged  " + hidden);
//        if (rootView != null) {
//            if (hidden) {
//                rootView.setFitsSystemWindows(false);
//            } else {
//                rootView.setFitsSystemWindows(true);
//            }
//            rootView.requestApplyInsets();
//        }
//        super.onHiddenChanged(hidden);
//    }

}
