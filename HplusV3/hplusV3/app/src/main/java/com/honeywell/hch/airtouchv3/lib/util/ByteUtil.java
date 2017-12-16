package com.honeywell.hch.airtouchv3.lib.util;


import com.google.gson.Gson;

/**
 * Created by wuyuan on 11/19/15.
 */
public class ByteUtil {

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }


    public static byte[] getDataBytes(Object object){

        Gson g = new Gson();
        String jsonString = g.toJson(object);
        return jsonString.getBytes();
    }

    public static String calculateCrc(String str){
        String result = "";
        if (StringUtil.isEmpty(str)){
            return result;
        }

        str = str.toUpperCase();

        int len = str.length();

        int bitLen = len * 8;
        int bit_cnt = 0;
        int ndex = 0;
        int mask = 0x80;
        int bit = 0;
        int crc = 0;

        while(bit_cnt < bitLen){
            if ((crc & 0x8000) == 0){
                bit = 0;
            }
            else{
                bit = 1;
            }

            if((str.charAt(ndex) & mask) != 0){
                bit = bit ^ 1;
            }

            crc = crc << 1;

            if (bit == 1)
            {
                crc = crc ^ 0x8004;
                crc = crc | 0x0001;
            }
            bit_cnt++;

            mask = mask >> 1;
            if (mask == 0)
            {
                ndex++;
                mask = 0x80;
            }
        }
        crc = crc & 0xffff;

        return Integer.toHexString(crc).toUpperCase();
    }

}
