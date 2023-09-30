package com.computhink.cvdps.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDetails {
    String taskId;
    String fileName;
    String stackTrace;
    LocalDateTime createTs;
    String userId;
}
