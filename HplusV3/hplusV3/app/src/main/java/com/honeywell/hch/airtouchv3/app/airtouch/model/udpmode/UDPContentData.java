package com.honeywell.hch.airtouchv3.app.airtouch.model.udpmode;

/**
 * Created by wuyuan on 11/19/15.
 */
public class UDPContentData {

   private UDPCommonHeaderData udpCommonHeaderData = new UDPCommonHeaderData();
    
    private UDPCMDHeaderData udpcmdHeaderData = new UDPCMDHeaderData();

    /**
     * 第三个数据包的内容
     */
    private UDPData udpData = new UDPData();

    private UDPFirstData udpFirstData = new UDPFirstData();


    public UDPCommonHeaderData getUdpCommonHeaderData() {
        return udpCommonHeaderData;
    }

    public void setUdpCommonHeaderData(UDPCommonHeaderData udpCommonHeaderData) {
        this.udpCommonHeaderData = udpCommonHeaderData;
    }

    public UDPCMDHeaderData getUdpcmdHeaderData() {
        return udpcmdHeaderData;
    }

    public void setUdpcmdHeaderData(UDPCMDHeaderData udpcmdHeaderData) {
        this.udpcmdHeaderData = udpcmdHeaderData;
    }

    public UDPData getUdpData() {
        return udpData;
    }

    public void setUdpData(UDPData udpData) {
        this.udpData = udpData;
    }

    public UDPFirstData getUdpFirstData() {
        return udpFirstData;
    }

    public void setUdpFirstData(UDPFirstData udpFirstData) {
        this.udpFirstData = udpFirstData;
    }
}
