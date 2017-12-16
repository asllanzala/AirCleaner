package com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;

import java.util.List;


/**
 * SpinnerArrayAdapter for home selection
 */
public class HomeSpinnerAdapter<T> extends ArrayAdapter<T> {
    private Context mContext;
    private String[] mHomeStrings;
    private String mSelectedHome;

    public HomeSpinnerAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    public HomeSpinnerAdapter(Context context, T[] objects) {
        super(context, 0, objects);
        mContext = context;
        mHomeStrings = (String[])objects;
    }

    public String getSelectedHome() {
        return mSelectedHome;
    }

    protected String getItemValue(T item, Context context) {
        return item.toString();
    }

    @Override
    public int getCount() {
        if (mHomeStrings == null) {
            return 0;
        }
        return mHomeStrings.length;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout
                    .list_item_home_spinner_drop_down, parent, false);
        }

        TextView tv = (TextView) view.findViewById(R.id.list_item_home_drop_text);
        String city = getItemValue(getItem(position), getContext());
        tv.setText(city);

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout
                    .list_item_home_spinner, parent, false);
        }

        TextView tv = (TextView) view.findViewById(R.id.list_item_home_title);
        mSelectedHome = getItemValue(getItem(position), mContext);
        tv.setText(mSelectedHome);
        tv.setTextSize(20);

        return view;
    }

}

