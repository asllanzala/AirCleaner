package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.app.activity.ATBaseAdapter;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by nan.liu on 2/2/15.
 */
public class CitiesAdapter extends ATBaseAdapter<City> implements SectionIndexer {
    private boolean isSearchView;
    private String[] mNicks = null;
    private HashMap<String, City> citiesMap = new HashMap<>();
    private String currentCityKey = null;

    private View preferenceView = null;

    public CitiesAdapter(Context context, boolean isSearchView) {
        super(context);
        this.isSearchView = isSearchView;
    }

    public void setPreferenceView(View preferenceView) {
        this.preferenceView = preferenceView;
    }

    public void setCurrentCityKey(String currentCityKey) {
        this.currentCityKey = currentCityKey;
    }

    public City getSelectedCity() {
        return citiesMap.get(currentCityKey);
    }

    @Override
    public void setData(ArrayList<City> list) {
        mList = list;
        mNicks = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            mNicks[i] = list.get(i).getNameEn() + list.get(i).getCode();
            citiesMap.put(mNicks[i], list.get(i));
        }
        Arrays.sort(mNicks);
        City lastCity = citiesMap.get(currentCityKey);
        if (lastCity != null) {
            lastCity.setCurrent(1);
            citiesMap.put(currentCityKey, lastCity);
        }
    }

    @Override
    public City getItem(int position) {
        return citiesMap.get(mNicks[position]);
    }

    @Override
    public int getItemLayout() {
        return R.layout.city_list_item;
    }

    @Override
    public IViewHolder getViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < mNicks.length; i++) {
            String l = mNicks[i].substring(0, 1);
            char firstChar = l.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    class ViewHolder implements IViewHolder {
        private ImageView isSelected;
        private TextView cityName;
        private TextView catalog;
        private View itemView;

        public ViewHolder(View convertView) {
            itemView = convertView;
            isSelected = (ImageView) convertView.findViewById(R.id.is_selected);
            cityName = (TextView) convertView.findViewById(R.id.city_name);
            catalog = (TextView) convertView.findViewById(R.id.list_catalog);
        }

        private OnClickListener itemClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag(R.id.data_id);
                if (mNicks[position].equals(currentCityKey))
                    return;
                City city = getItem(position);
                city.setCurrent(1);
                citiesMap.put(mNicks[position], city);
                City lastCity = citiesMap.get(currentCityKey);
                if (lastCity != null) {
                    lastCity.setCurrent(0);
                    citiesMap.put(currentCityKey, lastCity);
                }
                currentCityKey = mNicks[position];
                notifyDataSetChanged();
                preferenceView.setVisibility(View.VISIBLE);
            }
        };

        @Override
        public void bindView(int position) {
            City city = getItem(position);
            isSelected.setVisibility(city.isCurrent() == 1 ? View.VISIBLE : View.INVISIBLE);
            cityName.setText(AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH) ? city
                    .getNameZh() : city.getNameEn());
            setCatalog(position);
            itemView.setTag(R.id.data_id, position);
            itemView.setOnClickListener(itemClickListener);
        }

        private void setCatalog(int position) {
            String initial = "";
            String lastInitial = "";
            initial = mNicks[position].substring(0, 1);
            if (isSearchView) {
                this.catalog.setVisibility(View.GONE);
                return;
            }
            if (position > 0) {
                lastInitial = mNicks[position - 1].substring(0, 1);
            }
            if (initial.equals(lastInitial)) {
                this.catalog.setVisibility(View.GONE);
            } else {
                this.catalog.setVisibility(View.VISIBLE);
                this.catalog.setText(initial);
            }
        }
    }
}
