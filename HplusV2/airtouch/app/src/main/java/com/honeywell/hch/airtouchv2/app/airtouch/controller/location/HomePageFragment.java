package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseFragment;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.framework.view.VerticalPagerAdapter;
import com.honeywell.hch.airtouchv2.framework.view.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * The Fragment for one home(location).
 * Created by nan.liu on 2/24/15.
 */
public class HomePageFragment extends BaseFragment {
    private static final String ARG_HOME_INDEX = "homeIndex";
    public static final String ARG_LOCATION = "location";
    private static final String TAG = "AirTouchHomePageFragment";

    private int mHomeIndex = 0;
    private UserLocation mUserLocation = null;
    private VerticalViewPager mVerticalViewPager;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private VerticalPagerAdapter verticalPagerAdapter;
    private FragmentActivity mActivity;
    private HomeSkyFragment mHomeSkyFragment;
    private HomeCellFragment mHomeCellFragment;
    private CurrentGpsFragment mCurrentLocationFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param homeIndex
     * @return A new instance of fragment HomePageFragment.
     */
    public static HomePageFragment newInstance(FragmentActivity activity, int homeIndex,
                                               UserLocation userLocation) {
        HomePageFragment fragment = new HomePageFragment();
        fragment.setActivity(activity);
        fragment.loadFragmentList(homeIndex, userLocation);
        Bundle args = new Bundle();
        args.putInt(ARG_HOME_INDEX, homeIndex);
        args.putSerializable(ARG_LOCATION, userLocation);
        fragment.setArguments(args);
        return fragment;
    }

    public static HomePageFragment newInstance(FragmentActivity activity) {
        HomePageFragment fragment = new HomePageFragment();
        fragment.setActivity(activity);
        fragment.loadFragmentList();
        return fragment;
    }

    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
        if (getArguments() != null) {
            mHomeIndex = getArguments().getInt(ARG_HOME_INDEX);
            mUserLocation = (UserLocation) getArguments().getSerializable(ARG_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        mVerticalViewPager = (VerticalViewPager) view.findViewById(R.id.home_verticalViewPager);
        verticalPagerAdapter = new VerticalPagerAdapter(getChildFragmentManager(), mFragmentList);
        mVerticalViewPager.setAdapter(verticalPagerAdapter);
        mVerticalViewPager.setCurrentItem(1);
        mVerticalViewPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    ((MainActivity)mActivity).getViewPager().requestDisallowInterceptTouchEvent
                            (false);
                else
                    ((MainActivity)mActivity).getViewPager().requestDisallowInterceptTouchEvent
                            (true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void loadFragmentList(int homeIndex, UserLocation userLocation) {
        mHomeSkyFragment = new HomeSkyFragment();
        mFragmentList.add(mHomeSkyFragment);

        mHomeCellFragment = HomeCellFragment.newInstance(mActivity, homeIndex);
        mFragmentList.add(mHomeCellFragment);
    }

    public void loadFragmentList() {
        mHomeSkyFragment = new HomeSkyFragment();
        mFragmentList.add(mHomeSkyFragment);

        mCurrentLocationFragment = CurrentGpsFragment.newInstance(mActivity);
        mFragmentList.add(mCurrentLocationFragment);
    }

    public HomeCellFragment getHomeCellFragment() {
        return mHomeCellFragment;
    }
}
