package com.honeywell.hch.airtouchv3.framework.model;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.framework.global.AirTouchConstants;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by wuyuan on 9/24/15.
 */
@RunWith(RobolectricTestRunner.class)
public class AirTouchSeriesDeviceTest {

    private static final int AIRTOUCH_S_CONTROL_POINT_TOTAL = 7;

    private static final int AIRTOUCH_P_CONTROL_POINT_TOTAL = 9;

    private static final int AIRTOUCH_CONTROL_LEVEL_POINT = 2;

    private HomeDevice mAirTouchSDevice;

    private HomeDevice mAirTouchPDevice;

    protected HPlusApplication application = Mockito.mock(HPlusApplication.class, Mockito.RETURNS_DEEP_STUBS);


    @Before
    public void setup() {

        Mockito.when(application.getApplicationContext()).thenReturn(application);
        HPlusApplication.setHPlusApplication(application);

        mAirTouchSDevice = new AirTouchSeriesDevice();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setmDeviceType(AirTouchConstants.AIRTOUCHS_TYPE);
        mAirTouchSDevice.setDeviceInfo(deviceInfo);

        mAirTouchPDevice = new AirTouchSeriesDevice();
        DeviceInfo deviceInfo2 = new DeviceInfo();
        deviceInfo2.setmDeviceType(AirTouchConstants.AIRTOUCHP_TYPE);
        mAirTouchPDevice.setDeviceInfo(deviceInfo2);

    }

    @Test
    public void testGetDeviceControlInfo() {
        ControlPoint point1 = ((AirTouchSeriesDevice) mAirTouchSDevice).getDeviceControlInfo();
        Assert.assertEquals(AIRTOUCH_CONTROL_LEVEL_POINT, point1.getPointNumberOfEveryLevel());
        Assert.assertEquals(AIRTOUCH_S_CONTROL_POINT_TOTAL, point1.getTotalPointNumber());

        ControlPoint point2 = ((AirTouchSeriesDevice) mAirTouchPDevice).getDeviceControlInfo();
        Assert.assertEquals(AIRTOUCH_CONTROL_LEVEL_POINT, point2.getPointNumberOfEveryLevel());
        Assert.assertEquals(AIRTOUCH_P_CONTROL_POINT_TOTAL, point2.getTotalPointNumber());
    }



}
