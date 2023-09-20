package com.computhink.cvdps.model;


import lombok.Data;

@Data
public class DateFilterRequestBody {
    int fromYear;
    int fromMonth;
    int fromDay;
    int fromHour;
    int fromMinute;
    int fromSecond;

    int endYear;
    int endMonth;
    int endDay;
    int endHour;
    int endMinute;
    int endSecond;

}
