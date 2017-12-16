package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv3.framework.app.activity.BaseHasBackgroundActivity;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;

/**
 * Created by wuyuan on 10/16/15.
 */
public class OnTravelMinderActivity extends BaseHasBackgroundActivity {

    public final static String IS_NEED_TO_CURRENT = "is_need_to_current";

    private BlurBackgroundView mBlurBackgroundView;

    private TextView mTitleTextView;
    private TextView mOntravelText;
    private boolean isNeedToCurrent = false;

    private TextView mYesTextView;
    private TextView mNoTextView;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.on_travel_reminder);

        initDynamicBackground();

        //set the last gps city with now vaule
        AppConfig.shareInstance().resetLastGpsWithNowVaule();

        mTitleTextView = (TextView)findViewById(R.id.home_name);
        mOntravelText = (TextView)findViewById(R.id.home_location);

        AppConfig appConfig = AppConfig.shareInstance();
        City city = AppConfig.shareInstance().getCityFromDatabase(appConfig.getGpsCityCode());
        if (city.getNameZh() != null && city.getNameEn() != null) {
            String cityText = (AppConfig.shareInstance().getLanguage().equals(AppConfig
                    .LANGUAGE_ZH) ? city.getNameZh() : city.getNameEn());

            mTitleTextView.setText(cityText);
        }
        else{
            mTitleTextView.setText(appConfig.getGpsCityCode());
        }
        mOntravelText.setText(getResources().getText(R.string.ontravel_title));

        mYesTextView = (TextView)findViewById(R.id.ok_text);
        mYesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNeedToCurrent = true;
                finish();
            }
        });
        mNoTextView = (TextView)findViewById(R.id.no_text);
        mNoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNeedToCurrent = false;
                finish();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();

        intent.putExtra(IS_NEED_TO_CURRENT, isNeedToCurrent);
        setResult(RESULT_OK, intent);
        super.finish();

    }


}
