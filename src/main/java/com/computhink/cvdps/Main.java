package com.computhink.cvdps;

import com.computhink.cvdps.utils.DateUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World " +  DateUtil.getCurrentTimeStamp());

        SpringApplication.run(Main.class, args);
    }
}