package com.honeywell.hch.airtouchv2.app.airtouch.controller.location;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;

import java.util.List;

/**
 * Implementation of {@link android.support.v4.view.PagerAdapter} that
 * represents each page as a {@link Fragment} that is persistently
 * kept in the fragment manager as long as the user can return to the page.
 *
 * Modified from {@link android.support.v4.app.FragmentPagerAdapter},
 * change the type of the itemId to String (generate unique id based on location id and devices id).
 *
 * Created by nan.liu on 2/24/15.
 */

public class HomePageAdapter extends PagerAdapter {
    private List<HomeHalfPageFragment> fragments;
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    public HomePageAdapter(FragmentManager fm, List<HomeHalfPageFragment> oneListFragments) {
        this.mFragmentManager = fm;
        this.fragments = oneListFragments;
    }

    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    /*
     * Generate unique fragment id based on location id and device id,
     * New fragment will be created when location data changed,
     * otherwise fragment in memory will be showed with old location data.
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */
    public String getItemId(int position) {
        String itemId = position + "";
        if (position != 0 && AuthorizeApp.shareInstance().getUserLocations().size() >= position) {
            UserLocation userLocation = AuthorizeApp.shareInstance().getUserLocations().get
                    (position - 1);
            itemId = userLocation.getLocationID() + "";
            if (userLocation.getDeviceInfo() != null) {
                for (int i = 0; i < userLocation.getDeviceInfo().size(); i++) {
                    itemId += userLocation.getDeviceInfo().get(i).getDeviceID();
                }
            }
        }
        return itemId;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        final String itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(position);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);

        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment, makeFragmentName(position));
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    private static String makeFragmentName(int position) {
        return String.valueOf(position);
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    //clear all Fragment
    public void logoutClear(){
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (fragments != null){
            for(int i = 0;i < fragments.size();i++){
                HomeHalfPageFragment fitem = fragments.get(i);
                mCurTransaction.remove(fitem);
            }

        }
    }

    public void removeFragmentItem(int postion){
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (fragments != null && postion < fragments.size()){
                HomeHalfPageFragment fitem = fragments.get(postion);
                mCurTransaction.remove(fitem);
        }
    }


}
