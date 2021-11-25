package com.concordia.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String dateFormatter(Date date) {

        return formatter.format(date);

    }

    public static Date dateFormatter(String s) throws ParseException {
        return formatter.parse(s);
    }

}
