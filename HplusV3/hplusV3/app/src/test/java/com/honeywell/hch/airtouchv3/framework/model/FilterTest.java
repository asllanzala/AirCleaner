package com.honeywell.hch.airtouchv3.framework.model;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by wuyuan on 9/23/15.
 */
@RunWith(RobolectricTestRunner.class)
public class FilterTest {
    @Test
    public void testGetUsagePrecent() {
        Filter filter = new Filter();
        filter.setUsagePrecent(0);

        Assert.assertEquals(0.0f, filter.getUsagePrecent());

        Filter filter2 = new Filter();
        filter2.setUsagePrecent(0.55f);
        Assert.assertEquals(0.55f, filter2.getUsagePrecent());

        Filter filter3 = new Filter();
        filter3.setUsagePrecent(-1);
        Assert.assertEquals(-1.0f, filter3.getUsagePrecent());

        Filter filter4 = new Filter();
        filter4.setUsagePrecent(50);
        Assert.assertEquals(50.0f, filter4.getUsagePrecent());
    }

    @Test
    public void testSetUsagePrecent() {
        Filter filter = new Filter();
        filter.setUsagePrecent(0);

        Assert.assertEquals(0.0f, filter.getUsagePrecent());

        Filter filter2 = new Filter();
        filter2.setUsagePrecent(0.55f);
        Assert.assertEquals(0.55f, filter2.getUsagePrecent());

        Filter filter3 = new Filter();
        filter3.setUsagePrecent(-1);
        Assert.assertEquals(-1.0f, filter3.getUsagePrecent());

        Filter filter4 = new Filter();
        filter4.setUsagePrecent(50);
        Assert.assertEquals(50.0f, filter4.getUsagePrecent());
    }

    @Test
    public void testGetName() {
        Filter filter = new Filter();
        filter.setName("");

        Assert.assertEquals("", filter.getName());

        filter.setName("11");
        Assert.assertEquals("11", filter.getName());

        filter.setName("@#$%s安康");
        Assert.assertEquals("@#$%s安康", filter.getName());
    }

    @Test
    public void testSetName() {
        Filter filter = new Filter();
        filter.setName("");

        Assert.assertEquals("", filter.getName());

        filter.setName("11");
        Assert.assertEquals("11", filter.getName());

        filter.setName("@#$%s安康");
        Assert.assertEquals("@#$%s安康", filter.getName());
    }

    @Test
    public void testGetDesc() {
        Filter filter = new Filter();
        filter.setDesc("");

        Assert.assertEquals("", filter.getDesc());

        filter.setDesc("11");
        Assert.assertEquals("11", filter.getDesc());

        filter.setDesc("@#$%s安康");
        Assert.assertEquals("@#$%s安康", filter.getDesc());
    }

    @Test
    public void testSetDesc() {
        Filter filter = new Filter();
        filter.setDesc("");

        Assert.assertEquals("", filter.getDesc());

        filter.setDesc("11");
        Assert.assertEquals("11", filter.getDesc());

        filter.setDesc("@#$%s安康");
        Assert.assertEquals("@#$%s安康", filter.getDesc());
    }

    @Test
    public void testGetrFIDStr() {
        Filter filter = new Filter();
        filter.setRFIDStr("");

        Assert.assertEquals("", filter.getRFIDStr());

        filter.setRFIDStr(null);
        Assert.assertEquals(null, filter.getRFIDStr());

        filter.setRFIDStr("@#$%s安康");
        Assert.assertEquals("@#$%s安康", filter.getRFIDStr());

        filter.setRFIDStr("12345678adkjadad");
        Assert.assertEquals("12345678adkjadad", filter.getRFIDStr());
    }


}
