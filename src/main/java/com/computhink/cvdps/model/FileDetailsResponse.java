package com.computhink.cvdps.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileDetailsResponse {
    String taskId;
    String fileName;
    String status;
    String userId;
}
