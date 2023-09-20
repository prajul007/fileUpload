package com.computhink.cvdps.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static LocalDateTime getCurrentTimeStamp(){
//        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Date date = Calendar.getInstance().getTime();
        return Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime();
    }
}
