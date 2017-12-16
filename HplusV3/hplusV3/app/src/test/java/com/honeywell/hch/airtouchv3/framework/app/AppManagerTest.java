package com.honeywell.hch.airtouchv3.framework.app;

import com.honeywell.hch.airtouchv3.framework.model.UserLocationData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by wuyuan on 10/12/15.
 */
@RunWith(RobolectricTestRunner.class)
public class AppManagerTest {

    private UserLocationData mUserLocationData1;
    private UserLocationData mUserLocationData2;
    private UserLocationData mUserLocationData3;
    private UserLocationData mUserLocationData4;


    @Before
    public void setup() {

//        mUserLocationData1 = new UserLocationData();
//        mUserLocationData1.setLocationID(111);
//        mUserLocationData1.setCity("CHSH00000");
//        mUserLocationData1.setName("Shanghai");
//
//        DeviceInfo deviceInfo = new DeviceInfo();
//        deviceInfo.setDeviceID(1111);
//        deviceInfo.setmDeviceType(AirTouchConstants.AIRTOUCHS_TYPE);
//        AirTouchSeriesDevice airTouchSeriesDevice = new AirTouchSeriesDevice();
//        airTouchSeriesDevice.setDeviceInfo(deviceInfo);
//        RunStatus runStatus = new RunStatus();
//        runStatus.setmPM25Value(39);
//        airTouchSeriesDevice.setDeviceRunStatus(runStatus);
//        mUserLocationData1.addHomeDeviceItemToList(airTouchSeriesDevice);
//
//        DeviceInfo deviceInfo2 = new DeviceInfo();
//        deviceInfo2.setDeviceID(2222);
//        deviceInfo2.setmDeviceType(AirTouchConstants.AIRTOUCHP_TYPE);
//        AirTouchSeriesDevice airTouchSeriesDevice2 = new AirTouchSeriesDevice();
//        airTouchSeriesDevice2.setDeviceInfo(deviceInfo2);
//        RunStatus runStatus2 = new RunStatus();
//        runStatus2.setmPM25Value(121);
//        airTouchSeriesDevice2.setDeviceRunStatus(runStatus2);
//        mUserLocationData1.addHomeDeviceItemToList(airTouchSeriesDevice2);
//
//
//        DeviceInfo deviceInfo3 = new DeviceInfo();
//        deviceInfo3.setDeviceID(3333);
//        deviceInfo3.setmDeviceType(AirTouchConstants.AIRTOUCHS_TYPE);
//        AirTouchSeriesDevice airTouchSeriesDevice3 = new AirTouchSeriesDevice();
//        airTouchSeriesDevice3.setDeviceInfo(deviceInfo3);
//        RunStatus runStatus3 = new RunStatus();
//        runStatus3.setmPM25Value(110);
//        airTouchSeriesDevice3.setDeviceRunStatus(runStatus3);
//        mUserLocationData1.addHomeDeviceItemToList(airTouchSeriesDevice3);
//
//
//        mUserLocationData2 = new UserLocationData();
//        mUserLocationData2.setLocationID(222);
//        mUserLocationData2.setCity("CHSH00001");
//        mUserLocationData2.setName("TianJing");
//
//
//        mUserLocationData3 = new UserLocationData();
//        mUserLocationData3.setLocationID(333);
//        mUserLocationData3.setCity("CHSH00003");
//        mUserLocationData3.setName("Nanjing");

    }

    @Test
    public void testDddDeviceDataFromGetLocationAPI(){

//        UserLocation userLocation = new UserLocation();
//        userLocation.setLocationID(444);
//        userLocation.setName("Hainan");
//        userLocation.setCity("CHSH00006");
//
//        DeviceInfo deviceInfo = new DeviceInfo();
//        deviceInfo.setmDeviceType(AirTouchConstants.AIRTOUCHS_TYPE);
//        ArrayList<DeviceInfo> deviceInfosList = new ArrayList<DeviceInfo>();
//        deviceInfosList.add(deviceInfo);
//        userLocation.setDeviceInfo(deviceInfosList);

//        AppManager.shareInstance().addDeviceDataFromGetLocationAPI(userLocation);
//
//        Assert.assertEquals(1, AppManager.shareInstance().getUserLocationDataList().size());
//
//        UserLocationData testOne = AppManager.shareInstance().getUserLocationDataList().get(0);
//        Assert.assertEquals(444, testOne.getLocationID());


    }


    @Test
    public void testGetLocationWithId(){
//        ArrayList<UserLocationData> mList = new ArrayList<>();
//        mList.add(mUserLocationData1);
//        mList.add(mUserLocationData2);
//        AppManager.shareInstance().setUserLocationDataList(mList);
//
//        Assert.assertEquals("CHSH00000", AppManager.shareInstance().getLocationWithId(111).getCity());
//
//        Assert.assertEquals(null, AppManager.shareInstance().getLocationWithId(555));
    }
}
