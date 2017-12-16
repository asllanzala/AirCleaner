package com.honeywell.hch.airtouchv3.framework.app.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by nan.liu on 2/5/15.
 */
public abstract class ATBaseAdapter<T> extends BaseAdapter{
    protected ArrayList<T> mList = new ArrayList<>();
    public LayoutInflater mInflater;
    public Context mContext;

    public ATBaseAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setData(ArrayList<T> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(getItemLayout(), null);
            viewHolder = getViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (IViewHolder) convertView.getTag();
        }
        viewHolder.bindView(position);
        return convertView;
    }

    public abstract int getItemLayout();

    public abstract IViewHolder getViewHolder(View convertView);

    public interface IViewHolder {
        void bindView(int position);
    }
}
