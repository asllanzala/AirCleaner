package com.honeywell.hch.airtouchv2.framework.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class VerticalPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;


    public VerticalPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public VerticalPagerAdapter(FragmentManager fm,List<Fragment> oneListFragments){
        super(fm);
        this.fragments=oneListFragments;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

}