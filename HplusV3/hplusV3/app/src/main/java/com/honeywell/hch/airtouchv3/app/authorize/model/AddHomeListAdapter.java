package com.honeywell.hch.airtouchv3.app.authorize.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;

import java.util.ArrayList;

/**
 * Created by Qian Jin on 8/18/15.
 */
public class AddHomeListAdapter extends ArrayAdapter {
    private Context mContext;
    private ArrayList<City> mCityArrayList;


    public AddHomeListAdapter(Context context, ArrayList<City> cityArrayList) {
        super(context, 0);

        mContext = context;
        mCityArrayList = cityArrayList;
    }

    @Override
    public int getCount() {
        if (mCityArrayList == null) {
            return 0;
        }
        return mCityArrayList.size();
    }

    @Override
    public City getItem(int position) {
        return mCityArrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_add_home,
                    parent, false);
        }

        // get view resource
        TextView cityTextView = (TextView) convertView.findViewById(R.id.city_name_tv);
        cityTextView.setText(getCityName(getItem(position)));

        return convertView;
    }

    public String getCityName(City city) {
        return AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH)
                ? city.getNameZh() : city.getNameEn();


    }


}
