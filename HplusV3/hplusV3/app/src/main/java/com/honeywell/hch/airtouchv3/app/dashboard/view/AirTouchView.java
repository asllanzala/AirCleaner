package com.honeywell.hch.airtouchv3.app.dashboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.view.TypeTextView;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv3.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;

import java.math.BigDecimal;

/**
 * Created by Qian Jin on 10/10/15.
 */
public class AirTouchView extends RelativeLayout {
    public final static String MODE_MANUAL = "Manual";
    public final static String MODE_AUTO = "Auto";
    public final static String MODE_SLEEP = "Sleep";
    public final static String MODE_QUICK = "QuickClean";
    public final static String MODE_SILENT = "Silent";
    public final static String MODE_OFF = "Off";
    public final static String PM25_NO_DATA = "...";
    public final static int PM25_MEDIUM_LIMIT = 75;
    public final static int PM25_HIGH_LIMIT = 150;

    public final static int PM25_MAX_VALUE = 999;

    public final static int ERROR_MAX_VALUE = 65535;
    public final static int ERROR_SENSOR = 65534;
    public final static double TVOC_ERROR_SENSOR = 65.53;
    public final static int GOOD = 200;
    public final static int AVERAGE = 400;
    public final static int POOR = 600;
    private final int NUMERICALWIDTH = 155;
    private final int SCREENWIDTH = 1080;
    private final int ERRORWIDTH = 80;
    public final static double TVOC_LOW_LIMIT_FOR_450 = 200;
    public final static double TVOC_HIGH_LIMIT_FOR_450 = 600;
    public final static double TVOC_LOW_LIMIT_FOR_PREMIUM = 0.35;
    public final static double TVOC_HIGH_LIMIT_FOR_PREMIUM = 0.45;
    private static final int TYPE_TIME_DELAY = 500;
    private Context mContext;


    private final String TAG = "AirTouchView";
    
    public AirTouchView(Context context) {
        super(context);

        mContext = context;
    }

    public AirTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
    }


    protected void updateTvocValue(Context context, HomeDevice homeDevice, TypeTextView tvocTextView, int where) {
        if (homeDevice == null || ((AirTouchSeriesDevice)homeDevice).getDeviceRunStatus() == null){

            return;
        }
        double tvocValueF = ((AirTouchSeriesDevice)homeDevice).getDeviceRunStatus().getTvocValue();
        LogUtil.log(LogUtil.LogLevel.INFO, TAG,"tvocValueF: "+tvocValueF);
        BigDecimal bg = null;
        if (where == 0) {
            float width = NUMERICALWIDTH * DensityUtil.getScreenWidth() /SCREENWIDTH;
            tvocTextView.setTextSize(DensityUtil.px2sp(width));
        }
        if (homeDevice.getDeviceInfo().getmDeviceType() != AirTouchConstants.AIRTOUCH450_TYPE) {
            if (tvocValueF > 0) {
                bg = new BigDecimal(tvocValueF / 1000);
//            tvocValueF = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            if (tvocValueF > 0 && tvocValueF < PM25_MAX_VALUE) {
                tvocValueF = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                tvocTextView.setTypeText("" + tvocValueF);
            }
            else{
                startTextViewLoopAnimation(tvocTextView);
            }

            if (tvocValueF < TVOC_LOW_LIMIT_FOR_PREMIUM || tvocValueF == ERROR_MAX_VALUE || tvocValueF == ERROR_SENSOR) {
                tvocTextView.setTextColor(context.getResources().getColor(R.color.pm_25_good));
            } else if (tvocValueF < TVOC_HIGH_LIMIT_FOR_PREMIUM) {
                tvocTextView.setTextColor(context.getResources().getColor(R.color.pm_25_bad));
            } else {
                tvocTextView.setTextColor(context.getResources().getColor(R.color.pm_25_worst));
            }
        } else {
            switch ((int)tvocValueF) {
                case GOOD:
                    tvocTextView.setTypeText(context.getText(R.string.tvoc450_good));
                    tvocTextView.setTextColor(context.getResources().getColor(R.color.pm_25_good));
                    break;
                case AVERAGE:
                    tvocTextView.setTypeText(context.getText(R.string.tvoc450_average));
                    tvocTextView.setTextColor(context.getResources().getColor(R.color.pm_25_bad));
                    break;
                case POOR:
                    tvocTextView.setTypeText(context.getText(R.string.tvoc450_poor));
                    tvocTextView.setTextColor(context.getResources().getColor(R.color.pm_25_worst));
                    break;
            }
            if (where == 1) {
                tvocTextView.setTextSize(12);
            }

        }
        if (tvocValueF == ERROR_MAX_VALUE || tvocValueF == ERROR_SENSOR) {
            startTextViewLoopAnimation(tvocTextView);
            tvocTextView.setTextColor(context.getResources().getColor(R.color.pm_25_good));
        }
    }

    protected void updateDeviceValue(Context context, HomeDevice homeDevice, TypeTextView pm25TextView,int where) {
        if (homeDevice == null || ((AirTouchSeriesDevice)homeDevice).getDeviceRunStatus() == null){
            return;
        }
        int pm25Value = ((AirTouchSeriesDevice)homeDevice).getDeviceRunStatus().getmPM25Value();
        LogUtil.log(LogUtil.LogLevel.INFO, TAG,"pm25Value: "+pm25Value);
        if(where == 0) {
            float width = NUMERICALWIDTH * DensityUtil.getScreenWidth() /SCREENWIDTH;
            pm25TextView.setTextSize(DensityUtil.px2sp(width));
        }
        if (pm25Value >= 0 && pm25Value < PM25_MAX_VALUE) {
            pm25TextView.setTypeText("" + pm25Value);
        }else if(pm25Value >= PM25_MAX_VALUE && pm25Value != ERROR_MAX_VALUE && pm25Value != ERROR_SENSOR){
            pm25TextView.setTypeText(String.valueOf(PM25_MAX_VALUE));
        }
        else if(pm25Value == ERROR_MAX_VALUE || pm25Value == ERROR_SENSOR) {
            startTextViewLoopAnimation(pm25TextView);
        }
        if (pm25Value < PM25_MEDIUM_LIMIT || pm25Value == ERROR_MAX_VALUE || pm25Value == ERROR_SENSOR) {
            pm25TextView.setTextColor(context.getResources().getColor(R.color.pm_25_good));
        } else if (pm25Value < PM25_HIGH_LIMIT) {
            pm25TextView.setTextColor(context.getResources().getColor(R.color.pm_25_bad));
        } else {
            pm25TextView.setTextColor(context.getResources().getColor(R.color.pm_25_worst));
        }

        if( AppManager.shareInstance().isAirtouch450(homeDevice.getDeviceInfo().getmDeviceType())
                && where == 1) {
            pm25TextView.setTextSize(12);
        }
    }

    protected void updateRunStatusAndFan(Context context, HomeDevice homeDevice,
                                         TextView statusTextView, ImageView fanImageView) {
        String modeOrSpeed = ((AirTouchSeriesDevice)homeDevice).getDeviceModeOrSpeed(context);
        statusTextView.setText(modeOrSpeed);
        showFanRotation(modeOrSpeed, fanImageView);
    }

    private void startTextViewLoopAnimation(final TypeTextView textView) {
        textView.startLoop(PM25_NO_DATA, TYPE_TIME_DELAY);
    }

    public void stopTyperTimer( TypeTextView pm25TextView, TypeTextView tvocValueTextView ){
        if (pm25TextView != null) {
            pm25TextView.stop();
        }
        if (tvocValueTextView != null) {
            tvocValueTextView.stop();
        }
    }

    private void showFanRotation(String modeOrSpeed, ImageView fanImageView) {
        Animation lowRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_low);
        Animation mediumRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_medium);
        Animation highRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_high);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        lowRotate.setInterpolator(linearInterpolator);
        mediumRotate.setInterpolator(linearInterpolator);
        highRotate.setInterpolator(linearInterpolator);

        if (modeOrSpeed.equals(mContext.getString(R.string.offline))) {
            fanImageView.clearAnimation();
        } else if (modeOrSpeed.equals(mContext.getString(R.string.control_auto))) {
            fanImageView.startAnimation(lowRotate);
        } else if (modeOrSpeed.equals(mContext.getString(R.string.control_sleep))) {
            fanImageView.startAnimation(lowRotate);
        } else if (modeOrSpeed.equals(mContext.getString(R.string.control_quick))) {
            fanImageView.startAnimation(highRotate);
        } else if (modeOrSpeed.equals(mContext.getString(R.string.control_silent))) {
            fanImageView.startAnimation(lowRotate);
        } else if (modeOrSpeed.equals(mContext.getString(R.string.off))) {
            fanImageView.clearAnimation();
        } else if (modeOrSpeed.equals(mContext.getString(R.string.speed_low))) {
            fanImageView.startAnimation(lowRotate);
        } else if (modeOrSpeed.equals(mContext.getString(R.string.speed_medium))) {
            fanImageView.startAnimation(mediumRotate);
        } else if (modeOrSpeed.equals(mContext.getString(R.string.speed_high))) {
            fanImageView.startAnimation(highRotate);
        } else {
            fanImageView.clearAnimation();
        }
    }

}
