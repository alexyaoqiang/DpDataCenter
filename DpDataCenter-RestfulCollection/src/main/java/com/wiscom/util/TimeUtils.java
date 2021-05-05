package com.wiscom.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String dateToString1(Date date) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);

    }
}
