package com.honeywell.hch.airtouchv3.app.airtouch.controller.weather;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.app.airtouch.model.tccmodel.weather.WeatherPageData;
import com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart.WeatherChartView;
import com.honeywell.hch.airtouchv3.app.airtouch.view.weatherchart.WeatherChartView.ChartSwitchListener;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.location.BlurBackgroundView;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.database.CityChinaDBService;
import com.honeywell.hch.airtouchv3.framework.webservice.task.LongTimerRefreshTask;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.squareup.otto.Subscribe;

/**
 * Created by lynnliu on 15/10/13.
 */
public class WeatherActivity extends BaseHasBackgroundActivity {

    public static final String LOCATION_ID = "location_id";
    private static final int NO_LOCATION_ID = -1;

    private TextView mHomeNameTextView;
    private TextView mCityNameTextView;
    private ImageView mCancelImageView;
    private WeatherChartView mWeatherChartView;

    private CityChinaDBService mCityChinaDBService = null;
    private City mCity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.weather_main);

        mHomeNameTextView = (TextView) findViewById(R.id.home_name);
        mCityNameTextView = (TextView) findViewById(R.id.home_location);
        mBlurBackgroundView = (BlurBackgroundView) findViewById(R.id.home_background);
        mCancelImageView = (ImageView) findViewById(R.id.cancel_btn);
        mWeatherChartView = (WeatherChartView) findViewById(R.id.weather_chart);

        mCancelImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mWeatherChartView.setChartSwitchListener(mChartSwitchListener);

        initDynamicBackground();
    }

    /**
     * set home name and city name
     *
     * @param homeName
     */
    protected void setHomeNameText(String homeName) {
        mHomeNameTextView.setText(homeName);
        if (mCity != null) {
            String cityText = "(" + (AppConfig.shareInstance().getLanguage().equals(AppConfig
                    .LANGUAGE_ZH) ? mCity.getNameZh() : mCity.getNameEn()) + ")";

            mCityNameTextView.setText(cityText);
        } else {
            mCityNameTextView.setText("(" + getString(R.string.enroll_gps_fail) + ")");
        }

        mHomeNameTextView.setText(homeName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadLocationData();
    }

    private void loadLocationData() {
            if (mUserLocationData == null)
                return;
            mCityChinaDBService = new CityChinaDBService(this);
            mCity = mCityChinaDBService.getCityByCode(mUserLocationData.getCity());
            setHomeNameText(mUserLocationData.getName());

            setWeatherData();

    }

    /**
     * set weather icon and air quality data
     */
    private void setWeatherData() {
        WeatherPageData weatherPageData = mUserLocationData.getCityWeatherData();
        if (weatherPageData == null || weatherPageData.getWeather() == null)
            return;
        mWeatherChartView.setWeather(weatherPageData);
        if (weatherPageData.getHourlyData() != null && weatherPageData.getHourlyData().length > 0
         && weatherPageData.getHourlyData()[0] != null) {
            mWeatherChartView.setHourlyWeather(weatherPageData);
        }
    }

    private ChartSwitchListener mChartSwitchListener = new ChartSwitchListener() {
        @Override
        public void onTodayChart() {
        }

        @Override
        public void on7DaysChart() {
        }
    };

    @Subscribe
    public void onLocationDataRefreshReceived(LongTimerRefreshTask.WeatherDataLoadedEvent
                                                      locationDataLoadedEvent) {
        if (mUserLocationData == null) {
            return;
        }
        if (StringUtil.isEmpty(locationDataLoadedEvent.getCity())) {
            mWeatherChartView.setWeather(mUserLocationData.getCityWeatherData());
        } else if (locationDataLoadedEvent.getCity().equals(mUserLocationData.getCity())) {
            mWeatherChartView.setHourlyWeather(mUserLocationData.getCityWeatherData());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWeatherChartView.closeWeather();
    }
}
