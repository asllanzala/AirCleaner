package com.honeywell.hch.airtouchv3.app.authorize.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Qian Jin on 11/19/15.
 */
public class CountryCodeAdapter<T> extends ArrayAdapter<T> {
    private Map<String, Integer> mCountryCodeMap = new HashMap<>();

    public CountryCodeAdapter(Context context, List<T> objects) {
        super(context, 0, objects);

        getCountryCodeMap();
    }

    public CountryCodeAdapter(Context context, T[] objects) {
        super(context, 0, objects);

        getCountryCodeMap();
    }

    private void getCountryCodeMap() {
        mCountryCodeMap.put(AirTouchConstants.CHINA_CODE, R.drawable.china_flag);
        mCountryCodeMap.put(AirTouchConstants.INDIA_CODE, R.drawable.india_flag);
    }

    public String getItemValue(T item, Context context) {
        if (item != null){
            return item.toString();
        }
        return "";
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout
                    .list_item_country_code_spinner_drop_down, parent, false);
        }

        TextView tv = (TextView) view.findViewById(R.id.list_item_country_code_drop_text);
        String countryCode = getItemValue(getItem(position), getContext());
        tv.setText("+" + countryCode);

        ImageView iv = (ImageView) view.findViewById(R.id.list_item_country_code_drop_image);
        iv.setImageResource(mCountryCodeMap.get(countryCode));

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout
                    .list_item_country_code_spinner, parent, false);
        }

        TextView tv = (TextView) view.findViewById(R.id.list_item_country_code_title);
        String countryCode = getItemValue(getItem(position), getContext());
        tv.setText("+" + countryCode);

        ImageView iv = (ImageView) view.findViewById(R.id.list_item_country_code_image);
        iv.setImageResource(mCountryCodeMap.get(countryCode));

        return view;
    }


}
