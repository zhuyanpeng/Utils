package com.study.www;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * com.study.www.DateUtils: 时间工具类
 *
 * @author : Administrator.zhuyanpeng
 * @date : 2017/11/14    9:35
 **/
public class DateUtils {
    public static String YYYYMMDDHHMMSS="yyyyMMddHHmmss";
    public static String YYYYMMDDHHMMSSSSS="yyyyMMddHHmmssSSS";
    public static String DEFAULT_YYYYMMDDHHMMSS="yyyy-MM-dd HH:mm:ss";
    public static String YYYYMMDD="yyyyMMdd";
    public static String YYYYMMDDINTER="yyyy-MM-dd";

    /**
     * 获得当前时间按照 yyyy-MM-dd HH:mm:ss打印
     * @return
     */
    public static String getNowDateStr(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_YYYYMMDDHHMMSS);
        return simpleDateFormat.format(new Date());
    }

    public static String getDateFormat(Date date, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
    public static Date getStringFormat(String str,String format){
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            date = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date ;
    }

}
