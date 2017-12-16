package com.honeywell.hch.airtouchv3.lib.util;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vincent on 26/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DateTimeUtilTest {

    @Test
    public void testGetNowDateTimeString() {
        String date = "2016-01-26";

        Assert.assertEquals(1, DateTimeUtil.getDateTimeFromString(DateTimeUtil.THINKPAGE_DATE_FORMAT, "2016-01-26").getMonth() + 1);
    }

    @Test
    public void testGetDateTimeString() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeUtil.THINKPAGE_DATE_FORMAT);
        String str = dateFormat.format(date).toString();

        Assert.assertEquals(str , DateTimeUtil.getDateTimeString(date, DateTimeUtil.THINKPAGE_DATE_FORMAT));
    }
}
