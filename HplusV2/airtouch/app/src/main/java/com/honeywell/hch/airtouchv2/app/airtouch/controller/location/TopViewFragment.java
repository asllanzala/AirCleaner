package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.airtouch.view.AirTouchWorstDevice;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;

import java.util.ArrayList;

/**
 * top view above haze view
 *  Created by Stephen Wu 2015.6.1
 */
public class TopViewFragment  extends TopViewBaseFragment
{
    private static final String ARG_HOME_INDEX = "homeIndex";

    private View topView;

    private RelativeLayout myHouseView;

    private ImageView houseImageView;

    private ImageView windowImageView;


    private float[] houseBottomDistance = {5.5f, 0.5f, 4f, 5.5f, 4.5f, 7.5f};

    private AirTouchWorstDevice mAirTouchWorstDevice = null;
    private View mHomeReminderView = null;
    private UserLocation mUserLocation = null;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param homeIndex
     * @return A new instance of fragment HomeCellFragment.
     */
    public static TopViewFragment newInstance(FragmentActivity activity, int homeIndex) {
        TopViewFragment fragment = new TopViewFragment();
        fragment.setActivity(activity);
        Bundle args = new Bundle();
        args.putInt(ARG_HOME_INDEX, homeIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mHomeIndex = getArguments().getInt(ARG_HOME_INDEX);
        }

        ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();

        if (userLocations != null && mHomeIndex > 0 && userLocations.size() > mHomeIndex - 1){
            mUserLocation = userLocations.get(mHomeIndex - 1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (topView == null){

            initHomeTopView(inflater,container);

        }
        if (topView.getParent() != null){
            ViewGroup p = (ViewGroup)topView.getParent();
            p.removeAllViews();


        }
        return topView;
    }

    private void initHomeTopView(LayoutInflater inflater,ViewGroup container)
    {
        topView = inflater.inflate(R.layout.top_lay_view_two, container, false);
        initBaseView(topView);
        if (mHomeIndex > 0)
        {
            if (mUserLocation != null && mUserLocation.getName() != null) {
                mCity = getCityDBService().getCityByCode(mUserLocation.getCity());
                setHomeNameText(getCityDBService().getCityByCode(mCity.getCode()), mUserLocation
                        .getName());
            }
        }
        else
        {
            AppConfig appConfig = AppConfig.shareInstance();
            setHomeNameText(getCityDBService().getCityByCode(appConfig.getGpsCityCode()),
                    getResources().getString(R.string.current_location));
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume(){
       super.onResume();
    }

    public void showNearHillNoAnimation()
    {
        setVisibility(mNearHillImageView, View.VISIBLE);
        setVisibility(mHomeNameTextView, View.VISIBLE);
        setVisibility(mHomeLocationTextView, View.VISIBLE);
    }

    public void showNearHillAnimation()
    {
         Animation showFromBottom = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim.nearhill_in_from_bottom);
        startAnimation(mHomeLocationTextView,showFromBottom);
        startAnimation(mHomeNameTextView,showFromBottom);
         startAnimation(mNearHillImageView,showFromBottom);
        setVisibility(mNearHillImageView, View.VISIBLE);
        setVisibility(mHomeNameTextView, View.VISIBLE);
        setVisibility(mHomeLocationTextView, View.VISIBLE);
    }

    public void hideNearHillAnimation()
    {
        Animation hideToBottom = AnimationUtils.loadAnimation(getFragmentActivity(), R.anim
                .out_to_bottom);
        hideToBottom.setStartOffset(600);
        startAnimation(mNearHillImageView, hideToBottom);
        startAnimation(mHomeNameTextView, hideToBottom);
        startAnimation(mHomeLocationTextView, hideToBottom);
        setVisibility(mNearHillImageView, View.INVISIBLE);
        setVisibility(mHomeNameTextView, View.INVISIBLE);
        setVisibility(mHomeLocationTextView, View.INVISIBLE);
    }

    public void hideNearHillNoAnimationOfScroll()
    {
        setVisibility(mNearHillImageView, View.INVISIBLE);
        setVisibility(mHomeNameTextView, View.INVISIBLE);
        setVisibility(mHomeLocationTextView, View.INVISIBLE);
    }

    public void setTopViewHillView(int homeIndex){
        if (mNearHillImageView != null){
            mNearHillImageView.setImageResource(nearbyMountainDayIDs[mHomeIndex % 6]);;
        }
    }

    public void updateHomeName(){
        ArrayList<UserLocation> userLocations = AuthorizeApp.shareInstance().getUserLocations();

        if (userLocations != null && mHomeIndex > 0 && userLocations.size() > mHomeIndex - 1){
            mUserLocation = userLocations.get(mHomeIndex - 1);
        }
        if (mHomeIndex > 0)
        {
            if (mUserLocation != null && mUserLocation.getName() != null) {
                mCity = getCityDBService().getCityByCode(mUserLocation.getCity());
                setHomeNameText(getCityDBService().getCityByCode(mCity.getCode()), mUserLocation
                        .getName());
            }
        }
    }

}
