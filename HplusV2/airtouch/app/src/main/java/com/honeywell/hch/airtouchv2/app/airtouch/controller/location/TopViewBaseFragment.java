package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseRequestFragment;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.database.CityDBService;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.City;

/**
 * Created by wuyuan on 15/6/1.
 */
public class TopViewBaseFragment  extends BaseRequestFragment
{
    protected TextView mHomeNameTextView = null;
    protected TextView mHomeLocationTextView = null;
    protected ImageView mNearHillImageView;
    protected int[] nearbyMountainDayIDs = {R.drawable.image01, R.drawable.image11,
            R.drawable.image21, R.drawable.image31, R.drawable.image41, R.drawable.image51};

    protected  int mHomeIndex;

    protected  FragmentActivity mActivity;

    public CityDBService mCityDBService;
    protected City mCity = null;


    protected void initBaseView(View view)
    {
        mHomeNameTextView = (TextView) view.findViewById(R.id.home_name);
        mHomeLocationTextView = (TextView) view.findViewById(R.id.home_location);
        mNearHillImageView = (ImageView)view.findViewById(R.id.nearby_mountain_two);
        mNearHillImageView.setImageResource(nearbyMountainDayIDs[mHomeIndex % 6]);

    }

    protected void setHomeNameText(City city, String homeName)
    {
        this.mCity = city;
        mHomeNameTextView.setText(homeName);
        if (city.getNameZh() != null && city.getNameZh() != null)
        {
            String cityText = "(" + (AppConfig.shareInstance().getLanguage().equals(AppConfig
                    .LANGUAGE_ZH) ? city.getNameZh() : city.getNameEn()) + ")";

            mHomeLocationTextView.setText(cityText);
        } else
        {
            mHomeLocationTextView.setText("(" + getString(R.string.enroll_gps_fail) + ")");
        }
    }

    public FragmentActivity getFragmentActivity()
    {
        if (mActivity == null)
            mActivity = getActivity();
        return mActivity;
    }

    public CityDBService getCityDBService()
    {
        if (mCityDBService == null)
        {
            mCityDBService = new CityDBService(getFragmentActivity());
        }
        return mCityDBService;
    }

    public void setVisibility(View view, int visibility)
    {
        if (view == null)
            return;
        view.setVisibility(visibility);
    }

    protected void startAnimation(View view, Animation animation)
    {
        if (view == null || animation == null)
            return;
        view.startAnimation(animation);
    }
}
