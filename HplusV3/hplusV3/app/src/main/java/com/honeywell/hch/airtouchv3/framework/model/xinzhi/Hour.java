package com.honeywell.hch.airtouchv3.framework.model.xinzhi;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lynnliu on 10/16/15.
 */
public class Hour {
    @SerializedName("text")
    protected String mText;

    @SerializedName("code")
    protected int mCode;

    @SerializedName("temperature")
    protected int mTemperature;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int temperature) {
        mTemperature = temperature;
    }

//    history 24 hour weather data
//    {
//        "status": "OK",                     //状态信息。正常返回时值为"OK"，异常时返回具体错误信息。
//            "history": [{                       //24小时历史数据数组，24个对象，从新到旧排序。
//                "text": "雷阵雨",
//                "code": "11",
//                "temperature": "23",
//                "feels_like": "24",
//                "wind_direction": "西",
//                "wind_speed": "8.51",
//                "wind_scale": "2",
//                "humidity": "65",
//                "visibility": "10.0",
//                "pressure": "1010.2",
//                "pressure_rising": "steady",
//                "air_quality": {
//                      "city": {
//                          "aqi": "58",
//                          "pm25": "40",
//                          "pm10": "54",
//                          "so2": "4",
//                          "no2": "21",
//                          "co": "0.683",
//                          "o3": "111",
//                          "quality": null,
//                          "last_update": null
//                      },
//                  "stations": null
//                  },
//                  "time_stamp": "2014-08-16T23:45:48+08:00"   //该小时数据获取时间点。
//              }, {                                //第2个对象。
//                  "text": "雷阵雨",
//                  "code": "11",
//                  "temperature": "22",
//        ......                          //省略第2个对象其余部分以及另外22个对象。
//    }

//    future 24 hour weather data
//      {
//      "status": "OK",                         //状态信息。正常返回时值为"OK"，异常时返回具体错误信息。
//          "hourly": [{                            //未来24小时逐时天气预报数组，24个对象。
//              "text": "晴",                       //天气情况文字
//              "code": "0",                        //天气情况代码 (天气代码与天气图标对应说明)
//              "temperature": "9",                 //温度
//              "time": "2014-12-08T12:00:00+08:00" //时间
//          }, {                                    //下一小时预报
//              "text": "晴",
//              "code": "0",
//              "temperature": "9",
//              "time": "2014-12-08T13:00:00+08:00"
//          }, {                                    //省略未来3至24小时预报
//              ...
//          }]
//      }

}
