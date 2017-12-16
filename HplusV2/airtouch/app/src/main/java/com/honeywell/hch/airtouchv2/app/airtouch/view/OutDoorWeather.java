package com.honeywell.hch.airtouchv2.app.airtouch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.model.xinzhi.WeatherData;

/**
 * Created by nan.liu on 3/12/15.
 */
public class OutDoorWeather extends RelativeLayout {

    private Context mContext;

    private View view;
    private ImageView weatherImageView;
    private TextView weatherTextView;
    private TextView temperatureTextView;
    private TextView PM25TextView;
    private int[] weatherIconID = {R.drawable.sunny, R.drawable.sunny, R.drawable.sunny,
            R.drawable.sunny, R.drawable.heavycloudy, R.drawable.lightcloudy,
            R.drawable.lightcloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy,
            R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.rainy,
            R.drawable.rainy, R.drawable.rainy, R.drawable.rainy, R.drawable.rainy,
            R.drawable.rainy, R.drawable.rainy, R.drawable.rainandsnow, R.drawable.snow,
            R.drawable.snow, R.drawable.snow, R.drawable.snow, R.drawable.snow,
            R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy,
            R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy,
            R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy, R.drawable.cloudy,
            R.drawable.cloudy, R.drawable.cloudy};

    public OutDoorWeather(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public OutDoorWeather(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.outdoor_weather, this);
        weatherImageView = (ImageView) view.findViewById(R.id.outdoor_weather_icon);
        weatherTextView = (TextView) view.findViewById(R.id.outdoor_weather);
        temperatureTextView = (TextView) view.findViewById(R.id.outdoor_temperature);
        PM25TextView = (TextView) view.findViewById(R.id.pm25_value);
    }

    public void updateView(WeatherData weatherData) {
        if (weatherData == null || weatherData.getWeather() == null || weatherData.getWeather()
                .size() == 0 || weatherData.getWeather().get(0) == null)
            return;
        showAnimation(view);
        if (weatherData.getWeather().get(0).getNow() != null) {
            weatherTextView.setText(weatherData.getWeather().get(0).getNow().getText());
            temperatureTextView.setText(weatherData.getWeather().get(0).getNow().getTemperature()
                    + mContext.getResources().getString(R.string.temp_unit_c));
            int weatherCode = weatherData.getWeather().get(0).getNow().getCode();
            if (weatherCode == 99)
                weatherCode = weatherIconID.length - 1;
            weatherImageView.setImageResource(weatherIconID[weatherCode]);
            int PM25 = Integer.valueOf(weatherData.getWeather().get(0).getNow().getAirQuality()
                    .getAirQualityIndex().getPm25());
            if (PM25 < 75) {
                PM25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_good));
            } else if (PM25 < 150) {
                PM25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_bad));
            } else {
                PM25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_worst));
            }
            PM25TextView.setText(PM25 + "");
            showAnimation(PM25TextView);
            showAnimation(weatherTextView);
            showAnimation(temperatureTextView);
        }
    }

    private void showAnimation(View view) {
        if (view == null || view.isShown())
            return;
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(400);
        animation.setInterpolator(new AccelerateInterpolator());
        view.setAnimation(animation);
        animation.start();
        view.setVisibility(View.VISIBLE);
    }

}
