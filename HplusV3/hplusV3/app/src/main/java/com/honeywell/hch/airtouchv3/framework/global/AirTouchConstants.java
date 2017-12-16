package com.honeywell.hch.airtouchv3.framework.global;

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

    public static final int AIRTOUCHP_TYPE = 1048592;

    public static final int AIRTOUCHJD_TYPE = 1048578;

    public static final int AIRTOUCH450_TYPE = 1048579;

    /**
     * broadcast
     */
    public static final String HOME_CHANGED = "loginChanged";

    public static final String UPDATE_HOME_NAME = "update_home_name";

    public static final String SET_DEFALUT_HOME = "set_defalut_home";

    public static final String RENAME_HOME = "rename_home";

    public static final String DELETE_HOME = "delete_home";

    public static final String ADD_DEVICE = "add_device";

    public static final String AFTER_USER_LOGIN = "after_user_login";

    public static final  String SHORTTIME_REFRESH_END_ACTION = "short_time_refresh_task";

    public static final  String LONG_REFRESH_END_ACTION = "long_time_refresh_task";


    public static final  String GPS_RESULT = "gps_result";

    public static final String GET_DEVICE_CAPABILITY = "get_device_capability";

    /**
     * Max/Min limit
     */
    public static final int MAX_HOME_NUMBER = 5;
    public static final int MAX_DEVICE_NUMBER = 50;
    public static final int MAX_HOME_CHAR_EDITTEXT = 14;
    public static final int MIN_USER_PASSWORD = 6;

    /**
     * HTTP response bundle data key
     */
    public static final String COMM_TASK_BUNDLE_KEY= "taskId";
    public static final String LOCATION_ID_BUNDLE_KEY= "locationId";

    public static final String DEVICE_CAPABILITY_KEY= "device_capability_key";

    public static final String DEVICE_RUNSTATUS_KEY= "device_runstatus_key";

    public static final String WEATHER_DATA_KEY= "weather_data_key";

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
    public static final int COMM_TASK_END = 3004;
    public static final int COMM_TASK_PART_FAILED = 3005;
    public static final int COMM_TASK_ALL_FAILED = 3006;
    public static final int COMM_TASK_PART_SUCCEED = 3007;


    /**
     * the max value of low pm value
     */
    public static final int MAX_PMVALUE_LOW = 75;
    public static final int OUTDOOR_PM25_MAX = 110;
    /**
     * the middle value of middle pm value
     */
    public static final int MAX_PMVALUE_MIDDLE = 150;

    /**
     * location id
     */
    public static final String LOCATION_ID = "location_id";

    public static final String DEVICE_ID_LIST= "open_device_id_list";

    /**
     * country code
     */
    public static final String CHINA_CODE = "86";
    public static final String INDIA_CODE = "91";

    public static final int FROM_HOME_PAGE = 0;
    public static final int FROM_DEVICE_PAGE = 1;

    /**
     * intent putExtra
     */
    public static final String FORGET_PASSWORD = "forgetPassword";
    public static final String MOBILE_DONE_BACK = "mobileDoneBack";
    public static final String NEW_USER = "newUser";

    public static final String SMART_ENROLL_ENRTRANCE = "smartenrollentranch";


    public static int COMM_TASK_TIME_GAP = 1000;

    public  static final int ERROR_FILTER_RUNTIME = -1;

}
