package com.computhink.cvdps.model;


import lombok.Data;

@Data
public class DateFilterRequestBody {
    Integer fromYear;
    Integer fromMonth;
    Integer fromDay;
    Integer fromHour;
    Integer fromMinute;
    Integer fromSecond;

    Integer endYear;
    Integer endMonth;
    Integer endDay;
    Integer endHour;
    Integer endMinute;
    Integer endSecond;

}
