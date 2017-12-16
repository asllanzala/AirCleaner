package com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.enrollment.activity.EnrollBaseActivity;
import com.honeywell.hch.airtouchv2.framework.view.MessageBox;
import com.honeywell.hch.airtouchv2.framework.webservice.StatusCode;
import com.honeywell.hch.airtouchv2.framework.enrollment.controls.EnrollmentClient;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.DIYInstallationState;
import com.honeywell.hch.airtouchv2.framework.enrollment.models.WAPIRouter;
import com.honeywell.hch.airtouchv2.lib.http.HTTPRequestResponse;
import com.honeywell.hch.airtouchv2.lib.http.IReceiveResponse;
import com.honeywell.hch.airtouchv2.lib.http.RequestID;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.honeywell.hch.airtouchv2.lib.util.UmengUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Enrollment Step 3 - SmartPhone ask Air Touch to scan surrounding's SSID.
 * If there are too many SSID so that user can not find what he want,
 * press rescan to get SSID list again.
 */
public class EnrollConnectWifiActivity extends EnrollBaseActivity {

    private static final String TAG = "AirTouchEnrollConnectWifi";

    private Button rescanButton;
    private Context mContext;
    private static ProgressDialog mDialog;
//    private View rescanPromptView;
//    private ImageView loadingImageView;
    private TextView titileTextView;

    private ArrayList<WAPIRouter> mWAPIRouters;
    private ListView mNetworkList;
    private NetworkListAdapter networkListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollconnectwifi);

        super.TAG = TAG;

        mContext = EnrollConnectWifiActivity.this;
        titileTextView = (TextView) findViewById(R.id.connect_wifi_tv);
        rescanButton = (Button) findViewById(R.id.rescan);
        rescanButton.setOnClickListener(rescanOnClick);
//        rescanPromptView = findViewById(R.id.rescan_prompt_view);
//        loadingImageView = (ImageView) findViewById(R.id.loading_image);
        mNetworkList = (ListView) findViewById(R.id.wifi_list);
        mNetworkList.setOnItemClickListener(mScanResultClickListener);
        networkListAdapter = new NetworkListAdapter(EnrollConnectWifiActivity.this);

        if (AppConfig.isDebugMode) {
            titileTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(EnrollConnectWifiActivity.this, EnrollWifiPasswordActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadWAPIRouters();
    }

    private class NetworkListAdapter extends ArrayAdapter<WAPIRouter> {

        public NetworkListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public WAPIRouter getItem(int position) {
            return mWAPIRouters.get(position);
        }

        @Override
        public int getCount() {
            if (mWAPIRouters == null) {
                return 0;
            }
            return mWAPIRouters.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_network,
                        parent, false);
            }

            WAPIRouter wapiRouter = getItem(position);

            if (wapiRouter.getSSID() != null) {
                TextView ssidTextView = (TextView) convertView
                        .findViewById(R.id.list_item_network_text);
                ssidTextView.setText(wapiRouter.getSSID());
            }

            Drawable drawable = null;
            if (wapiRouter.isLocked()) {
                drawable = getContext().getResources().getDrawable(R.drawable.wifi_lock);
            }

            ImageView image = (ImageView) convertView
                    .findViewById(R.id.list_item_network_lock_image);
            image.setImageDrawable(drawable);
            return convertView;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            MessageBox.createTwoButtonDialog(this, null,
                    getString(R.string.enroll_quit), getString(R.string.no), null,
                    getString(R.string.yes), quitEnroll);
        }

        return false;
    }

    OnClickListener rescanOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mWAPIRouters != null) {
                mWAPIRouters.removeAll(mWAPIRouters);
                networkListAdapter.notifyDataSetChanged();
            }
            loadWAPIRouters();

            //start rotation
            Animation operatingAnim = AnimationUtils.loadAnimation(EnrollConnectWifiActivity.this, R.anim.enroll_rescan_rotate);
            LinearInterpolator lin = new LinearInterpolator();
            operatingAnim.setInterpolator(lin);
            if (operatingAnim != null) {
                rescanButton.startAnimation(operatingAnim);
            }
        }

    };

    private OnItemClickListener mScanResultClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WAPIRouter wapiRouter = (WAPIRouter) parent.getItemAtPosition(position);
            DIYInstallationState.setWAPIRouter(wapiRouter);
            Intent i = new Intent();
            i.setClass(EnrollConnectWifiActivity.this, EnrollWifiPasswordActivity.class);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        }
    };

    private void loadWAPIRouters() {
        IReceiveResponse getWAPIRoutersResponse = new IReceiveResponse() {
            @Override
            public void onReceive(HTTPRequestResponse httpRequestResponse) {
                if (mWAPIRouters != null) {
                    mWAPIRouters.clear();
                } else {
                    mWAPIRouters = new ArrayList<>();
                }
                rescanButton.clearAnimation();
                if (httpRequestResponse.getStatusCode() != StatusCode.OK) {
                    MessageBox.createSimpleDialog(EnrollConnectWifiActivity.this, null,
                            getResources().getString(R.string.no_network), null, null);

                    WAPIRouter otherRouter = new WAPIRouter();
                    otherRouter.setSSID(getString(R.string.enroll_other));
                    mWAPIRouters.add(otherRouter);
                    mNetworkList.setAdapter(networkListAdapter);
                    onFinish();
                    return;
                }
                if (!StringUtil.isEmpty(httpRequestResponse.getData())) {
                    List<WAPIRouter> wapiRouters = null;
                    Type wapiRouterType = new TypeToken<List<WAPIRouter>>() {
                    }.getType();
                    try {
                        JSONObject responseJSON = new JSONObject(httpRequestResponse.getData());
                        if (responseJSON.has("Routers")) {
                            String response = responseJSON.getJSONArray("Routers").toString();
                            wapiRouters = new Gson().fromJson(response,
                                    wapiRouterType);
                        }
                        if (wapiRouters != null) {
                            HashMap<String, WAPIRouter> routerHashMap = new HashMap<>();
                            for (Iterator iterator = wapiRouters.iterator(); iterator.hasNext();) {
                                WAPIRouter element = (WAPIRouter) iterator.next();
                                routerHashMap.put(element.getSSID(), element);
                            }
                            Iterator it = routerHashMap.keySet().iterator();
                            while (it.hasNext()) {
                                String key = it.next().toString();
                                mWAPIRouters.add(routerHashMap.get(key));
                            }
                        }
                        Collections.sort(mWAPIRouters);

                        WAPIRouter otherRouter = new WAPIRouter();
                        otherRouter.setSSID(getString(R.string.enroll_other));
                        mWAPIRouters.add(otherRouter);

                        mNetworkList.setAdapter(networkListAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onFinish();
            }
        };
        EnrollmentClient.sharedInstance()
                .getWAPIRouters(RequestID.GET_ROUTER, getWAPIRoutersResponse);
        disableRescanButton();

        mDialog = ProgressDialog.show(mContext, null, getString(R.string.enroll_scanning));
//        rescanPromptView.setVisibility(View.VISIBLE);
//        AnimationDrawable anim = (AnimationDrawable) loadingImageView.getBackground();
//        anim.start();
    }

    protected void onFinish() {
        if (mDialog != null)
            mDialog.dismiss();

        enableRescanButton();
    }

    private void disableRescanButton() {
        rescanButton.setClickable(false);
        rescanButton.setTextColor(getResources().getColor(R.color.enroll_light_grey));
    }

    private void enableRescanButton() {
//        rescanPromptView.setVisibility(View.INVISIBLE);
        rescanButton.setClickable(true);
        rescanButton.setTextColor(getResources().getColor(R.color.enroll_text_grey));
    }

    private MessageBox.MyOnClick quitEnroll = new MessageBox.MyOnClick() {
        @Override
        public void onClick(View v) {
            UmengUtil.onEvent(EnrollConnectWifiActivity.this,
                    UmengUtil.EventType.ENROLL_CANCEL.toString(), "page3");

            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    };
}