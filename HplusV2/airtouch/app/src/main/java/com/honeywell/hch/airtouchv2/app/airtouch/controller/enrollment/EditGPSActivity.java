package com.honeywell.hch.airtouchv2.app.airtouch.controller.enrollment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.framework.app.activity.BaseActivity;
import com.honeywell.hch.airtouchv2.framework.config.AppConfig;
import com.honeywell.hch.airtouchv2.framework.database.CityDBService;
import com.honeywell.hch.airtouchv2.app.airtouch.model.dbmodel.City;
import com.honeywell.hch.airtouchv2.lib.util.StringUtil;
import com.honeywell.hch.airtouchv2.framework.view.SideBar;

import java.util.ArrayList;

/**
 * Created by nan.liu on 2/2/15.
 */
public class EditGPSActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "AirTouchEditGPS";

    private Button confirmButton = null;
    private ImageButton backButton = null;
    private ListView cityListView = null;
    private EditText searchEditText;
    private TextView gpsAddressTextView = null;
    private SideBar citiesSidebar = null;

    private WindowManager mWindowManager = null;
    private InputMethodManager imm = null;

    protected ArrayList<City> mCitiesList = new ArrayList<>();
    public static SparseArray<String> p2s = new SparseArray<>();
    private CitiesAdapter citiesAdapter = null;
    private CityDBService mCityDBService = null;
    private City currentGPSCity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gps);

        Intent intent = getIntent();
        currentGPSCity = (City) intent.getSerializableExtra("currentGPS");

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        initView();
        View.OnTouchListener touchListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        };
        cityListView.setOnTouchListener(touchListener);
        OnScrollListener scrollListener = new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        };
        cityListView.setOnScrollListener(scrollListener);
        confirmButton.setOnClickListener(this);
        searchEditText = (EditText) findViewById(R.id.search_editText);
        TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        };
        searchEditText.setOnEditorActionListener(editorActionListener);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCitiesListFromSearch(true);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCityDBService = new CityDBService(this);
        mCitiesList = getAllCities();
        citiesAdapter = new CitiesAdapter(this, false);
        if (currentGPSCity != null) {
            citiesAdapter.setCurrentCityKey(currentGPSCity.getNameEn() + currentGPSCity.getCode());
        }
        refreshList();
    }

    private void updateCitiesListFromSearch(boolean hideSoftInput) {
        String searchCityName = searchEditText.getText().toString().trim();
//        if (imm.isActive() && hideSoftInput) {
//            imm.hideSoftInputFromWindow(findViewById(R.id.search_editText)
//                    .getWindowToken(), 0);
//        }
        if (!StringUtil.isEmpty(searchCityName)) {
            mCitiesList = mCityDBService.getCitiesByKey(searchCityName);
            citiesSidebar.setVisibility(View.GONE);
            if (null == mCitiesList)
                return;
        } else {
            mCitiesList = getAllCities();
            citiesSidebar.setVisibility(View.VISIBLE);
        }
        citiesAdapter = new CitiesAdapter(this, !StringUtil.isEmpty(searchCityName));
        refreshList();
    }

    private void refreshList() {
        citiesAdapter.setData(mCitiesList);
        citiesAdapter.setPreferenceView(confirmButton);
        cityListView.setAdapter(citiesAdapter);
    }

    public void initView() {
        gpsAddressTextView = (TextView) findViewById(R.id.gps_address);
        if (currentGPSCity != null) {
            gpsAddressTextView.setText(AppConfig.shareInstance().getLanguage().equals(AppConfig.LANGUAGE_ZH) ?
                    currentGPSCity.getNameZh() : currentGPSCity.getNameEn());
        }
        citiesSidebar = (SideBar) findViewById(R.id.sidebar);
        backButton = (ImageButton) findViewById(R.id.back_btn);
        backButton.setOnClickListener(this);
        confirmButton = (Button) findViewById(R.id.ok_btn);
        confirmButton.setOnClickListener(this);
        cityListView = (ListView) findViewById(R.id.city_listView);
        citiesSidebar.setListView(cityListView);
    }

    public ArrayList<City> getAllCities() {
        return mCityDBService.findAllCities();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.ok_btn:
                City city = citiesAdapter.getSelectedCity();
                Intent intent = new Intent();
                intent.putExtra("city", city);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

}

