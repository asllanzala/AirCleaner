package com.honeywell.hch.airtouchv3.app.dashboard.controller.location;

import com.honeywell.hch.airtouchv3.app.dashboard.controller.MainActivity;
import com.honeywell.hch.airtouchv3.framework.app.AppManager;
import com.honeywell.hch.airtouchv3.framework.config.AppConfig;
import com.honeywell.hch.airtouchv3.framework.config.UserConfig;
import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;
import com.honeywell.hch.airtouchv3.framework.webservice.task.LongTimerRefreshTask;

import java.util.List;

/**
 * Created by wuyuan on 10/15/15.
 * is used for deal the current location logic.Only used by MainActivity
 */
public class CurrentLocationLogic {

    public static final int NO_FOUND_LOCATION_ID = -1;
    private static final int LOGIN_STATUS = 2;
    private MainActivity mMainActivity;
    private AppConfig mAppConfig;

    private boolean isHasCurrentLocation = true;

    public CurrentLocationLogic(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mAppConfig = AppConfig.shareInstance();
    }


    public boolean isHasHome() {
        List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
        if (userLocationDataList != null && userLocationDataList.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean isHasSameCityInLocationList() {
        if (isHasCurrentLocation) {
            String cityCode = mAppConfig.getGpsCityCode();
            List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
            if (userLocationDataList != null && userLocationDataList.size() > 0) {
                for (UserLocationData userLocationData : userLocationDataList) {
                    if (cityCode != null && cityCode.equalsIgnoreCase(userLocationData.getCity())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * the condition of on travelling :
     * 1. gps success this time
     * 2. it's different between the city of this time and last time
     * 3. has home list
     * 4. the current city does not contain in the home list
     *
     * @return
     */
    public boolean isOnTravelling() {
        if (AppConfig.shareInstance().isDifferent()
                && isHasHome()
                && !isHasSameCityInLocationList()) {
            return true;
        }
        return false;
    }

    public void addCurrentLocation(boolean isNeedGotoCurrentLocation) {
        isHasCurrentLocation = true;
        CurrentGpsFragment currentGpsFragment = CurrentGpsFragment.newInstance(mMainActivity);
        mMainActivity.getHomeList().add(0, currentGpsFragment);
        mMainActivity.notifyViewPagerChange();

        int gotoIndex = !isNeedGotoCurrentLocation ? mMainActivity.getCurrentHomeIndex() + 1 : 0;
        gotoTheSpecifyHome(gotoIndex);

    }

    public void addCurrentLocation() {
        isHasCurrentLocation = true;
        CurrentGpsFragment currentGpsFragment = CurrentGpsFragment.newInstance(mMainActivity);
        mMainActivity.getHomeList().add(0, currentGpsFragment);
    }


    public void loadHomeList(int status) {
        List<UserLocationData> userLocations = AppManager.shareInstance().getUserLocationDataList();

        if (userLocations == null || userLocations.size() == 0) {
            clearAllCellExceptionGpsCell(status);
        } else {
            updateHomeListAccountResult(userLocations, status);
        }
        mMainActivity.setViewPagerGotoCurrentItem(mMainActivity.getCurrentHomeIndex());
        updateHomeCellAfterLoadHomeList();
    }


    private void updateHomeListAccountResult(List<UserLocationData> userLocations,int status) {

        //when the home list change in another phone, we should keep this phone's home not skipping to another home
        int currentLocationId = mMainActivity.getCurrentHomeLocationId();

        //if userLocation size is greater than home list size,generate new fragment and add in list
        int homeListSize = isHasCurrentLocation ? mMainActivity.getHomeList().size() - 1 : mMainActivity.getHomeList().size();
        boolean isHaveSameCity = isHasSameCityInLocationList();

        if (userLocations.size() != homeListSize || isHaveSameCity){
            mMainActivity.getHomePageAdapter().logoutClear();
            mMainActivity.getHomeList().clear();

            int firstHomdeIndex = 0;

            // India version
            if (!isHaveSameCity && !AppConfig.shareInstance().isIndiaAccount()) {
                addCurrentLocation();
                firstHomdeIndex = 1;
            } else {
                isHasCurrentLocation = false;

                //no current location ,so need to set the different flag as false.
                //need not to show the on travel reminder
                mAppConfig.setIsDifferent(false);
            }

            for (int i = 0; i < userLocations.size(); i++) {
                BaseLocationFragment homePageFragment = HomeCellFragment.newInstance(mMainActivity, firstHomdeIndex + i,i);
                mMainActivity.getHomeList().add(homePageFragment);
            }
            mMainActivity.setViewPagerScroll(true);
            mMainActivity.getHomePageAdapter().notifyDataSetChanged();
        }
        if (userLocations.size() > homeListSize){
            //We should refresh the weather data when adding a home
            LongTimerRefreshTask longTimerRefreshTask = new LongTimerRefreshTask();
            longTimerRefreshTask.execute();
        }

         setCurrentItemIndex(status,currentLocationId);

    }

    private void setCurrentItemIndex(int status,int currentLocationId){
        if (status == LOGIN_STATUS){
            UserConfig mUserConfig = new UserConfig(mMainActivity);
            mUserConfig.loadDefaultHome();
            mUserConfig.loadDefaultHomeId();
            int defaultHomeNumber = mMainActivity.getAuthorizeApp().getDefaultHomeNumber();
            if (defaultHomeNumber < mMainActivity.getHomeList().size()) {
                int curent = isHasCurrentLocation ? defaultHomeNumber + 1 : defaultHomeNumber;
                mMainActivity.setCurrentHomeIndex(curent);
            }
        }
        else{
            if (currentLocationId != NO_FOUND_LOCATION_ID){
                getLocationIndexWithLocationId(currentLocationId);
            }
        }
        if (mMainActivity.getCurrentHomeIndex() >= mMainActivity.getHomeList().size()){
            mMainActivity.setCurrentHomeIndex(0);
        }
    }


    public void updateHomeCellAfterLoadHomeList(){
        updateCurrentHomeData(mMainActivity.getCurrentHomeIndex());
    }

    public void updateCurrentHomeData(int homeIndex){
        List<UserLocationData> userLocations = AppManager.shareInstance().getUserLocationDataList();
        if (homeIndex < mMainActivity.getHomeList().size()){
            BaseLocationFragment baseLocationFragment = mMainActivity.getHomeList().get(homeIndex);
            if (baseLocationFragment != null){
                if (baseLocationFragment instanceof CurrentGpsFragment){
                    baseLocationFragment.updateHomeCellData(0,AppManager.shareInstance().getGpsUserLocation());
                }
                else{
                    if (isHasCurrentLocation){
                        baseLocationFragment.updateHomeCellData(homeIndex,userLocations.get(homeIndex - 1));

                    }
                    else{
                        baseLocationFragment.updateHomeCellData(homeIndex,userLocations.get(homeIndex));
                    }
                }
            }

        }
    }

    private void clearAllCellExceptionGpsCell(int status) {

        if (mMainActivity.getHomePageAdapter().getCount() > 1 || mMainActivity.getHomeList().size() > 1 ||
                (mMainActivity.getHomePageAdapter().getCount() == 1 && !(mMainActivity.getHomePageAdapter().getItem(0) instanceof CurrentGpsFragment))){
            mMainActivity.getHomePageAdapter().logoutClear();
            mMainActivity.getHomeList().clear();
            addCurrentLocation();
        }
        if (status == LOGIN_STATUS){
            mMainActivity.updateCurrentLocationFragmentShow();
        }
        mMainActivity.setViewPagerScroll(true);
        mMainActivity.setCurrentHomeIndex(0);
        mMainActivity.getHomePageAdapter().notifyDataSetChanged();
    }

    public void gotoTheSpecifyHome(int homeIndex) {
        if (homeIndex >= mMainActivity.getHomeList().size()){
            homeIndex = 0;
        }
        mMainActivity.setCurrentHomeIndex(homeIndex);
        mMainActivity.setViewPagerGotoCurrentItem(homeIndex);
    }

    public void updateHomeWithLocationId(int locationId){
        List<UserLocationData> userLocations = AppManager.shareInstance().getUserLocationDataList();

        if (userLocations != null) {
            for (int i = 0; i < userLocations.size(); i++) {
                if (userLocations.get(i).getLocationID() == locationId) {
                    int curentHomeIndex = isHasCurrentLocation ? i+1 : i;
                    mMainActivity.setCurrentHomeIndex(curentHomeIndex);

                    UserLocationData mUserLocation = userLocations.get(i);
                    /**
                     * TO-DO
                     */
                    mMainActivity.getHomeList().get(curentHomeIndex).updateHomeCellData(curentHomeIndex, mUserLocation);
                    mMainActivity.setViewPagerGotoCurrentItem(curentHomeIndex);
                    break;
                }
            }
        }
    }

    public boolean iSHasCurrentLocation(){
        return isHasCurrentLocation;
    }

    public void setDefaultHome(){

        goToTheDefaultHome();
        mMainActivity.updateMenuItems();
    }

    public void goToTheDefaultHome(){
        if (mMainActivity.getHomeList().size() <= 1 && isHasCurrentLocation){
            return;
        }

        int defaultHomeLocalId = AppManager.shareInstance().getAuthorizeApp().getDefaultHomeLocalId();

        AppManager.shareInstance().getAuthorizeApp().
                setDefaultHomeNumber(getLocationIndexWithLocationId(defaultHomeLocalId));

    }


    private int getLocationIndexWithLocationId(int homeLocalId ){
        List<UserLocationData> userLocationDataList = AppManager.shareInstance().getUserLocationDataList();
        int homeIndex = mMainActivity.getCurrentHomeIndex();
        int size = userLocationDataList.size();
        if (userLocationDataList != null && size > 0){
            for (int i = 0 ;i < size;i++){
                UserLocationData userLocationData = userLocationDataList.get(i);
                if (userLocationData != null && userLocationData.getLocationID() == homeLocalId){
                    homeIndex = i;
                    if (isHasCurrentLocation){
                        homeIndex = homeIndex + 1;
                    }
                    break;
                }
            }

        }
        mMainActivity.setViewPagerGotoCurrentItem(homeIndex);
        return homeIndex;
    }

}
