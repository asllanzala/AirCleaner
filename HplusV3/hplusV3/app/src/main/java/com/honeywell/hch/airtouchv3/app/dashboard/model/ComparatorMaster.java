package com.honeywell.hch.airtouchv3.app.dashboard.model;

import com.honeywell.hch.airtouchv3.framework.model.HomeDevice;

import java.util.Comparator;

/**
 * Created by Qian Jin on 10/20/15.
 */
public class ComparatorMaster implements Comparator {

    @Override
    public int compare(Object arg0, Object arg1) {
        HomeDevice device0 = (HomeDevice)arg0;
        HomeDevice device1 = (HomeDevice)arg1;

        if (device0.getIsMasterDevice() < device1.getIsMasterDevice())
            return 1;
        if (device0.getIsMasterDevice() > device1.getIsMasterDevice())
            return -1;

        return 0;
    }
}
