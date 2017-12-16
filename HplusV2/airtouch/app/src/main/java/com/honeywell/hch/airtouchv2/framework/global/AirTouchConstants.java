package com.honeywell.hch.airtouchv2.framework.global;

import com.honeywell.hch.airtouchv2.app.airtouch.controller.device.HouseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyuan on 15/4/28.
 */
public class AirTouchConstants{
    /**
     * display HouseActivity
     */
    public static final  String DISPLAY_INTENT_ACTION = "display_activity_component";

    public static  final String ANIMATION_SHOW_CITY_LAYOUT_ACTION = "show_city_layout_action";

    public static final int HOUSEACTIVITY_FINISH_WHAT = 1;

    public static final float FROM_X = 1;

    public static final float TO_X = 0.16f;

    public static final String ADD_DEVICE_OR_HOME_ACTION = "add_device_or_home_action";

    public static final String IS_ADD_HOME = "is_add_home";

    // for enrollment last step, location id of selected home
    public static final String LOCAL_LOCATION_ID = "local_location_id";

    public static final String DELETE_DEVICE_SUCCESS_ACTION = "delete_device_success_action";

    public static List<HouseActivity> houseActivityList = new ArrayList<HouseActivity>(1);

    /**
     * particle level constant
     */
    public static final int PARTICLE_LEVEL_SEVEN = 7;

    public static final int PARTICLE_LEVEL_SIX = 6;

    public static final int PARTICLE_LEVEL_FIVE = 5;

    public static final int PARTICLE_LEVEL_FOUR = 4;

    public static final int PARTICLE_LEVEL_THREE = 3;

    public static final int PARTICLE_LEVEL_TWO = 2;

    public static final int PARTICLE_LEVEL_ONE = 1;

    public static final int PARTICLE_LEVEL_NONE = -1;

    /**
     * clean dust level constant
     */
    public static final int LEVEL_ONE_MAX = 1;

    public static final int LEVEL_TWO_MAX = 10;

    public static final int LEVEL_THREE_MAX = 75;

    public static final int LEVEL_FOUR_MAX = 150;

    public static final int LEVEL_FIVE_MAX = 1500;

    public static final int LEVEL_SIX_MAX = 15000;

    /**
     * emotion request param
     */
    public static final int NONE_REQUEST = -1;
    public static final int YESTERDAY_REQUEST = 0;
    public static final int THIS_WEEK_REQUEST = 1;
    public static final int THIS_MONTH_REQUEST = 2;
    public static final int SO_FAR_REQUEST = 3;

    /**
     * for init emotion bubble content of cigeratte or car fume
     */
    public static final String INIT_STR_VALUE = "0.00";

    public static final int AIRTOUCHS_TYPE = 1048577;

    /**
     * broadcast
     */
    public static final String HOME_CHANGED = "loginChanged";

    public static final String UPDATE_HOME_NAME = "update_home_name";

    /**
     * Max/Min limit
     */
    public static final int MAX_HOME_NUMBER = 5;
    public static final int MAX_DEVICE_NUMBER = 5;
    public static final int MAX_HOME_CHAR_EDITTEXT = 14;
    public static final int MIN_USER_PASSWORD = 6;

    /**
     * HTTP response bundle data key
     */
    public static final String COMM_TASK_BUNDLE_KEY= "taskId";
    public static final String LOCATION_ID_BUNDLE_KEY= "locationId";
    /**
     * HTTP response flag for cases
     */
    public static final int CHECK_MAC_ALIVE = 2000;
    public static final int CHECK_MAC_AGAIN = 2001;
    public static final int CHECK_MAC_OFFLINE = 2002;
    public static final int COMM_TASK_SUCCEED = 3000;
    public static final int COMM_TASK_FAILED = 3001;
    public static final int COMM_TASK_RUNNING = 3002;
    public static final int COMM_TASK_TIMEOUT = 3003;


    /**
     * the max value of low pm value
     */
    public static final int MAX_PMVALUE_LOW = 75;
    /**
     * the middle value of middle pm value
     */
    public static final int MAX_PMVALUE_MIDDLE = 150;

}
