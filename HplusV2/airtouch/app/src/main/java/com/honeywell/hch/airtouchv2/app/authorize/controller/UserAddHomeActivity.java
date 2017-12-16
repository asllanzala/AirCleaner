package com.honeywell.hch.airtouchv2.app.authorize.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.AddLocationRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.app.authorize.model.AddHomeListAdapter;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.framework.database.CityDBService;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.view.AirTouchEditText;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.task.AddLocationTask;
import com.honeywell.hch.airtouchv2.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import java.util.ArrayList;


/**
 * Created by Jin Qian on 8/19/2015.
 */
public class UserAddHomeActivity extends BaseActivity {
    private FrameLayout goBackLayout;
    private Button addHomeButton;
    private ListView mAddHomeListView;
    private AddHomeListAdapter mAddHomeListAdapter;
    private AirTouchEditText searchCityEditText;
    private AirTouchEditText nameHomeEditText;
    private ImageView searchIcon;
    private ProgressDialog mDialog;

    private CityDBService mCityDBService;
    protected ArrayList<City> mCitiesList = new ArrayList<>();
    private City mSelectedCity;
    private InputMethodManager imm;
    private Context mContext;

    private boolean isCitySelected;
    private static String TAG = "AirTouchUserAddHome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        mContext = UserAddHomeActivity.this;
        mCityDBService = new CityDBService(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            Intent intent = new Intent();
            intent.setClass(mContext, UserEditHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            finish();
        }

        return false;
    }

    private void initView() {
        searchIcon = (ImageView) findViewById(R.id.search_place_iv);
        mAddHomeListView = (ListView) findViewById(R.id.home_city_listView);

        goBackLayout = (FrameLayout) findViewById(R.id.add_home_back_layout);
        goBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, UserEditHomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });

        addHomeButton = (Button) findViewById(R.id.add_home_button_confirm);
        addHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAddHome();
            }
        });

        initEditText();

    }

    private void initEditText() {
        searchCityEditText = (AirTouchEditText) findViewById(R.id.search_place_et);
        searchCityEditText.getEditText().setTextColor(mContext.getResources().getColor(R.color.white));
        searchCityEditText.setImage(AirTouchEditText.ComponentType.REMOVE);
        searchCityEditText.setInputMaxLength(14);
        searchCityEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringUtil.specialCharacterFilter(searchCityEditText);
                StringUtil.maxCharacterFilter(searchCityEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(searchCityEditText.getEditorText())) {
                    searchIcon.setVisibility(View.INVISIBLE);
                } else {
                    searchIcon.setVisibility(View.VISIBLE);
                    searchCityEditText.showClearButton(false);
                    isCitySelected = false;

                    mCitiesList.clear();
                    if (mAddHomeListAdapter != null) {
                        mAddHomeListAdapter.notifyDataSetChanged();
                    }
                    setCityListViewHeight(mCitiesList.size());
                }

                updateCitiesListFromSearch();
                decideButtonShowOrNot();
            }
        });

        nameHomeEditText = (AirTouchEditText) findViewById(R.id.name_place_et);
        nameHomeEditText.getEditText().setTextColor(getResources().getColor(R.color.white));
        nameHomeEditText.setEditorHint(getString(R.string.my_home));
        nameHomeEditText.setEditorHintColor(getResources().getColor(R.color.login_hint_text));
        nameHomeEditText.setImage(AirTouchEditText.ComponentType.REMOVE);
        nameHomeEditText.setInputMaxLength(14);
        nameHomeEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StringUtil.specialCharacterFilter(nameHomeEditText);
                StringUtil.maxCharacterFilter(nameHomeEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                decideButtonShowOrNot();
            }
        });
    }

    private void updateCitiesListFromSearch() {
//        if (imm.isActive()) {
//            imm.hideSoftInputFromWindow(findViewById(R.id.search_place_et)
//                    .getWindowToken(), 0);
//        }

        String searchCityName = searchCityEditText.getEditorText().trim();
        if (!StringUtil.isEmpty(searchCityName)) {
            mCitiesList = mCityDBService.getCitiesByKey(searchCityName);
            if (null == mCitiesList)
                return;
        }

        setCityListViewHeight(mCitiesList.size());

        mAddHomeListAdapter = new AddHomeListAdapter(mContext, mCitiesList);
        mAddHomeListView.setAdapter(mAddHomeListAdapter);
        mAddHomeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isCitySelected = true;
                mSelectedCity = mAddHomeListAdapter.getItem(position);

                // show city name in the EditText
                String cityName = mAddHomeListAdapter.getCityName(mAddHomeListAdapter.getItem(position));
                searchCityEditText.setEditorText(cityName);
                mCitiesList.clear();
                mAddHomeListAdapter.notifyDataSetChanged();

                // set focus on next EditText
                nameHomeEditText.getEditText().requestFocus();
                nameHomeEditText.getEditText().setFocusable(true);
                nameHomeEditText.getEditText().setFocusableInTouchMode(true);
                // set cursor to the start of the text
//                nameHomeEditText.getEditText().setCursorVisible(true);
//                Editable et = nameHomeEditText.getEditText().getText();
//                Selection.setSelection(et, 0);
//                imm.showSoftInput(nameHomeEditText, 0);
            }
        });

    }

    private void setCityListViewHeight(int size) {
        ViewGroup.LayoutParams params = mAddHomeListView.getLayoutParams();
        if (size > 3) {
            params.height = DensityUtil.dip2px(178); // 3.5 height of listView
        } else {
            params.height = ListView.LayoutParams.WRAP_CONTENT;
        }
        mAddHomeListView.setLayoutParams(params);
    }


    private void decideButtonShowOrNot() {
        if (!StringUtil.isEmpty(nameHomeEditText.getEditorText())
                && isCitySelected) {
            addHomeButton.setVisibility(View.VISIBLE);
        } else {
            addHomeButton.setVisibility(View.INVISIBLE);
        }
    }

    private void processAddHome() {
        IActivityReceive addLocationResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }

                if (responseResult.isResult()) {
                    switch (responseResult.getRequestId()) {
                        case ADD_LOCATION:
                            if (responseResult.getResponseCode() == StatusCode.OK) {
                                finishAndQuit();
                            } else {
                                errorHandle(responseResult, getString(R.string.enroll_error));
                            }
                            return;

                        default:
                            break;
                    }
                } else {
                    errorHandle(responseResult, getString(R.string.enroll_error));
                }
            }
        };

        // used home name is not allowed to add again
        for (int i = 0; i < AuthorizeApp.shareInstance().getUserLocations().size(); i++) {
            UserLocation userLocation = AuthorizeApp.shareInstance().getUserLocations().get(i);
            if (nameHomeEditText.getEditorText().equals(userLocation.getName())
                    && (userLocation.getCity().equals(mSelectedCity.getCode()))) {
                MessageBox.createSimpleDialog(this, null,
                        getString(R.string.same_home), null, null);
                return;
            }
        }

        mDialog = ProgressDialog.show(mContext, null, getString(R.string.adding_home));
        AddLocationRequest addLocationRequest = new AddLocationRequest();
        addLocationRequest.setCity(mSelectedCity.getCode());
        addLocationRequest.setName(nameHomeEditText.getEditorText());
        AddLocationTask requestTask
                = new AddLocationTask(addLocationRequest, addLocationResponse);
        requestTask.execute();
    }

    private void finishAndQuit() {
        // send broadcast and notify home page
        Intent intent = new Intent(AirTouchConstants.ADD_DEVICE_OR_HOME_ACTION);
        intent.putExtra(AirTouchConstants.IS_ADD_HOME, true);
        sendBroadcast(intent);

        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
