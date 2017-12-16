package com.honeywell.hch.airtouchv2.app.airtouch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.model.AirTouchSeriesDevice;
import com.honeywell.hch.airtouchv2.framework.model.RunStatus;
import com.honeywell.hch.airtouchv2.lib.util.LogUtil;

/**
 * Custom view for worst device info at home page.
 * Created by nan.liu on 3/3/15.
 */
public class AirTouchWorstDevice extends RelativeLayout {

    private TextView deviceStatusTextView;
    private ImageView deviceStatusImageView;
    private TextView deviceNameTextView;
    private TextView pm25TextView;
    private TextView cleanTimeTextView;

    private Context mContext;
    private AirTouchSeriesDevice mHomeDevice;

    public AirTouchWorstDevice(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public AirTouchWorstDevice(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.device_worst, this);
        deviceStatusTextView = (TextView) findViewById(R.id.device_status_text);
        deviceStatusImageView = (ImageView) findViewById(R.id.device_status_image);
        deviceNameTextView = (TextView) findViewById(R.id.device_name);
        pm25TextView = (TextView) findViewById(R.id.pm25_value);
        cleanTimeTextView = (TextView) findViewById(R.id.clean_time_tv);
    }

    public void updateView(AirTouchSeriesDevice homeDevice) {
        mHomeDevice = homeDevice;
        if (mHomeDevice == null || mHomeDevice.getDeviceInfo() == null)
        {
            return;
        }

        deviceNameTextView.setText(mHomeDevice.getDeviceInfo().getName());
        setPm25TextViewVlaue();
        setDeviceStatusImageViewValue();

    }

    public void updateCleanTime(String text) {
        cleanTimeTextView.setText(text);
    }

    private void setPm25TextViewVlaue() {
        RunStatus runStatus = mHomeDevice.getDeviceRunStatus();
        int pm25Value = 0;
        if (runStatus != null){
            pm25Value = runStatus.getmPM25Value();
        }
        if (pm25Value > 0 && pm25Value < 999) {
            pm25TextView.setText("" + pm25Value);
        } else {
            pm25Value = 0;
            pm25TextView.setText("...");
        }

        if (pm25Value < 75) {
            pm25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_good));
        } else if (pm25Value < 150) {
            pm25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_bad));
        } else {
            pm25TextView.setTextColor(mContext.getResources().getColor(R.color.pm_25_worst));
        }
    }

    private void setDeviceStatusImageViewValue(){

        Animation lowRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_low);
        Animation mediumRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_medium);
        Animation highRotate = AnimationUtils.loadAnimation(mContext, R.anim.fan_rotate_high);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        lowRotate.setInterpolator(linearInterpolator);
        mediumRotate.setInterpolator(linearInterpolator);
        highRotate.setInterpolator(linearInterpolator);

        // Auto, High, Low ...
        String status = "";

        if (!mHomeDevice.getDeviceInfo().getIsAlive()) {
            status = mContext.getString(R.string.offline);
            deviceStatusTextView.setText(status);
            deviceStatusImageView.clearAnimation();
            return;
        }

        if (mHomeDevice.getDeviceRunStatus() != null) {
            status = mHomeDevice.getDeviceRunStatus().getScenarioMode();
            if (status.equals("Manual")) {
                switch (mHomeDevice.getDeviceRunStatus().getFanSpeedStatus()) {
                    case "Speed_1":
                    case "Speed_2":
                        status = mContext.getString(R.string.speed_low);
                        deviceStatusImageView.startAnimation(lowRotate);
                        break;
                    case "Speed_3":
                    case "Speed_4":
                    case "Speed_5":
                        status = mContext.getString(R.string.speed_medium);
                        deviceStatusImageView.startAnimation(mediumRotate);
                        break;
                    case "Speed_6":
                    case "Speed_7":
                        status = mContext.getString(R.string.speed_high);
                        deviceStatusImageView.startAnimation(highRotate);
                        break;
                    default:
                        break;
                }
            } else if (status.equals("Auto")) {
                status = mContext.getString(R.string.control_auto);
                deviceStatusImageView.startAnimation(lowRotate);
            } else if (status.equals("Sleep")) {
                status = mContext.getString(R.string.control_sleep);
                deviceStatusImageView.startAnimation(lowRotate);
            } else if (status.equals("QuickClean")) {
                status = mContext.getString(R.string.control_quick);
                deviceStatusImageView.startAnimation(highRotate);
            } else if (status.equals("Silent")) {
                status = mContext.getString(R.string.control_silent);
                deviceStatusImageView.startAnimation(lowRotate);
            } else if (status.equals("Off")) {
                status = mContext.getString(R.string.off);
                deviceStatusImageView.clearAnimation();
            }
        }
        else
        {
            LogUtil.log(LogUtil.LogLevel.ERROR,"WorseDevice","mHomeDevice.getDeviceRunStatus() is null and there is no status value");
        }
        deviceStatusTextView.setText(status);
    }

}
