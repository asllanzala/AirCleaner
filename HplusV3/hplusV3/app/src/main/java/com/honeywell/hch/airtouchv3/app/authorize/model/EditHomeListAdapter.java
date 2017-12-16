package com.honeywell.hch.airtouchv3.app.authorize.model;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by Qian Jin on 8/14/15.
 */
public class EditHomeListAdapter extends ArrayAdapter {
    private ArrayList<HomeAndCity> mHomeAndCityList;
    private InputMethodManager inputMethodManager;
    private int mSelectedPosition = -1;
    private EditText mHomeNameEditText;
    private Context mContext;
    private View mConvertView;
    private SetHomeCallback mSetHomeCallback;
    private RemoveHomeCallback mRemoveHomeCallback;
    private RenameHomeCallback mRenameHomeCallback;


    public EditHomeListAdapter(Context context, ArrayList<HomeAndCity> homeAndCityList) {
        super(context, 0);

        mContext = context;
        mHomeAndCityList = homeAndCityList;
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public int getCount() {
        if (mHomeAndCityList == null) {
            return 0;
        }
        return mHomeAndCityList.size();
    }

    public EditText getHomeNameEditText() {
        return mHomeNameEditText;
    }

    public void setHomeNameEditText(EditText homeNameEditText) {
        mHomeNameEditText = homeNameEditText;
    }

    public void setConvertView(View convertView) {
        mConvertView = convertView;
    }

    @Override
    public HomeAndCity getItem(int position) {
        return mHomeAndCityList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_edit_home,
                    parent, false);
        }
        setConvertView(convertView);

        // get view resource
        LinearLayout selected3ImageLayout = (LinearLayout) convertView.findViewById(R.id.selected_home_three_image);
        ImageView defaultHomeImageView = (ImageView) convertView.findViewById(R.id.default_home_iv);
        final TextView homeNameTextView = (TextView) convertView.findViewById(R.id.home_name_tv);
        homeNameTextView.setMaxWidth(DensityUtil.dip2px(130));
        final EditText homeNameEditText = (EditText) convertView.findViewById(R.id.home_name_et);
        homeNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                StringUtil.specialCharacterFilter(homeNameEditText);
                StringUtil.maxCharacterFilter(homeNameEditText);
                StringUtil.addOrEditHomeFilter(homeNameEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        hideEditHome(homeNameEditText, homeNameTextView);
        TextView homeCityTextView = (TextView) convertView.findViewById(R.id.home_city_tv);
//        homeCityTextView.setMaxWidth(DensityUtil.dip2px(75));
        ImageView defaultHomeImageButton = (ImageView) convertView.findViewById(R.id.select_default_home_iv);

        // 3 image icons click listener
        defaultHomeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetHomeCallback.callback();
                inputMethodManager.hideSoftInputFromWindow
                        (homeNameEditText.getWindowToken(), 0);
                hideEditHome(homeNameEditText, homeNameTextView);
            }
        });
        ImageView removeHomeImageButton = (ImageView) convertView.findViewById(R.id.select_remove_home_iv);
        removeHomeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemoveHomeCallback.callback();
                inputMethodManager.hideSoftInputFromWindow
                        (homeNameEditText.getWindowToken(), 0);
                hideEditHome(homeNameEditText, homeNameTextView);
            }
        });
        ImageView renameHomeImageButton = (ImageView) convertView.findViewById(R.id.select_rename_home_iv);
        renameHomeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeNameEditText.getVisibility() == View.VISIBLE) {
                    return;
                }

                mRenameHomeCallback.callback(homeNameEditText);
                showEditHome(homeNameEditText, homeNameTextView);
            }
        });

        // set TextView of home and city
        HomeAndCity homeAndCity = getItem(position);
        homeNameTextView.setText(homeAndCity.getHomeName());
        homeCityTextView.setText(" , " + homeAndCity.getHomeCity());
        homeCityTextView.setTextColor(mContext.getResources().getColor(R.color.login_hint_text));

        // setup image icons
        defaultHomeImageView.setVisibility(View.INVISIBLE);
        selected3ImageLayout.setVisibility(View.GONE);
        convertView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.edit_home_list));
        if (position == AppManager.shareInstance().getAuthorizeApp().getDefaultHomeNumber())
            defaultHomeImageView.setVisibility(View.VISIBLE);

        // setup selected list
        if (position == mSelectedPosition) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.list_item_bg));
            homeCityTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            selected3ImageLayout.setVisibility(View.VISIBLE);
        }

        inputMethodManager.hideSoftInputFromWindow(convertView.getWindowToken(), 0);

        return convertView;
    }

    /**
     * Callback interface setup
     */
    public interface SetHomeCallback {
        void callback();
    }

    public interface RemoveHomeCallback {
        void callback();
    }

    public interface RenameHomeCallback {
        void callback(EditText editText);
    }

    public void setRemoveHomeCallback(RemoveHomeCallback removeHomeCallback) {
        mRemoveHomeCallback = removeHomeCallback;
    }

    public void setRenameHomeCallback(RenameHomeCallback renameHomeCallback) {
        mRenameHomeCallback = renameHomeCallback;
    }

    public void setSetHomeCallback(SetHomeCallback setHomeCallback) {
        mSetHomeCallback = setHomeCallback;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void showEditHome(final EditText homeNameEditText, final TextView homeNameTextView) {
        homeNameEditText.setText(homeNameTextView.getText());
        homeNameEditText.setVisibility(View.VISIBLE);
        homeNameEditText.setFocusable(true);
        homeNameEditText.setFocusableInTouchMode(true);
        homeNameEditText.requestFocus();
        homeNameEditText.setCursorVisible(true);
        homeNameEditText.selectAll();
        homeNameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
                        && inputMethodManager.isActive()) {
                    homeNameEditText.setVisibility(View.GONE);
                    homeNameTextView.setText(homeNameEditText.getText());
                    homeNameTextView.setVisibility(View.VISIBLE);
                    inputMethodManager.hideSoftInputFromWindow(homeNameEditText.getWindowToken(), 0);
                }
                return false;
            }
        });

        inputMethodManager.showSoftInput(homeNameEditText, InputMethodManager.SHOW_FORCED);
        setHomeNameEditText(homeNameEditText);

        homeNameTextView.setVisibility(View.GONE);
    }

    public void hideEditHome(EditText homeNameEditText, TextView homeNameTextView) {
        homeNameEditText.setVisibility(View.GONE);
        homeNameTextView.setVisibility(View.VISIBLE);
    }

}
