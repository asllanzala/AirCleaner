package com.honeywell.hch.airtouchv3.app.airtouch.model.udpmode;

import com.honeywell.hch.airtouchv3.lib.util.ByteUtil;

/**
 * Created by wuyuan on 11/19/15.
 */
public class UDPCommonHeaderData {

    private int magic = 21930;

    //CMD Header 和 data长度总和
    private int len;

    //是否加密：固定值为 0（统一不加密）
    private int mEnctype;

    //CMD header 和 data 数据的 asscal 码 对256的取余
    private byte checksum;

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getmEnctype() {
        return mEnctype;
    }

    public void setmEnctype(int mEnctype) {
        this.mEnctype = mEnctype;
    }

    public byte getChecksum() {
        return checksum;
    }

    public void setChecksum(byte checksum) {
        this.checksum = checksum;
    }


    public byte[] getMagaicByte(){
        return ByteUtil.intToBytes(magic);
    }


    public byte[] getLenByte(){
        return ByteUtil.intToBytes(len);
    }


    public byte[] getmEnctypeByte(){
        return ByteUtil.intToBytes(mEnctype);
    }

    public byte[] getChecksumByte(){
        byte[] chars = new byte[1];
        chars[0] = checksum;
        return chars;
    }

}
