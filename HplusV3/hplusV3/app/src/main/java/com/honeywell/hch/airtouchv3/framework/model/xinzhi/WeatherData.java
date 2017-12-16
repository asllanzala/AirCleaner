package com.honeywell.hch.airtouchv3.framework.model.xinzhi;

import com.google.gson.annotations.SerializedName;
import com.honeywell.hch.airtouchv3.lib.http.IRequestResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jin Qian on 2/2/2015.
 */
public class WeatherData implements IRequestResponse, Serializable {
    @SerializedName("status")
    private String mStatus;

    @SerializedName("weather")
    private ArrayList<Weather> mWeather;

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public ArrayList<Weather> getWeather() {
        return mWeather;
    }

    public void setWeather(ArrayList<Weather> weather) {
        mWeather = weather;
    }

}

/**
 * Sample JSON response data as below
 */
//{
//        "status": "OK",                     //状态信息。正常返回时值为"OK"，异常时返回具体错误信息。
//        "weather": [{                       //城市天气数组。同时查询多个城市时，该数组则包含多个对象。
//        "city_name": "北京",            //城市名
//        "city_id": "CHBJ000000",        //城市ID，V2.0以上采用心知城市ID编码
//        "last_update": "2014-01-26T12:52:57+08:00", //数据更新时间。该城市的本地时间，采用国际ISO8601时间格式标准。
//        "now": {                        //实时天气
//        "text": "多云",             //天气情况文字
//        "code": "4",                //天气情况代码 (天气代码与天气图标对应说明)
//        "temperature": "1",         //当前实时温度
//        "feels_like": "-4",         //当前实时体感温度
//        "wind_direction": "南",     //风向
//        "wind_speed": "15.64",      //风速。单位：km/h
//        "wind_scale": "3",          //风力等级。根据风速计算的风力等级，参考百度百科定义：风力等级。**V1.0新增**
//        "humidity": "47",           //湿度。单位：百分比%
//        "visibility": "11.0",       //能见度。单位：公里km
//        "pressure": "1015.92",      //气压。单位：百帕hPa
//        "pressure_rising": "N/A",   //气压变化。0或steady为稳定，1或rising为升高，2或falling为降低
//        "air_quality": {            //空气质量 (需要中级或高级会员权限)
//        "city": {               //城市综合空气质量数据
//        "aqi": "59",        //空气质量指数(AQI)是描述空气质量状况的定量指数
//        "pm25": "37",       //PM2.5颗粒物（粒径小于等于2.5μm）1小时平均值。单位：μg/m³
//        "pm10": "66",       //PM10颗粒物（粒径小于等于10μm）1小时平均值。单位：μg/m³
//        "so2": "52",        //二氧化硫1小时平均值。单位：μg/m³
//        "no2": "39",        //二氧化氮1小时平均值。单位：μg/m³
//        "co": "0.742",      //一氧化碳1小时平均值。单位：mg/m³
//        "o3": "35",         //臭氧1小时平均值。单位：μg/m³
//        "quality": "良",    //空气质量类别，有“优、良、轻度污染、中度污染、重度污染、严重污染”6类
//        "last_update": "2014-01-26T11:00:00+08:00" //数据发布时间
//        },
//        "stations": [{          //该城市所有监测站数据数组（需要高级会员权限）
//        "aqi": "54",
//        "pm25": "22",
//        "pm10": "57",
//        "so2": "50",
//        "no2": "0",
//        "co": "0.5",
//        "o3": "13",
//        "station": "万寿西宫",  //检测点名称
//        "last_update": "2014-01-26T11:00:00+08:00"
//        }, {
//        "aqi": "71",
//        "pm25": "47",
//        "pm10": "92",
//        "so2": "33",
//        "no2": "31",
//        "co": "0.7",
//        "o3": "35",
//        "station": "定陵",
//        "last_update": "2014-01-26T11:00:00+08:00"
//        }]
//        },
//        "alarms": [{               //气象灾害预警（需要大客户会员权限，当该城市当前无预警信息时，此数组为空。）
//        "title": "北京市气象台发布霾黄色预警",
//        "type": "霾",
//        "level": "黄色",
//        "status": "预警中",
//        "description": "预计今天白天到夜间，北京市气象条件不利于空气污染物的稀释、扩散和消除，有轻度到中度霾，局地有重度霾，请防范。",
//        "pubdate": "2014-01-26T07:00:36+08:00"
//        }]
//        },
//        "today": {                      //今日相关数据
//        "sunrise": "7:29 AM",       //日出时间
//        "sunset": "5:26 PM",        //日落时间
//        "suggestion": {             //生活建议指数 **V1.0新增**
//        "dressing": {           //穿衣指数
//        "brief": "薄冬衣",   //简要建议
//        "details": "棉衣、冬大衣、皮夹克、内着衬衫或羊毛内衣、毛衣、外罩大衣" //详细建议
//        },
//        "uv": {                 //紫外线指数
//        "brief": "弱",
//        "details": "紫外线弱"
//        },
//        "car_washing": {        //洗车指数
//        "brief": "非常适宜",
//        "details": "洗车后至少未来4天内没有降水、大风、沙尘天气，保洁时间长，非常适宜洗车。"
//        },
//        "travel": {             //旅游指数
//        "brief": "不太适宜",
//        "details": "天气较冷，不太适宜旅游；"
//        },
//        "flu": {                //感冒指数
//        "brief": "多发期",
//        "details": "天气较冷，室内外温差较大，较易引起感冒；"
//        },
//        "sport": {              //运动指数
//        "brief": "不适宜",
//        "details": "天气较冷，多数人不适宜户外运动；"
//        }
//        }
//        },
//        "future": [{                    //天气预报数组 (初级会员返回3天，中级会员5天，高级会员国内7天国际10天)
//        "date": "2014-01-26",       //日期
//        "day": "周日",              //星期
//        "text": "多云",             //天气情况文字 (白天和晚间通过斜线“/”分割。若白天晚间情况一致，则只保留一个值)
//        "code1": "4",               //白天天气情况代码 (天气代码与天气图标对应说明)
//        "code2": "4",               //晚间天气情况代码 (天气代码与天气图标对应说明)
//        "high": "4",                //最高温度 (无数据时为"-")
//        "low": "-5",                //最低温度 (无数据时为"-")
//        "cop": "0%",                //降水概率
//        "wind": "微风小于3级"        //风向风力 **V1.0新增**
//        }, {                            //第二天天气预报
//        "date": "2014-01-27",
//        "day": "周一",
//        "text": "多云/晴",
//        "code1": "4",
//        "code2": "1",
//        "high": "10",
//        "low": "-6",
//        "cop": "0%",
//        "wind": "微风小于3级"
//        }]
//        }]
//        }
