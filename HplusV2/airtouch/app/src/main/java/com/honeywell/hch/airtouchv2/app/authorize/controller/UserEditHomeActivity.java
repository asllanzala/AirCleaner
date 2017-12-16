package com.honeywell.hch.airtouchv2.app.authorize.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.request.SwapLocationRequest;
import com.honeywell.hch.airtouchv2.app.airtouch.model.tccmodel.user.response.UserLocation;
import com.honeywell.hch.airtouchv2.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv2.app.authorize.model.EditHomeListAdapter;
import com.honeywell.hch.airtouchv2.app.authorize.model.HomeAndCity;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.webservice.task.DeleteLocationTask;
import com.honeywell.hch.airtouchv2.framework.webservice.task.ResponseResult;
import com.honeywell.hch.airtouchv2.framework.webservice.task.SwapLocationNameTask;
import com.honeywell.hch.airtouchv2.lib.http.IActivityReceive;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;

import java.util.ArrayList;


/**
 * Created by Jin Qian on 8/13/2015.
 */
public class UserEditHomeActivity extends BaseActivity implements View.OnClickListener {
    private FrameLayout mGoBackLayout;
    private FrameLayout mAddPlaceLayout;
    private ArrayList<HomeAndCity> mHomeAndCityList;
    private ListView mEditHomeListView;
    private EditHomeListAdapter mEditHomeListAdapter;
    private Button mLeftButton, mRightButton, mConfirmButton;
    private ProgressDialog mDialog;
    private HomeAndCity mSelectedHome;
    private Boolean mIsSetDefaultHome;

    private Context mContext;
    private static String TAG = "AirTouchUserEditHome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);
        mContext = UserEditHomeActivity.this;

        initView();
        initHomeListView();

        AppConfig.shareInstance().setHomePageCover(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }

        return false;
    }

    private void initView() {
        mConfirmButton = (Button) findViewById(R.id.edit_home_button_confirm);
        mLeftButton = (Button) findViewById(R.id.edit_home_button_left);
        mRightButton = (Button) findViewById(R.id.edit_home_button_right);
        mGoBackLayout = (FrameLayout) findViewById(R.id.edit_home_back_layout);
        mAddPlaceLayout = (FrameLayout) findViewById(R.id.add_home_layout);
        mEditHomeListView = (ListView) findViewById(R.id.edit_home_list);
        mGoBackLayout.setOnClickListener(this);
        mAddPlaceLayout.setOnClickListener(this);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_home_back_layout:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                if (mEditHomeListAdapter.getHomeNameEditText() != null) {
                    inputMethodManager.hideSoftInputFromWindow
                            (mEditHomeListAdapter.getHomeNameEditText().getWindowToken(), 0);
                }

                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;

            case R.id.add_home_layout:
                // if home number is above 5, stop to add home
                if (AuthorizeApp.shareInstance().getUserLocations().size() >= AirTouchConstants.MAX_HOME_NUMBER) {
                    MessageBox.createSimpleDialog(UserEditHomeActivity.this, null,
                            getString(R.string.max_home2), null, null);
                    break;
                }
                Intent intent = new Intent();
                intent.setClass(mContext, UserAddHomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                finish();
                break;

            case R.id.edit_home_button_left:
                backToInitStat();
                break;

            case R.id.edit_home_button_right:
                processRemoveHome();
                break;

            case R.id.edit_home_button_confirm:
                if (mIsSetDefaultHome) {
                    processSetDefaultHome();
                } else {
                    // used home name in same city is not allowed to add again
                    for (int i = 0; i < AuthorizeApp.shareInstance().getUserLocations().size(); i++) {
                        UserLocation userLocation = AuthorizeApp.shareInstance().getUserLocations().get(i);
                        City mCity = getCityDBService().getCityByCode(userLocation.getCity());
                        String cityNameCnOrEn = AppConfig.shareInstance().getLanguage().equals(AppConfig
                                .LANGUAGE_ZH) ? mCity.getNameZh() : mCity.getNameEn();
                        if (mEditHomeListAdapter.getHomeNameEditText().getText().toString().equals(userLocation.getName())
                                && (cityNameCnOrEn.equals(mSelectedHome.getHomeCity()))
                                && (i != mEditHomeListAdapter.getSelectedPosition())) {
                            MessageBox.createSimpleDialog(UserEditHomeActivity.this, null,
                                    getString(R.string.same_home), null, null);
                            return;
                        }
                    }
                    processRenameHome(mEditHomeListAdapter.getHomeNameEditText());
                }
                break;

            default:
                break;
        }
    }

    private void initHomeListView() {
        initHomeList();
        setupListAdapter();
    }

    private void initHomeList() {
        mHomeAndCityList = new ArrayList<>();

        ArrayList<UserLocation> userLocations
                = AuthorizeApp.shareInstance().getUserLocations();

        if (userLocations != null) {
            int locationSize = (userLocations.size() > 5 ? 5 : userLocations.size());
            if (mHomeAndCityList != null)
                mHomeAndCityList.clear();

            for (int i = 0; i < locationSize; i++) {
                City mCity = getCityDBService().getCityByCode(userLocations.get(i).getCity());
                String cityNameCnOrEn = AppConfig.shareInstance().getLanguage().equals(AppConfig
                        .LANGUAGE_ZH) ? mCity.getNameZh() : mCity.getNameEn();

                HomeAndCity homeAndCity = new HomeAndCity(userLocations.get(i).getName(),
                        userLocations.get(i).getLocationID(), cityNameCnOrEn);
                mHomeAndCityList.add(homeAndCity);
            }
        }

        mEditHomeListAdapter = new EditHomeListAdapter(mContext, mHomeAndCityList);
        mEditHomeListView.setAdapter(mEditHomeListAdapter);
    }

    private void setupListAdapter() {
        // click image icon to set default home, callback from EditHomeListAdapter
        mEditHomeListAdapter.setSetHomeCallback(new EditHomeListAdapter.SetHomeCallback() {
            @Override
            public void callback() {
                mConfirmButton.setVisibility(View.VISIBLE);
                mLeftButton.setVisibility(View.INVISIBLE);
                mRightButton.setVisibility(View.INVISIBLE);
                mConfirmButton.setText(getString(R.string.set_as_default));
                mIsSetDefaultHome = true;
            }
        });

        // click image icon to remove home
        mEditHomeListAdapter.setRemoveHomeCallback(new EditHomeListAdapter.RemoveHomeCallback() {
            @Override
            public void callback() {
                mConfirmButton.setVisibility(View.INVISIBLE);
                mLeftButton.setVisibility(View.VISIBLE);
                mRightButton.setVisibility(View.VISIBLE);
                mLeftButton.setText(getString(R.string.cancel));
                mRightButton.setText(getString(R.string.remove_this_home));
            }
        });

        // click image icon to rename home
        mEditHomeListAdapter.setRenameHomeCallback(new EditHomeListAdapter.RenameHomeCallback() {
            @Override
            public void callback(final EditText homeNameEditText) {
                mConfirmButton.setVisibility(View.VISIBLE);
                mLeftButton.setVisibility(View.INVISIBLE);
                mRightButton.setVisibility(View.INVISIBLE);
                mConfirmButton.setText(getString(R.string.rename_this_home));
                mIsSetDefaultHome = false;
            }
        });

        // click on the list
        mEditHomeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditHomeListAdapter.setSelectedPosition(position);
                mEditHomeListAdapter.notifyDataSetChanged();

                mConfirmButton.setVisibility(View.INVISIBLE);
                mLeftButton.setVisibility(View.INVISIBLE);
                mRightButton.setVisibility(View.INVISIBLE);

                // store current selected HomeAndCity
                mSelectedHome = (HomeAndCity) parent.getItemAtPosition(position);
            }
        });
    }

    private void processSetDefaultHome() {
        AuthorizeApp.shareInstance().
                setDefaultHomeNumber(mEditHomeListAdapter.getSelectedPosition());

        finishAndQuit();
    }

    private void processRemoveHome() {
        IActivityReceive swapLocationResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                switch (responseResult.getRequestId()) {
                    case DELETE_LOCATION:
                        if (responseResult.isResult()) {
                            if (responseResult.getResponseCode() == StatusCode.OK) {
                                inCaseRemoveDefaultHome();
                                finishAndQuit();
                            } else {
                                errorHandle(responseResult, getString(R.string.enroll_error));
                                backToInitStat();
                            }
                        } else if (responseResult.getResponseCode() == StatusCode.BAD_REQUEST) {
                            MessageBox.createSimpleDialog(UserEditHomeActivity.this, null,
                                    getString(R.string.remove_home_fail), null, null);
                            backToInitStat();
                        } else {
                            errorHandle(responseResult, getString(R.string.enroll_error));
                            backToInitStat();
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        mDialog = ProgressDialog.show(mContext, null, getString(R.string.deleting_home));
        DeleteLocationTask requestTask
                = new DeleteLocationTask(mSelectedHome.getLocationId(), swapLocationResponse);
        requestTask.execute();

    }

    private void processRenameHome(EditText homeNameEditText) {
        IActivityReceive swapLocationResponse = new IActivityReceive() {
            @Override
            public void onReceive(ResponseResult responseResult) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }

                switch (responseResult.getRequestId()) {
                    case SWAP_LOCATION:
                        if (responseResult.isResult()) {
                            if (responseResult.getResponseCode() == StatusCode.OK) {
                                finishAndQuit();
                            } else {
                                errorHandle(responseResult, getString(R.string.enroll_error));
                                backToInitStat();
                            }
                        } else {
                            errorHandle(responseResult, getString(R.string.enroll_error));
                            backToInitStat();
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        if (StringUtil.isEmpty(homeNameEditText.getText().toString())) {
            MessageBox.createSimpleDialog(UserEditHomeActivity.this, null,
                    getString(R.string.home_name_empty), null, null);
            return;
        }

        mDialog = ProgressDialog.show(mContext, null, getString(R.string.renaming));
        SwapLocationRequest swapLocationRequest = new SwapLocationRequest();
        swapLocationRequest.setName(homeNameEditText.getText().toString());
        SwapLocationNameTask requestTask = new SwapLocationNameTask(mSelectedHome.getLocationId(),
                swapLocationRequest, swapLocationResponse);
        requestTask.execute();
    }

    private void finishAndQuit() {
        // send broadcast and notify home page
        Intent intent = new Intent(AirTouchConstants.UPDATE_HOME_NAME);
        sendBroadcast(intent);

        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    private void backToInitStat() {
        mConfirmButton.setVisibility(View.INVISIBLE);
        mLeftButton.setVisibility(View.INVISIBLE);
        mRightButton.setVisibility(View.INVISIBLE);
        mEditHomeListAdapter.setSelectedPosition(-1);
        mEditHomeListAdapter.notifyDataSetChanged();
    }

    /**
     * in case remove default home
     * set the first one as default home
     */
    private void inCaseRemoveDefaultHome() {
        if (mEditHomeListAdapter.getSelectedPosition() ==
                AuthorizeApp.shareInstance().getDefaultHomeNumber()) {
            AuthorizeApp.shareInstance().setDefaultHomeNumber(0);
        }
    }

}
