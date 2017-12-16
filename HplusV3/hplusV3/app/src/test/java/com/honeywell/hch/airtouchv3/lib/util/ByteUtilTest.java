package com.honeywell.hch.airtouchv3.lib.util;

import com.honeywell.hch.airtouchv3.util.HPlusFileUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Vincent on 26/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ByteUtilTest {

    @Test
    public void testIntToBytes() {
        int value = 123;
        Assert.assertEquals(value, (ByteUtil.intToBytes(value))[0]);
    }

    @Test
    public void testIntToBytes2() {
        int value = 123;
        Assert.assertEquals(value, (ByteUtil.intToBytes(value))[0]);
    }

    @Test
    public void testGetDataBytes() {
        String str = HPlusFileUtils
                .readFileFromWebTestsAsString("create_group");

        Assert.assertEquals(34, ByteUtil.getDataBytes(str)[0]);
    }

    @Test
    public void testCalculateCrc() {
        String str = null;
        Assert.assertEquals("", ByteUtil.calculateCrc(str));

        str = "";
        Assert.assertEquals("", ByteUtil.calculateCrc(str));

        str = "test";
        Assert.assertEquals("7EA1", ByteUtil.calculateCrc(str));

    }


}
