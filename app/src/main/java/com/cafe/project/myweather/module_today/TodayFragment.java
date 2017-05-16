package com.cafe.project.myweather.module_today;

import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cafe.demo.library.refresh.IRefreshView;
import com.cafe.project.myweather.R;
import com.cafe.project.myweather.base.BaseRefreshFragment;
import com.cafe.project.myweather.bean.HeWeather;
import com.cafe.project.myweather.utils.GsonUtil;
import com.cafe.project.myweather.utils.LocationUtil;
import com.cafe.project.myweather.utils.NetUtil;
import com.cafe.project.myweather.utils.UrlUtil;
import com.cafe.project.myweather.utils.WeatherInfoUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cafe on 2017/5/10.
 */

public class TodayFragment extends BaseRefreshFragment {


    private TextView condTxt;
    private TextView temperatureTxt;
    private TextView airQualityNumTxt;
    private TextView airQualityTxt;
    private TextView firstDay;
    private TextView secondDay;
    private TextView thirdDay;
    private TextView tmp1;
    private TextView tmp2;
    private TextView tmp3;
    private LinearLayout hourlyForecastGroup;
    private TextView nowTmp;
    private LinearLayout airQualityBg;

    @Override
    public boolean hasTopView() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_today;
    }

    @Override
    public IRefreshView getRefreshView(View view) {
        return (IRefreshView) view.findViewById(R.id.id_refresh);
    }

    @Override
    public void downloadData() {
        if (LocationUtil.getLocationCityName() != null && !"".equals(LocationUtil.getLocationCityName()))
            NetUtil.url(LocationUtil.getLocationCityName(), UrlUtil.REQUEST_TYPE_WEATHER).execute(new NetUtil.StringCallBack() {
                @Override
                public void onFailure(IOException e) {

                }

                @Override
                public void onResponse(final String response) {
                    iRefreshView.completeRefresh();
                    Log.i("today", response);
                    resolveJson(response);

                }
            });
    }

    private void resolveJson(final String response) {
        HeWeather weather = (HeWeather) GsonUtil.getInstanceByJson(response, HeWeather.class);
        refreshView(weather);
    }

    private void refreshView(HeWeather weathers) {
        HeWeather.HeWeather5Bean heWeather5Bean = weathers.getHeWeather5().get(0);
        //设置当前天气状况
        HeWeather.HeWeather5Bean.NowBean now = heWeather5Bean.getNow();
        //当前空气质量
        HeWeather.HeWeather5Bean.AqiBean aqi = heWeather5Bean.getAqi();
        //获取日期预告
        List<HeWeather.HeWeather5Bean.DailyForecastBean> dailyForecast = heWeather5Bean.getDaily_forecast();
        //获取小时预告
        List<HeWeather.HeWeather5Bean.HourlyForecastBean> hourlyForecast = heWeather5Bean.getHourly_forecast();

        String du = "°/";
        String du2 = "°";

        if (now != null && now.getCond() != null) {
            condTxt.setText(now.getCond().getTxt());
            temperatureTxt.setText(now.getTmp());
            nowTmp.setText(now.getTmp() + du2);
        }

        TypedArray airQualityColor = getResources().obtainTypedArray(R.array.air_quality_bg);

        if (aqi != null) {
            airQualityNumTxt.setText(aqi.getCity().getAqi());
            String qlty = aqi.getCity().getQlty();
            int i = WeatherInfoUtil.airQuality.indexOf(qlty);
            airQualityTxt.setText("空气质量" + qlty);
            airQualityBg.setBackground(airQualityColor.getDrawable(i));
        }
        airQualityColor.recycle();


        firstDay.setText(dailyForecast.get(0).getDate());
        secondDay.setText(dailyForecast.get(1).getDate());
        thirdDay.setText(dailyForecast.get(2).getDate());
        tmp1.setText(dailyForecast.get(0).getTmp().getMax() + du + dailyForecast.get(0).getTmp().getMin() + du2);
        tmp2.setText(dailyForecast.get(1).getTmp().getMax() + du + dailyForecast.get(0).getTmp().getMin() + du2);
        tmp3.setText(dailyForecast.get(2).getTmp().getMax() + du + dailyForecast.get(0).getTmp().getMin() + du2);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        for (int i = 0; i < hourlyForecast.size(); i++) {
            LinearLayout v = (LinearLayout) inflater.inflate(R.layout.hourly_forecast_one, hourlyForecastGroup, false);
            TextView timeTxt = (TextView) v.findViewById(R.id.id_hour_time);
            TextView tmpTxt = (TextView) v.findViewById(R.id.id_hour_tmp);

            try {
                Date date = format.parse(hourlyForecast.get(i).getDate());
                timeTxt.setText(date.getHours() + "h");
                tmpTxt.setText(hourlyForecast.get(i).getTmp() + du2);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            hourlyForecastGroup.addView(v);
        }

    }


    @Override
    public void initView(View view) {
        super.initView(view);
        setToolBarInfo(false, "Today");
        //当前天气
        condTxt = (TextView) view.findViewById(R.id.cond);
        temperatureTxt = (TextView) view.findViewById(R.id.temperature);
        airQualityNumTxt = (TextView) view.findViewById(R.id.air_quality_num);
        airQualityTxt = (TextView) view.findViewById(R.id.air_quality);
        airQualityBg = (LinearLayout) view.findViewById(R.id.air_quality_bg);


        //日期预告天气
        firstDay = (TextView) view.findViewById(R.id.id_firstDay);
        secondDay = (TextView) view.findViewById(R.id.id_secondDay);
        thirdDay = (TextView) view.findViewById(R.id.id_thirdDay);
        tmp1 = (TextView) view.findViewById(R.id.id_tmp1);
        tmp2 = (TextView) view.findViewById(R.id.id_tmp2);
        tmp3 = (TextView) view.findViewById(R.id.id_tmp3);

        //小时预告天气
        hourlyForecastGroup = (LinearLayout) view.findViewById(R.id.id_hourly_forecast);
        nowTmp = (TextView) view.findViewById(R.id.id_now_tmp);


    }


}
