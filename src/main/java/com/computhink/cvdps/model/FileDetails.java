package com.computhink.cvdps.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileDetails {
    String taskId;
    String fileName;
    String status;
    LocalDateTime receivedTs;
    LocalDateTime completionTs;
    String clientIpAddress;
    String userId;
    String uploadDir;
    String fileExtension;
    String taskResult;
    String resultDetails;
}
