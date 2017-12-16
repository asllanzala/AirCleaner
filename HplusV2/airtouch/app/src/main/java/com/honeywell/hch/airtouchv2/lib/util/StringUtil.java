package com.honeywell.hch.airtouchv2.lib.util;

import android.widget.EditText;

import com.honeywell.hch.airtouchv2.framework.global.AirTouchConstants;
import com.honeywell.hch.airtouchv2.framework.view.AirTouchEditText;

import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * The util for string
 * Created by liunan on 1/16/15.
 */
public class StringUtil {

    /**
     * judge if a string is null, empty, or only space
     *
     * @param str
     *            the specified string
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String notNullString(String str) {
        return isEmpty(str) ? "" : str;
    }

    /**
     * Trim the referred string on the left of source string.
     *
     * @param src
     * @param trimString
     * @return
     */
    public static String trimLeft(String src, String trimString)
    {
        if (isEmpty(src) || isEmpty(trimString))
        {
            return src;
        }
        while (src.startsWith(trimString))
        {
            src = src.substring(trimString.length());
        }
        return src;
    }

    /**
     * Trim the referred string on the right of source string.
     *
     * @param src
     * @param trimString
     * @return
     */
    public static String trimRight(String src, String trimString)
    {
        if (isEmpty(src) || isEmpty(trimString))
        {
            return src;
        }
        while (src.endsWith(trimString))
        {
            src = src.substring(0, src.length() - trimString.length());
        }
        return src;
    }

    /**
     * Replaces "searchFor" with "replaceWith" in "sourceStr".
     *
     * @param sourceStr
     *            The source string
     * @param searchFor
     *            Pattern to be replaced
     * @param replaceWith
     *            Pattern to replace with
     * @return resulting string.
     */
    public static String replace(String sourceStr, String searchFor, String replaceWith) {
        if (sourceStr == null || searchFor == null || searchFor.length() == 0 || replaceWith == null)
            return sourceStr;

        // Search for searchStr
        int pos = sourceStr.indexOf(searchFor);
        if (pos < 0) {
            return sourceStr;
        }

        StringBuffer sb = new StringBuffer();

        while (pos >= 0) {
            sb.append(sourceStr.substring(0, pos)).append(replaceWith);

            sourceStr = sourceStr.substring(pos + searchFor.length());
            pos = sourceStr.indexOf(searchFor);
        }

        sb.append(sourceStr);

        return sb.toString();
    }

    /**
     * Splits the given string into an array of substrings. Examples:
     * split("a;b;c;d;e", ';') ->"a", "b", "c", "d", "e" split("a;;c;d;e", ';')
     * -> "a", "", "c", "d", "e" split("a;b;c;d;e",'=') -> "a;b;c;d;e"
     * split(";b;c;d;e", ';') -> "", "b", "c", "d", "e" split(";", ';') -> "",""
     * split("", ';') -> ""
     *
     * @param source
     *            String to split.
     * @param sep
     *            Separator character.
     * @return Array of string tokens. In case of no tokens an empty array is
     *         returned (never null.)
     */
    public static String[] split(String source, char sep) {
        if (isEmpty(source)) {
            return new String[] { "" };
        }

        int len = source.length();
        Vector<String> list = new Vector<String>();
        int i = 0;
        int start = 0;

        while (i < len) {
            if (source.charAt(i) == sep) {
                list.addElement(source.substring(start, i));
                start = i + 1;
            }
            i++;
        }
        if (i > start) // some trailing text found, append it
        {
            list.addElement(source.substring(start));
        } else if (source.charAt(len - 1) == sep) // source ends with a
        // separator, add the final
        // empty token
        {
            list.addElement("");
        }

        String[] arr = new String[list.size()];
        list.copyInto(arr);

        return arr;
    }

    /**
     * Extracts one parameter from a string and converts to a long. Example:
     * getParseInt("name=Jazz;age=17;active=1", "age", 7) => 17
     *
     * @param line String containing any number of name=value pairs.
     * @param keyword Keyword marking the value to extract.
     * @param defaultValue Value used in case the parameter is wrong/not found.
     * @return Extracted value.
     */
    public static long getParseValue(String line, String keyword, long defaultValue)
    {
        long ret = defaultValue;
        if (!keyword.endsWith("="))
        {
            keyword += "=";
        }
        String val = getValueStartingWith(line, keyword, ";", "");
        if (!isEmpty(val))
        {
            ret = toLong(val, defaultValue);
        }
        return ret;
    }

    /**
     * Extracts one parameter from a string. Example: getParseInt("name=Jazz;age=17;active=1",
     * "name", "Frank Bith") => "Jazz"
     *
     * @param line String containing any number of name=value pairs.
     * @param keyword Keyword marking the value to extract.
     * @param defaultValue Value used in case the parameter is wrong/not found.
     * @return Extracted value.
     */
    public static String getParseValue(String line, String keyword, String defaultValue)
    {
        String ret = defaultValue;
        if (!keyword.endsWith("="))
        {
            keyword += "=";
        }
        String val = getValueStartingWith(line, keyword, ";", "");
        if (!isEmpty(val))
        {
            ret = val;
        }
        return ret;
    }

    /**
     * Extracts one parameter from a string and converts to an int. Example:
     * getParseInt("name=Jazz;age=17;active=1", "age", 7) => 17
     *
     * @param line String containing any number of name=value pairs.
     * @param keyword Keyword marking the value to extract.
     * @param defaultValue Value used in case the parameter is wrong/not found.
     * @return Extracted value.
     */
    public static int getParseValue(String line, String keyword, int defaultValue)
    {
        int ret = defaultValue;
        if (!keyword.endsWith("="))
        {
            keyword += "=";
        }
        // Add ";" to fix getValueStartingWith() bug
        String val = getValueStartingWith(";" + line, ";" + keyword, ";", "");
        if (!isEmpty(val))
        {
            ret = toInt(val, defaultValue);
        }
        return ret;
    }

    /**
     * Converts given string to an integer value in a safe way.
     *
     * @param str
     *            string to convert
     * @param dft
     *            default value in case the string cannot be converted
     * @return integer value represented in the string, or the default value
     */
    public static int toInt(String str, int dft) {
        if (isEmpty(str)) {
            return dft;
        }

        int ret = dft;
        try {
            ret = Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            ret = dft;
        }

        return ret;
    }

    /**
     * Extracts the value part of the token starting with the specified pattern. Tokens are
     * delimited with sSep. Case is ignored.
     *
     * Example: GetValueStartingWith("a=15;b=-23;c=3", "b=", ";", "0") ==> "-23".
     *
     * Warning! GetValueStartingWith("stt=5;t=abc", "t=", ";", "0") ==> "5" !!!
     *
     * @param sStr string String to search in.
     * @param sPtt string Pattern the value starts with.
     * @param sSep string Tokens separator.
     * @param sDft string Default value in case pattern is not found.
     * @return Value part of the token.
     */
    public static String getValueStartingWith(final String sStr, final String sPtt, final String sSep, final String sDft)
    {
        if (isEmpty(sStr)) { return sDft; }

        String ret = sDft;
        try
        {
            int pos = sStr.indexOf(sPtt);
            if (pos < 0)
            {
                pos = sStr.toUpperCase().indexOf(sPtt.toUpperCase());
            }
            if (pos >= 0)
            {
                pos += sPtt.length();

                int end = sStr.indexOf(sSep, pos);
                if (end >= pos)
                {
                    ret = sStr.substring(pos, end);
                }
                else
                {
                    ret = sStr.substring(pos);
                }
            }
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        return ret;
    }

    /**
     * Converts given string to a long value in a safe way.
     *
     * @param str
     *            string to convert
     * @param dft
     *            default value in case the string cannot be converted
     * @return long value represented in the string, or the default value
     */
    public static long toLong(String str, long dft) {
        if (isEmpty(str)) {
            return dft;
        }

        long ret = dft;
        try {
            ret = Long.parseLong(str);
        } catch (NumberFormatException ex) {
            ret = dft;
        }

        return ret;
    }

    public static void specialCharacterFilter(AirTouchEditText et) {
        String str = stringFilter(et.getEditorText());
        if(!et.getEditorText().equals(str)) {
            et.setEditorText(str);
            et.getEditText().setSelection(str.length());
        }
    }

    public static void specialCharacterFilter(EditText et) {
        String editable = et.getText().toString();
        String str = stringFilter(editable);
        if(!editable.equals(str)){
            et.setText(str);
            et.setSelection(str.length());
        }
    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        // Only digits and characters allowed
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static void maxCharacterFilter(AirTouchEditText et) {
        try {
            String strTransfer = new String(et.
                    getEditorText().getBytes("GBK"), "ISO8859_1");
            if (strTransfer.length() > AirTouchConstants.MAX_HOME_CHAR_EDITTEXT) {
                String str = et.getEditorText().substring(0,
                        AirTouchConstants.MAX_HOME_CHAR_EDITTEXT / 2);
                et.setEditorText(str);
                et.getEditText().setSelection(str.length());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void maxCharacterFilter(EditText et) {
        try {
            String strTransfer = new String(et.
                    getText().toString().getBytes("GBK"), "ISO8859_1");
            if (strTransfer.length() > AirTouchConstants.MAX_HOME_CHAR_EDITTEXT) {
                String str = et.getText().toString().substring(0,
                        AirTouchConstants.MAX_HOME_CHAR_EDITTEXT / 2);
                et.setText(str);
                et.setSelection(str.length());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
