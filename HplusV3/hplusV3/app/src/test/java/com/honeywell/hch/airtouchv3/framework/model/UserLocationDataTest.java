package com.honeywell.hch.airtouchv3.framework.model;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

/**
 * Created by wuyuan on 10/12/15.
 */
@RunWith(RobolectricTestRunner.class)
public class UserLocationDataTest {

    public static final int WATER_TYPE = 10000;

    private UserLocationData mUserLocationData;

    protected HPlusApplication application = Mockito.mock(HPlusApplication.class, Mockito.RETURNS_DEEP_STUBS);


    @Before
    public void setup() {
        Mockito.when(application.getApplicationContext()).thenReturn(application);
        HPlusApplication.setHPlusApplication(application);

        mUserLocationData = new UserLocationData();
        mUserLocationData.setLocationID(111);
        mUserLocationData.setCity("CHSH00000");
        mUserLocationData.setName("Shanghai");

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceID(1111);
        deviceInfo.setmDeviceType(AirTouchConstants.AIRTOUCHS_TYPE);
        AirTouchSeriesDevice airTouchSeriesDevice = new AirTouchSeriesDevice();
        airTouchSeriesDevice.setDeviceInfo(deviceInfo);
        RunStatus runStatus = new RunStatus();
        runStatus.setmPM25Value(39);
        airTouchSeriesDevice.setDeviceRunStatus(runStatus);
        mUserLocationData.addHomeDeviceItemToList(airTouchSeriesDevice);

        DeviceInfo deviceInfo2 = new DeviceInfo();
        deviceInfo2.setDeviceID(2222);
        deviceInfo2.setmDeviceType(AirTouchConstants.AIRTOUCHP_TYPE);
        AirTouchSeriesDevice airTouchSeriesDevice2 = new AirTouchSeriesDevice();
        airTouchSeriesDevice2.setDeviceInfo(deviceInfo2);
        RunStatus runStatus2 = new RunStatus();
        runStatus2.setmPM25Value(121);
        airTouchSeriesDevice2.setDeviceRunStatus(runStatus2);
        mUserLocationData.addHomeDeviceItemToList(airTouchSeriesDevice2);


        DeviceInfo deviceInfo3 = new DeviceInfo();
        deviceInfo3.setDeviceID(3333);
        deviceInfo3.setmDeviceType(AirTouchConstants.AIRTOUCHS_TYPE);
        AirTouchSeriesDevice airTouchSeriesDevice3 = new AirTouchSeriesDevice();
        airTouchSeriesDevice3.setDeviceInfo(deviceInfo3);
        RunStatus runStatus3 = new RunStatus();
        runStatus3.setmPM25Value(110);
        airTouchSeriesDevice3.setDeviceRunStatus(runStatus3);
        mUserLocationData.addHomeDeviceItemToList(airTouchSeriesDevice3);

        DeviceInfo deviceInfo4 = new DeviceInfo();
        deviceInfo4.setDeviceID(4444);
        deviceInfo4.setmDeviceType(WATER_TYPE);
        HomeDevice waterDevice = new HomeDevice();
        waterDevice.setDeviceInfo(deviceInfo4);
        mUserLocationData.addHomeDeviceItemToList(waterDevice);

    }

    @Test
    public void testGetWorstDevice(){
        AirTouchSeriesDevice worstDevice = mUserLocationData.getDefaultDevice();
        Assert.assertEquals(2222, worstDevice.getDeviceInfo().getDeviceID());

        Assert.assertEquals(121, worstDevice.getDeviceRunStatus().getmPM25Value());
    }


    @Test
    public void testGetAirTouchSeriesList(){
        ArrayList<AirTouchSeriesDevice> airTouchSeriesDevicesList = mUserLocationData.getAirTouchSeriesList();
        Assert.assertEquals(3, airTouchSeriesDevicesList.size());
    }

    @Test
    public void testGetHomeDeviceWithDeviceId(){
        HomeDevice homeDevice = mUserLocationData.getHomeDeviceWithDeviceId(1111);
        boolean isInstatnceof = homeDevice instanceof AirTouchSeriesDevice;
        Assert.assertEquals(true, isInstatnceof);
        Assert.assertEquals(AirTouchConstants.AIRTOUCHS_TYPE, homeDevice.getDeviceType());

        HomeDevice homeDevice2 = mUserLocationData.getHomeDeviceWithDeviceId(4444);
        boolean isInstatnceof2 = homeDevice2 instanceof AirTouchSeriesDevice;
        Assert.assertEquals(false, isInstatnceof2);

        Assert.assertEquals(WATER_TYPE, homeDevice2.getDeviceType());
    }
}
