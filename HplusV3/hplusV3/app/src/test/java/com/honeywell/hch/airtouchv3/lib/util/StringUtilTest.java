package com.honeywell.hch.airtouchv3.lib.util;

/**
 * Created by wuyuan on 15/7/6.
 * <p/>
 * add StringUtil unit test
 */

import com.honeywell.hch.airtouchv3.HPlusApplication;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StringUtilTest
{
    protected HPlusApplication application = Mockito.mock(HPlusApplication.class, Mockito.RETURNS_DEEP_STUBS);

    @Before
    public void setUp() throws Exception {
        Mockito.when(application.getApplicationContext()).thenReturn(application);
        HPlusApplication.setHPlusApplication(application);
    }

    @Test
    public void testIsEmpty()
    {

        String testStr = "";
        Assert.assertEquals(true, StringUtil.isEmpty(testStr));

        testStr = "test1";
        Assert.assertEquals(false, StringUtil.isEmpty(testStr));

        testStr = null;
        Assert.assertEquals(true, StringUtil.isEmpty(testStr));

    }

    @Test
    public void testNotNullString()
    {

        String testStr = "";
        Assert.assertEquals("", StringUtil.notNullString(testStr));

        testStr = "test1";
        Assert.assertEquals(testStr, StringUtil.notNullString(testStr));

        testStr = null;
        Assert.assertEquals("", StringUtil.notNullString(testStr));
    }

    @Test
    public void testTrimLeft()
    {
        String src = "";
        String testStr = "";
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        testStr = null;
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        testStr = "te";
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        src = null;
        testStr = "";
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        testStr = null;
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        testStr = "te";
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        src = "test";
        testStr = "";
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        testStr = null;
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        testStr = "te";
        Assert.assertEquals("st", StringUtil.trimLeft(src, testStr));

        src = "abcdadabccst";
        testStr = "ab";
        Assert.assertEquals("cdadabccst", StringUtil.trimLeft(src, testStr));

        src = "abcdadabccst";
        testStr = "bc";
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        src = "abcdadabccst";
        testStr = "st";
        Assert.assertEquals(src, StringUtil.trimLeft(src, testStr));

        src = "abcdadabccst";
        testStr = src;
        Assert.assertEquals("", StringUtil.trimLeft(src, testStr));
    }


    @Test
    public void testTrimRight()
    {
        String src = "";
        String testStr = "";
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        testStr = null;
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        testStr = "te";
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        src = null;
        testStr = "";
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        testStr = null;
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        testStr = "te";
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        src = "test";
        testStr = "";
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        testStr = null;
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        testStr = "te";
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        src = "abcdadabccst";
        testStr = "ab";
        Assert.assertEquals(src, StringUtil.trimRight(src, testStr));

        src = "abcdadabccst";
        testStr = "bccst";
        Assert.assertEquals("abcdada", StringUtil.trimRight(src, testStr));


        src = "abcdadabccst";
        testStr = src;
        Assert.assertEquals("", StringUtil.trimRight(src, testStr));
    }


    @Test
    public void testReplace()
    {

        String sourceStr = null;
        String searchFor = "dkalcd";
        String replaceWith = "dsc";
        Assert.assertEquals(sourceStr, StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = "dadace";
        searchFor = "";
        replaceWith = "dsc";
        Assert.assertEquals(sourceStr, StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = "dadace";
        searchFor = null;
        replaceWith = "dsc";
        Assert.assertEquals(sourceStr, StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = "dadace";
        searchFor = "da";
        replaceWith = null;
        Assert.assertEquals(sourceStr, StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = "";
        searchFor = null;
        replaceWith = null;
        Assert.assertEquals(sourceStr, StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = null;
        searchFor = null;
        replaceWith = null;
        Assert.assertEquals(sourceStr, StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = "dadf52errddf";
        searchFor = "dacgd";
        replaceWith = "dcs";
        Assert.assertEquals(sourceStr, StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = "dwcsds";
        searchFor = "dw";
        replaceWith = "cc";
        Assert.assertEquals("cccsds", StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = "dwcsds";
        searchFor = "dwcsds";
        replaceWith = "ccc";
        Assert.assertEquals("ccc", StringUtil.replace(sourceStr, searchFor, replaceWith));

        sourceStr = "dwcsdcscsdts";
        searchFor = "cs";
        replaceWith = "";
        Assert.assertEquals("dwddts", StringUtil.replace(sourceStr, searchFor, replaceWith));

    }


    @Test
    public void testSplit()
    {
        String source = null;
        char sep = ' ';
        Assert.assertEquals(true, stringArrayEqual(new String[]{""}, StringUtil.split(source,
                sep)));

        source = "dadfadaxgx";
        sep = 'a';
        Assert.assertEquals(true, stringArrayEqual(new String[]{"d", "df", "d", "xgx"},
                StringUtil.split(source, sep)));

        source = "aaaa";
        sep = 'a';
        Assert.assertEquals(true, stringArrayEqual(new String[]{"", "", "", "", ""}, StringUtil
                .split(source, sep)));
//
//
        source = "cvada";
        sep = 'a';
        Assert.assertEquals(true, stringArrayEqual(new String[]{"cv", "d", ""}, StringUtil.split
                (source, sep)));


    }

    @Test
    public void testGetParseValueForLongReturn()
    {
        String line = "name=Jazz;age=17;active=1";
        String keyword = "age";
        Assert.assertEquals(17, StringUtil.getParseValue(line, keyword, 7));

    }

    @Test
    public void testGetParseValueForStringReturn()
    {
        String line = "name=Jazz;age=17;active=1";
        String keyword = "name";
        Assert.assertEquals("Jazz", StringUtil.getParseValue(line, keyword, ""));

    }

    @Test
    public void testGetParseValueForIntReturn()
    {
        String line = "name=Jazz;age=17;active=1";
        String keyword = "age";
        Assert.assertEquals(17, StringUtil.getParseValue(line, keyword, 7));

    }

    @Test
    public void testToInt()
    {
        String str = "";
        int defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toInt(str, defaultVaule));

        str = null;
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toInt(str, defaultVaule));

        str = "1adw";
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toInt(str, defaultVaule));

        str = "123.";
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toInt(str, defaultVaule));

        str = "123.44";
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toInt(str, defaultVaule));

        str = "-1111";
        defaultVaule = 0;
        Assert.assertEquals(-1111, StringUtil.toInt(str, defaultVaule));

        str = "123";
        defaultVaule = 0;
        Assert.assertEquals(123, StringUtil.toInt(str, defaultVaule));
    }

    @Test
    public void testGetValueStartingWith()
    {
        String str = "";
        String sPar = "";
        String sSep = "";
        String sDefault = "";

        Assert.assertEquals("", StringUtil.getValueStartingWith(str, sPar, sSep, sDefault));

        str = null;
        sPar = "";
        sSep = "";
        sDefault = "";
        Assert.assertEquals("", StringUtil.getValueStartingWith(str, sPar, sSep, sDefault));

        str = "a=15;b=-23;c=3";
        sPar = "a = ";
        sSep = ";";
        sDefault = "";
        Assert.assertEquals("", StringUtil.getValueStartingWith(str, sPar, sSep, sDefault));


        str = "a=15;b=-23;c=3";
        sPar = "a=";
        sSep = ";";
        sDefault = "";
        Assert.assertEquals("15", StringUtil.getValueStartingWith(str, sPar, sSep, sDefault));

        str = "a=15;b=-23;c=3";
        sPar = "a=";
        sSep = "=";
        sDefault = "";
        Assert.assertEquals("15;b", StringUtil.getValueStartingWith(str, sPar, sSep, sDefault));

        str = "a=15;b=-23;c=3";
        sPar = "a=";
        sSep = "";
        sDefault = "";
        Assert.assertEquals("", StringUtil.getValueStartingWith(str, sPar, sSep, sDefault));
    }

    @Test
    public void testToLong()
    {
        String str = "";
        long defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toLong(str, defaultVaule));

        str = null;
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toLong(str, defaultVaule));

        str = "1adw";
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toLong(str, defaultVaule));

        str = "123.";
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toLong(str, defaultVaule));

        str = "-1111";
        defaultVaule = 0;
        Assert.assertEquals(-1111, StringUtil.toLong(str, defaultVaule));

        str = "123";
        defaultVaule = 0;
        Assert.assertEquals(123, StringUtil.toLong(str, defaultVaule));

        str = "123.55";
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toLong(str, defaultVaule));

        str = "0.12355";
        defaultVaule = 0;
        Assert.assertEquals(0, StringUtil.toLong(str, defaultVaule));
    }


    @Test
    public void testStringFilterAddHome() {
        String str = "123";
        Assert.assertEquals("123", StringUtil.stringFilter(str));
        str = "@#$%%^";
        Assert.assertEquals("", StringUtil.stringFilter(str));
    }

    @Test
    public void testGetStringWithColon() {
        String splitedStr = null;
        int number = 0;
        Assert.assertEquals("", StringUtil.getStringWithColon(splitedStr, number));
        splitedStr = "";
        Assert.assertEquals("", StringUtil.getStringWithColon(splitedStr, number));
        splitedStr = "123123";
        number = 10;
        Assert.assertEquals(splitedStr, StringUtil.getStringWithColon(splitedStr, number));
        number = 5;
        Assert.assertEquals("12312:3", StringUtil.getStringWithColon(splitedStr, number));

    }

    private boolean stringArrayEqual(String[] str1, String[] str2)
    {
        if (str1 == null && str2 == null)
        {
            return true;
        }

        if ((str1 == null && str2 != null) || (str1 != null && str2 == null))
        {
            return false;
        }
        if (str1.length != str2.length)
        {
            return false;
        }

        for (int i = 0; i < str1.length; i++)
        {
            if (!str1[i].equals(str2[i]))
            {
                return false;
            }
        }
        return true;
    }
}
