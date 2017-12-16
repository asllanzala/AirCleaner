package com.honeywell.hch.airtouchv3.app.airtouch.controller.enrollment.smartlink;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.broadcom.cooee.Cooee;
import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.app.airtouch.model.udpmode.UDPContentData;
import com.honeywell.hch.airtouchv3.lib.util.ByteUtil;
import com.honeywell.hch.airtouchv3.lib.util.LogUtil;
import com.honeywell.hch.airtouchv3.lib.util.NetWorkUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by wuyuan on 11/24/15.
 */
public class ConnectAndFindDeviceManager {

    public static final String IS_CONNECT = "isconnecting";


    private static final String TAG = "ConnectAndFindDeviceManager";

    private static final int CONNECTING_TIMEOUT = 2000;
    private static final int PROCESS_END = 2001;
    private static final int WIFI_CONNECTED_CHECK_END = 2002;

    private static final int THREAD_ERROR = 2003;

    private Thread mCountThread = null;
    private Thread mSendCooeeThread = null;
    private Thread mSendThread = null;
    private Thread mReceiveThread = null;
    private Thread mCheckWifiConnectThread = null;

    boolean mSendCooeeThreadDone = false;
    boolean mCountThredThreadDone = false;


    public static final int DEST_DEFAULT_PORT = 4320;

    public static final int SOURCE_DEFAULT_PORT = 33333;

    private static final int MAX_COUNT_TIME = 80;



    private DatagramSocket udpSocket;


    static DatagramSocket receiveudpSocket = null;

    static DatagramPacket receiveudpPacket = null;

    private byte[] contentBytes ;


    private static final String  PRODUCT_UUID_S = "KSN95Y";
    private static final String  PRODUCT_UUID_450S = "KHN6YM";
    private static final  String PRODUCT_UUID_STR = "productuuid";
    private static final  String MAC_STR_KEY = "mac";
    private static final  String CODE_STR_KEY = "code";

    private static String mDeviceMacWithcolon ; //这个mac是扫二维码后得到的

    private static String mDeviceMacWithNocolon;

    private static final String MAC_HAADER_COOEE = "0000";

    private boolean mSendThreadRun = true;
    private boolean mReceiveThreadRun = true;


    private String deviceIdAddress = "";
    private int receiveTime = 0;

    private Object mLockobj = new Object();


    private SmartlinkInputPasswordActivity mSmartlinkInputPasswordActivity;

    private int mConnectingCount = 0;

    public ConnectAndFindDeviceManager(SmartlinkInputPasswordActivity smartlinkInputPasswordActivity, String deviceMacwithcolon,String deviceNocolon){

        mSmartlinkInputPasswordActivity = smartlinkInputPasswordActivity;
        mDeviceMacWithcolon = deviceMacwithcolon;
        mDeviceMacWithNocolon = deviceNocolon;

    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTING_TIMEOUT:
                case THREAD_ERROR:
                    endAllThread();
                    if (mSmartlinkInputPasswordActivity != null){
                        mSmartlinkInputPasswordActivity.connectingTimeout();
                    }
                    break;
                case PROCESS_END:
                    endAllThread();
                    if (mSmartlinkInputPasswordActivity != null) {
                        mSmartlinkInputPasswordActivity.findDeviceSuccess();
                    }

                case WIFI_CONNECTED_CHECK_END:
                     Bundle bundle = msg.getData();
                    boolean isConnecting = bundle.getBoolean(IS_CONNECT);
                    if (mSmartlinkInputPasswordActivity != null){
                        if (isConnecting){
                            restAllThread();
                            mSmartlinkInputPasswordActivity.startFindThread();
                        }
                        else{
                            mSmartlinkInputPasswordActivity.showAlertDialog(R.string.smartlink_access_network);
                            mSmartlinkInputPasswordActivity.enableComponent();
                        }
                    }
                    mCheckWifiConnectThread = null;
                    break;



            }
            super.handleMessage(msg);
        }
    };

    private void startCountThread(){

        if (mCountThread == null) {
            mCountThread = new Thread() {
                public void run() {
                    while (!mCountThredThreadDone) {

                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {

                        }
                        mConnectingCount++;
                        if(mConnectingCount >= MAX_COUNT_TIME){
                            if (!mCountThredThreadDone){
                                Message message = Message.obtain();
                                message.what = CONNECTING_TIMEOUT;
                                mHandler.sendMessage(message);
                            }
                        }
                    }
                }
            };
        }
        if (!mCountThredThreadDone){
            mCountThread.start();
        }


    }

    /**
     *
     * @param ssid  ssid
     * @param password  route's password
     * @param mLocalIp ip
     */
    public void startConnectingAndFinding(final String ssid,final String password,final int mLocalIp){


            startSendCooeeThread(ssid,password,mLocalIp);

            startCountThread();

            // 发现设备 udp
            constructUDPContentData(1);
            sendUdp();
            receivUdp();
    }

    private void startSendCooeeThread(final String ssid,final String password,final int mLocalIp){
        if (mSendCooeeThread == null) {
            mSendCooeeThread = new Thread() {
                public void run() {
                    while (!mSendCooeeThreadDone) {

                        String mac = MAC_HAADER_COOEE + mDeviceMacWithNocolon;
                        Cooee.send(ssid, password, mLocalIp, mac);

                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {

                        }
                    }
                }
            };
        }
        if (!mSendCooeeThreadDone){
            mSendCooeeThread.start();
        }
    }

    /**
     *
     * @param type 1 和 3 两种情况
     */
    private void constructUDPContentData(int type){
        int index = 0;
        UDPContentData udpContentData = new UDPContentData();

        byte[] udpdataBytes = null;
        if (type == 1){
            Log.e("Main","constructUDPContentData first udp data");
            udpdataBytes = ByteUtil.getDataBytes(udpContentData.getUdpFirstData());
        }
        else {
            Log.e("Main","constructUDPContentData third udp data");

            udpContentData.getUdpData().setMac(mDeviceMacWithcolon);
            udpdataBytes = ByteUtil.getDataBytes(udpContentData.getUdpData());
        }



        udpContentData.getUdpcmdHeaderData().setType(type);

        byte[] typeBytes = udpContentData.getUdpcmdHeaderData().getmtypeByte();
        byte[] cmdBytes = udpContentData.getUdpcmdHeaderData().getcmdByte();

        int len = udpdataBytes.length + typeBytes.length + cmdBytes.length;
        udpContentData.getUdpCommonHeaderData().setLen(len);


        int checkSum = 0;
        for (int i  = 0; i < udpdataBytes.length; i++){
            checkSum += udpdataBytes[i];
        }

        for (int i  = 0; i < typeBytes.length; i++){
            checkSum += typeBytes[i];
        }

        for (int i  = 0; i < cmdBytes.length; i++){
            checkSum += cmdBytes[i];
        }

        udpContentData.getUdpCommonHeaderData().setChecksum((byte)(checkSum % 256));

        byte[] lenBytes =  udpContentData.getUdpCommonHeaderData().getLenByte();
        byte[] enctypeByte = udpContentData.getUdpCommonHeaderData().getmEnctypeByte();
        byte[] magicByte = udpContentData.getUdpCommonHeaderData().getMagaicByte();
        byte[] checkByte = udpContentData.getUdpCommonHeaderData().getChecksumByte();

        int totalLen = magicByte.length + lenBytes.length + enctypeByte.length + checkByte.length
                + typeBytes.length + cmdBytes.length + udpdataBytes.length;

        synchronized(mLockobj){
            contentBytes = null;
            contentBytes = new byte[totalLen];

            for (int i = 0; i < magicByte.length; i++){
                contentBytes[index]= magicByte[i];
                index++;
            }

            for (int i = 0; i < lenBytes.length; i++){
                contentBytes[index]= lenBytes[i];
                index++;
            }

            for (int i = 0; i < enctypeByte.length; i++){
                contentBytes[index]= enctypeByte[i];
                index++;
            }

            for (int i = 0; i < checkByte.length; i++){
                contentBytes[index]= checkByte[i];
                index++;
            }

            for (int i = 0; i < typeBytes.length; i++){
                contentBytes[index]= typeBytes[i];
                index++;
            }

            for (int i = 0; i < cmdBytes.length; i++){
                contentBytes[index]= cmdBytes[i];
                index++;
            }

            for (int i = 0; i < udpdataBytes.length; i++){
                contentBytes[index]= udpdataBytes[i];
                index++;
            }
        }

    }


    private void sendUdp(){
        if (mSendThread == null || !mSendThread.isAlive()){
            mSendThread =
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while(mSendThreadRun){
                                DatagramPacket dataPacket = null;

                                try {
                                    if(udpSocket == null){
                                        udpSocket = new DatagramSocket(null);
                                        udpSocket.setReuseAddress(true);
                                        udpSocket.bind(new InetSocketAddress(SOURCE_DEFAULT_PORT));
                                    }

                                    dataPacket = new DatagramPacket(contentBytes, contentBytes.length);
                                    dataPacket.setLength(contentBytes.length);

                                    InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
                                    dataPacket.setPort(DEST_DEFAULT_PORT);
                                    dataPacket.setAddress(broadcastAddr);
                                } catch (Exception e) {
                                    LogUtil.log(LogUtil.LogLevel.ERROR,TAG, "==1 =" + e.toString());
                                }
                                // while( start ){
                                try {
                                    udpSocket.send(dataPacket);
                                } catch (Exception e) {
                                    LogUtil.log(LogUtil.LogLevel.ERROR, TAG, "==2 =" + e.toString());
                                }
                                // }

                                if (udpSocket != null){
                                    udpSocket.close();
                                    udpSocket = null;
                                }

                                try {
                                    //发udp的包不能太频繁，会导致设备反应不过来。
                                    Thread.sleep(600);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                        }

                    });
        }
        mSendThread.start();

    }



    private void receivUdp(){
        if (mReceiveThread == null){
            mReceiveThread =
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            byte[] data = new byte[512];
                            try {
                                if(receiveudpSocket == null){
                                    receiveudpSocket = new DatagramSocket(null);
                                    receiveudpSocket.setReuseAddress(true);
                                    receiveudpSocket.bind(new InetSocketAddress(SOURCE_DEFAULT_PORT));
                                }
                                receiveudpPacket = new DatagramPacket(data, data.length);
                            } catch (SocketException e1) {
                                e1.printStackTrace();
                                return;
                            }

                            while (mReceiveThreadRun) {
                                try {
                                    receiveudpSocket.receive(receiveudpPacket);
                                    if (null != receiveudpPacket.getAddress()) {
                                        //解析数据
                                        String thisUdpIp = receiveudpPacket.getAddress().toString();
                                        Log.e("MainActivity","thisUdpIp = " + thisUdpIp);
                                        if (receiveTime == 0){
                                            if(pareseDeviceFirstUdpBytes(data,thisUdpIp)){
                                                Log.e("MainActivity", "receive second upd package success");
                                                //send the third udp to device
                                                constructUDPContentData(3);

                                            }
                                        }
                                        else if(receiveTime == 1){
                                            if(pareseDeviceLastUdpBytes(data,thisUdpIp)){
                                                Log.e("MainActivity", "receive last upd package success");
                                                Message message = Message.obtain();
                                                message.what = PROCESS_END;
                                                mHandler.sendMessage(message);
                                                return;
                                            }
                                        }

                                    }
                                } catch (Exception e) {
                                    Log.e("Main", "Exception = " + e);
                                }

                            }

                        }
                    });
        }
        if (mReceiveThread != null){
            mReceiveThread.start();
        }

    }



    private boolean pareseDeviceFirstUdpBytes(byte[] deviceBytes,String ipAddress){
        try{
            String deviceUdpStr = new String(deviceBytes);
            Log.e("MainActivity","deviceUdpStr = " + deviceUdpStr);

            int left = deviceUdpStr.indexOf(PRODUCT_UUID_STR);
            if (left >= 0){

                int macleft = deviceUdpStr.indexOf(MAC_STR_KEY);
                if (macleft >= 0){
                    String massageStr = deviceUdpStr.substring(macleft).toLowerCase();
                    String lowMac = mDeviceMacWithcolon.toLowerCase();
                    if (massageStr.contains(lowMac)){
                        mSendCooeeThreadDone = true;
                        deviceIdAddress = ipAddress;
                        receiveTime++;
                        Log.e("MainActivity","return true" );
                        return true;
                    }

                }
            }
        }catch (Exception e){

            Log.e("MainActivity","pareseDeviceFirstUdpBytes Exception = " + e.toString());
        }
        return false;
    }

    private boolean pareseDeviceLastUdpBytes(byte[] deviceBytes,String ipAddress){
        try{
            if (!"".equals(deviceIdAddress) && deviceIdAddress.equalsIgnoreCase(ipAddress)){
                String deviceUdpStr = new String(deviceBytes);
                int left = deviceUdpStr.indexOf(CODE_STR_KEY);
                if (left >=0){
                    mSendThreadRun = false;
                    return true;
                }
            }
        }catch (Exception e){

        }
        return false;
    }


    private void endAllThread(){
        mSendThreadRun = false;
        mReceiveThreadRun = false;
        mSendCooeeThreadDone = true;
        mCountThredThreadDone = true;
        receiveTime = 0;
        mConnectingCount = 0;

        mCountThread = null;
        mSendCooeeThread = null;
        mSendThread = null;
        mReceiveThread = null;
    }

    private void restAllThread(){
        mSendThreadRun = true;
        mReceiveThreadRun = true;
        mSendCooeeThreadDone = false;
        mCountThredThreadDone = false;
        receiveTime = 0;
        mConnectingCount = 0;

        mCountThread = null;
        mSendCooeeThread = null;
        mSendThread = null;
        mReceiveThread = null;
        mCheckWifiConnectThread = null;
    }

    public void startCheckNetworkConnectingThread(){
        if (mCheckWifiConnectThread == null) {
            mCheckWifiConnectThread = new Thread() {
                public void run() {
                   boolean isCanAccessNetwork = NetWorkUtil.isNetworkCanAccessInternet();
                    Message message = Message.obtain();
                    message.what = WIFI_CONNECTED_CHECK_END;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(IS_CONNECT,isCanAccessNetwork);
                    message.setData(bundle);
                    mHandler.sendMessage(message);

                }
            };
        }
        mCheckWifiConnectThread.start();
    }



}
