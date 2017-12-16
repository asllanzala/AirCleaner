package com.honeywell.hch.airtouchv3.framework.model;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Qian Jin on 12/3/15.
 */
public class LocationAddress implements IRequestResponse, Serializable {

    @SerializedName("address_components")
    private List<AddressComponents> mAddressComponents;

    public List<AddressComponents> getAddressComponents() {
        return mAddressComponents;
    }
}
