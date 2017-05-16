package com.cafe.project.myweather.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cafe.project.myweather.R;

/**
 *
 */
public abstract class BaseActivity extends AppCompatActivity {

    public BaseActivity thisActivity;
    public LayoutInflater inflater;
    public String TAG = this.getClass().getSimpleName();

    public View.OnClickListener listener;
    private ViewGroup rootView;

    @LayoutRes
    public abstract int getLayoutId();

    /**
     * 是否使用baseActivity提供的统一的actionBar
     *
     * @return
     */
    public abstract boolean hasTopView();

    public abstract void initView();

    @SuppressWarnings("unchecked")
    public <T extends View> T f(@IdRes int resId) {
        return (T) super.findViewById(resId);
    }

    public void setOnClick(View... views) {
        for (View view :
                views) {
            if (listener == null) {
                throw new RuntimeException("点击监听器为空");
            } else
                view.setOnClickListener(listener);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisActivity = this;
        super.onCreate(savedInstanceState);

        if (!hasTopView()) {
            setContentView(getLayoutId());

        } else {
            setContentView(R.layout.activity_app_base);

            inflater = getLayoutInflater();
            ViewGroup childAt = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            rootView = (ViewGroup) inflater.inflate(getLayoutId(), childAt, false);

            childAt.addView(rootView);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
        initView();
    }

    /**
     * Add a normal ActionBar
     *
     * @param hasBackBtn is there has a back button.
     * @param title      the title of ActionBar
     */
    public Toolbar addOriginalActionBar(boolean hasBackBtn, String title) {

        if (!hasTopView())
            return null;

        Toolbar toolbar = f(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        if (hasBackBtn) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
