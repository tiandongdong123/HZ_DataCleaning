package com.wanfang.datacleaning.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/8 18:04 
 *  @Version  V1.0   
 */
public class DateUtils {

    /**
     * 年份格式：yyyy
     */
    public static final String YEAR_FORMAT = "yyyy";
    /**
     * 月份格式：M
     */
    public static final String MONTH_M_FORMAT = "M";
    /**
     * 月份格式：MM
     */
    public static final String MONTH_FORMAT = "MM";
    /**
     * 时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期格式：yyyy-MM-dd
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private DateUtils() {
    }

    /**
     * 将Date格式化成String
     *
     * @param date   日期
     * @param format 格式
     * @return String
     */
    public static String format(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.format(date);
    }

    /**
     * 获取当前时间
     *
     * @return long
     */
    public static String getCurrentTime() {
        return format(new Date(), TIME_FORMAT);
    }

    /**
     * 获取当前时间戳（秒）
     *
     * @return long
     */
    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }
}
