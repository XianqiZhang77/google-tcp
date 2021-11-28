package com.concordia.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String dateFormatter(Date date) {

        return formatter.format(date);

    }

    public static Date dateFormatter(String s) throws ParseException {
        return formatter.parse(s);
    }

    public static boolean checkTimeDiff(Date launchTime, Date prevTime, int W) {
        Calendar prevCalendar = Calendar.getInstance();
        prevCalendar.setTime(prevTime);
        prevCalendar.add(Calendar.HOUR, W);
        Date nowTime = prevCalendar.getTime();
        return nowTime.before(launchTime);
    }

    public static Date addRangeTime(Date date, Double range) {

        int exeTime = range.intValue();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, exeTime);

        return calendar.getTime();
    }

    public static void main(String[] args) throws ParseException {
        System.out.println((dateFormatter("2014-02-18 12:27:46.117").getTime() - dateFormatter("2014-01-01 00:00:02.000").getTime()) / 1461303);
    }

}
