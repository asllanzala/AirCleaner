package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink.EnrollAccessManager;
import com.honeywell.hch.airtouchv3.app.authorize.AuthorizeApp;
import com.honeywell.hch.airtouchv3.app.authorize.controller.UserEditHomeActivity;
import com.honeywell.hch.airtouchv3.app.authorize.controller.UserLoginActivity;
import com.honeywell.hch.airtouchv3.app.dashboard.controller.MainActivity;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.model.xinzhi.Weather;
import com.honeywell.hch.airtouchv3.framework.permission.HPlusPermission;
import com.honeywell.hch.airtouchv3.framework.permission.Permission;
import com.honeywell.hch.airtouchv3.framework.view.MessageBox;
import com.honeywell.hch.airtouchv3.framework.webservice.task.LongTimerRefreshTask;
import com.honeywell.hch.airtouchv3.lib.util.AsyncTaskExecutorUtil;
import com.honeywell.hch.airtouchv3.lib.util.GpsUtil;
import com.honeywell.hch.airtouchv3.lib.util.NetWorkUtil;
import com.honeywell.hch.airtouchv3.lib.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.List;


/**
 * Created by nan.liu on 2/13/15.
 */
public class CurrentGpsFragment extends BaseLocationFragment{
    private static final String TAG = "AirTouchCurrentGPS";


    private View mGpsFragmentView = null;

    private RelativeLayout mGpsSuccessLayout;
    private RelativeLayout mGpsFailedLayout;
    private RelativeLayout mIndiaGpsFailedLayout;
    private TextView mRefreshTextView;
    private TextView mManualAddTextView;
    private Button mEnrollButton;

    private RelativeLayout mFourBtnLayout;

    private boolean isFisrtTime = true;

    private AuthorizeApp mAuthorizeApp;
    private final int MY_PERMISSIONS_REQUEST_WRITE_SETTINGS = 1234;

    private AlertDialog mAlertDialog;

    private HPlusPermission mPlusPermission;

    private TextView mGpsTextView;
    private TextView mGpsTextView2;

    private HPlusPermission mHPlusPermission;

    private LinearLayout mRefreshManualAddLayout;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param activity
     * @return A new instance of fragment HomeCellFragment.
     */
    public static CurrentGpsFragment newInstance(FragmentActivity activity) {
        CurrentGpsFragment fragment = new CurrentGpsFragment();
        fragment.setActivity(activity);
        fragment.getGpsLocation();
        return fragment;
    }

    private void getGpsLocation() {
        mUserLocation = AppManager.shareInstance().getGpsUserLocation();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mGpsFragmentView == null) {
            mGpsFragmentView = inflater.inflate(R.layout.fragment_currentgps, container, false);
            initView(mGpsFragmentView);

        }
        if (mGpsFragmentView.getParent() != null) {
            ViewGroup p = (ViewGroup) mGpsFragmentView.getParent();
            p.removeAllViews();
        }
        return mGpsFragmentView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mGpsSuccessLayout = (RelativeLayout) view.findViewById(R.id.gps_success);
        mGpsFailedLayout = (RelativeLayout) view.findViewById(R.id.gps_not_success);
        mIndiaGpsFailedLayout = (RelativeLayout) view.findViewById(R.id.india_gps_not_success);

        mRefreshManualAddLayout = (LinearLayout) view.findViewById(R.id.okyes_layout);

        mGpsTextView = (TextView) view.findViewById(R.id.title);
        mGpsTextView2 = (TextView) view.findViewById(R.id.content1);

        mRefreshTextView = (TextView) view.findViewById(R.id.refresh_text);
        mManualAddTextView = (TextView) view.findViewById(R.id.manual_add_text);
        mEnrollButton = (Button) view.findViewById(R.id.india_enroll_btn);
        mEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decideWhichActivityGo();
            }
        });

        mFourBtnLayout = (RelativeLayout) view.findViewById(R.id.four_btn_layout);


        mPlusPermission = new HPlusPermission();

        setLocatingStatus();


        isViewInint = true;

        initViewClickListen();

        showCurrentLocationWeatherLayout();

        decideShowCurrentFourBtn();

        startSwitchBackground();

        startGps();

    }

    private void initViewClickListen() {
        mRefreshTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkUtil.isNetworkAvailable(mActivity)) {
                    if (mAlertDialog == null || !mAlertDialog.isShowing()) {
                        mAlertDialog = MessageBox.createSimpleDialog(mActivity, null, mActivity.getString(R.string.no_network), null, null);
                    }
                } else {
                    startCheckGpsPermission();
                }
            }
        });

        mManualAddTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getAuthorizeApp().isLoginSuccess()) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UserEditHomeActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.activity_zoomin, R.anim.activity_zoomout);
                } else {
                    MessageBox.createTwoButtonDialog(getActivity(), null,
                            getString(R.string.not_login), getString(R.string.yes),
                            userLoginButton, getString(R.string.no), null);
                }

            }
        });
    }


    private MessageBox.MyOnClick userLoginButton = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), UserLoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    };
    private MessageBox.MyOnClick userLoginCancelButton = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            getAuthorizeApp().setIsUserWantToEnroll(false);
        }
    };

    private void initGpsLocation() {
        if (isIndiaLogin()) {
            return;
        }

        AppConfig appConfig = AppConfig.shareInstance();
        String cityCode = appConfig.getGpsCityCode();
        mUserLocation = AppManager.shareInstance().getGpsUserLocation();
        if (isViewInint && !mPlusPermission.isLocationPermissionGranted(getFragmentActivity())) {
            //permission deny
            setLocateNoPermission();
        } else {
            if (StringUtil.isEmpty(cityCode) && isViewInint) {
                // locating
                setLocatingStatus();
            } else if (AppConfig.LOCATION_FAIL.equals(cityCode) && isViewInint) {
                //locate failed
                setLocateFailStatus();
            } else if (isViewInint) {
                // locate successfully
                setLocateSuccessStatus(cityCode);

            }
        }

    }


    private void setLocateSuccessStatus(String cityCode) {
        // India version
//        City city = getCityIndiaDBService().getCityByCode(cityCode);
//        if (city != null && city.getNameEn() != null) {
//            getAuthorizeApp().setGPSCountry(AirTouchConstants.INDIA_CODE);
//        }

        mCity = AppConfig.shareInstance().getCityFromDatabase(mUserLocation.getCity());
        mUserLocation.setCity(cityCode);

        // The located city is not in database.
        if (mCity.getNameEn() == null) {
            mCity.setNameZh(cityCode);
            mCity.setNameEn(cityCode);
        }

        if (mCity != null && mCity.getNameZh() != null && mCity.getNameEn() != null) {
            // India version
            if (!getAuthorizeApp().isLoginSuccess() && AppConfig.shareInstance().isLocatedInIndia()) {
                mGpsSuccessLayout.setVisibility(View.GONE);
                mIndiaGpsFailedLayout.setVisibility(View.VISIBLE);
                mGpsFailedLayout.setVisibility(View.GONE);
            } else {
                mGpsSuccessLayout.setVisibility(View.VISIBLE);
                mGpsFailedLayout.setVisibility(View.GONE);
                mIndiaGpsFailedLayout.setVisibility(View.GONE);
                showCurrentLocationWeatherLayout();
            }

            ((MainActivity) getFragmentActivity()).updateMenuItems();

            String cityName = (AppConfig.shareInstance().getLanguage().equals(AppConfig
                    .LANGUAGE_ZH) ? mCity.getNameZh() : mCity.getNameEn());
            mUserLocation.setName(cityName);

            ((MainActivity) getFragmentActivity()).updateTravelSideMenu();

            if (isFisrtTime) {
                LongTimerRefreshTask longTimerRefreshTask = new LongTimerRefreshTask();
                AsyncTaskExecutorUtil.executeAsyncTask(longTimerRefreshTask);
                isFisrtTime = false;
            }
            setHomeNameText(mCity, getFragmentActivity().getResources().getString(R.string.current_location));

            if (getAuthorizeApp().isLoginSuccess()) {
                //if user has logined ,should decide if the home list have contained the location
                ((MainActivity) getFragmentActivity()).updateMainActivityDisplay(((MainActivity) getFragmentActivity()).REFRESH_STATUS);
            }

            //start download task
            AppManager.shareInstance().startDownBackgroundTask(false);

        }

        decideShowCurrentFourBtn();
    }

    private void setLocateNoPermission() {
        mGpsFailedLayout.setVisibility(View.VISIBLE);
        mGpsTextView.setText(getFragmentActivity().getResources().getString(R.string.no_located_permission_1));
        mGpsTextView2.setText(getFragmentActivity().getResources().getString(R.string.no_located_permission_2));
        mRefreshManualAddLayout.setVisibility(View.GONE);

        mGpsSuccessLayout.setVisibility(View.GONE);
        mIndiaGpsFailedLayout.setVisibility(View.GONE);

        setHomeNameText(getFragmentActivity().getResources().getString(R.string.no_located_permission_title), getFragmentActivity().getResources().getString(R.string.current_location));


        mUserLocation.setName(AppConfig.LOCATION_FAIL);

        ((MainActivity) getFragmentActivity()).updateTravelSideMenu();

    }

    private void setLocateFailStatus() {
        mGpsFailedLayout.setVisibility(View.VISIBLE);
        mGpsTextView.setText(getFragmentActivity().getResources().getString(R.string.gps_fail_content1));
        mGpsTextView2.setText(getFragmentActivity().getResources().getString(R.string.gps_fail_content2));
        mRefreshManualAddLayout.setVisibility(View.VISIBLE);

        mGpsSuccessLayout.setVisibility(View.GONE);
        mIndiaGpsFailedLayout.setVisibility(View.GONE);


        setHomeNameText(getFragmentActivity().getResources().getString(R.string.enroll_gps_fail), getFragmentActivity().getResources().getString(R.string.current_location));

        mUserLocation.setName(AppConfig.LOCATION_FAIL);

        ((MainActivity) getFragmentActivity()).updateTravelSideMenu();

    }

    private void setLocatingStatus() {
        mGpsSuccessLayout.setVisibility(View.VISIBLE);
        mWeatherChartView.setVisibility(View.INVISIBLE);
        mFourBtnLayout.setVisibility(View.INVISIBLE);
        mGpsFailedLayout.setVisibility(View.GONE);
        mIndiaGpsFailedLayout.setVisibility(View.GONE);

        mHomeNameTextView.setText(getFragmentActivity().getResources().getString(R.string.current_location));
        mCityNameTextView.setText("(" + getFragmentActivity().getString(R.string.enroll_gps_on) + ")");
        mCityNameTextView.startAnimation(AnimationUtils.loadAnimation(getFragmentActivity(),
                R.anim.window_alpha));

        ((MainActivity) getFragmentActivity()).updateTravelSideMenu();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.TAG = TAG;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void updateGpsCurrentLocation() {
        initGpsLocation();
    }


    @Override
    public void bindData2View() {
        decideShowCurrentFourBtn();
    }

    public void decideShowCurrentFourBtn() {
        if (isViewInint) {
            List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
            if ((userLocationDataList != null && userLocationDataList.size() > 0)
                    || StringUtil.isEmpty(AppConfig.shareInstance().getGpsCityCode())) {
                //hide the emotion button
                mFourBtnLayout.setVisibility(View.GONE);
            } else {
                mFourBtnLayout.setVisibility(View.VISIBLE);
            }

            // India version
            if (AppConfig.shareInstance().isLocatedInIndia()) {
                mFourBtnLayout.setVisibility(View.GONE);
            }
        }

    }


    @Subscribe
    public void onLocationDataRefreshReceived(LongTimerRefreshTask.WeatherDataLoadedEvent
                                                      locationDataLoadedEvent) {
        if (mUserLocation == null) {
            return;
        }
        if (StringUtil.isEmpty(locationDataLoadedEvent.getCity())) {
            mWeatherChartView.setWeather(mUserLocation.getCityWeatherData());
        } else if (locationDataLoadedEvent.getCity().equals(mUserLocation.getCity())) {
            mWeatherChartView.setHourlyWeather(mUserLocation.getCityWeatherData());
        }
//        setWeatherIcon();
    }

    @Override
    protected void handleWeatherData(Weather weatherData) {
        super.handleWeatherData(weatherData);

        showCurrentLocationWeatherLayout();
    }

    private void decideWhichActivityGo() {
        if (!getAuthorizeApp().isLoginSuccess()) {
            getAuthorizeApp().setIsUserWantToEnroll(true);
            MessageBox.createTwoButtonDialog(getActivity(), null,
                    getString(R.string.not_login), getString(R.string.yes),
                    userLoginButton, getString(R.string.no), userLoginCancelButton);
        } else {
            EnrollAccessManager.startIntent(getFragmentActivity(), "");
        }
    }

    // India version
    private boolean isIndiaLogin() {
        if (getAuthorizeApp().isLoginSuccess()) {
            if (AppConfig.shareInstance().isIndiaAccount() && isViewInint) {
                mGpsSuccessLayout.setVisibility(View.GONE);
                mGpsFailedLayout.setVisibility(View.GONE);
                mIndiaGpsFailedLayout.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return false;
    }

    private AuthorizeApp getAuthorizeApp() {

        if (mAuthorizeApp == null) {
            mAuthorizeApp = AppManager.shareInstance().getAuthorizeApp();
        }
        return mAuthorizeApp;
    }

    public void startCheckGpsPermission() {
        mHPlusPermission = new HPlusPermission((MainActivity)getFragmentActivity());
        mHPlusPermission.checkAndRequestPermission(Permission.PermissionCodes.LOCATION_SERVICE_REQUEST_CODE, getFragmentActivity());
    }

    public void startGps(){
        GpsUtil mGpsUtil = new GpsUtil(HPlusApplication.getInstance().getCityChinaDBService(), HPlusApplication.getInstance().getCityIndiaDBService());

        try {
            mGpsUtil.initGps();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void showCurrentLocationWeatherLayout() {
        if (!StringUtil.isEmpty(AppConfig.shareInstance().getGpsCityCode())) {
            showWeatherLayout();
        }
    }

}
