package com.honeywell.hch.airtouchv3.framework.model;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by wuyuan on 9/24/15.
 */
@RunWith(RobolectricTestRunner.class)
public class ControlPointTest {
    @Test
    public void testGetTotalPointNumber() {
        ControlPoint controlPoint = new ControlPoint();

        controlPoint.setTotalPointNumber(0);
        Assert.assertEquals(0, controlPoint.getTotalPointNumber());

        controlPoint.setTotalPointNumber(14);
        Assert.assertEquals(14, controlPoint.getTotalPointNumber());

        controlPoint.setTotalPointNumber(-1);
        Assert.assertEquals(-1, controlPoint.getTotalPointNumber());
    }

    @Test
    public void testSetTotalPointNumber() {
        ControlPoint controlPoint = new ControlPoint();

        controlPoint.setTotalPointNumber(0);
        Assert.assertEquals(0, controlPoint.getTotalPointNumber());

        controlPoint.setTotalPointNumber(14);
        Assert.assertEquals(14, controlPoint.getTotalPointNumber());

        controlPoint.setTotalPointNumber(-1);
        Assert.assertEquals(-1, controlPoint.getTotalPointNumber());
    }

    @Test
    public void testGetPointNumberOfEveryLevel() {
        ControlPoint controlPoint = new ControlPoint();

        controlPoint.setPointNumberOfEveryLevel(0);
        Assert.assertEquals(0, controlPoint.getPointNumberOfEveryLevel());

        controlPoint.setPointNumberOfEveryLevel(14);
        Assert.assertEquals(14, controlPoint.getPointNumberOfEveryLevel());

        controlPoint.setPointNumberOfEveryLevel(-1);
        Assert.assertEquals(-1, controlPoint.getPointNumberOfEveryLevel());
    }

    @Test
    public void testSetPointNumberOfEveryLevel() {
        ControlPoint controlPoint = new ControlPoint();

        controlPoint.setPointNumberOfEveryLevel(0);
        Assert.assertEquals(0, controlPoint.getPointNumberOfEveryLevel());

        controlPoint.setPointNumberOfEveryLevel(14);
        Assert.assertEquals(14, controlPoint.getPointNumberOfEveryLevel());

        controlPoint.setPointNumberOfEveryLevel(-1);
        Assert.assertEquals(-1, controlPoint.getPointNumberOfEveryLevel());
    }
}
