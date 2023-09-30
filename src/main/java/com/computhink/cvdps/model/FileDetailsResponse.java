package com.computhink.cvdps.model;

import lombok.Data;

@Data
public class FileDetailsResponse {
    String taskId;
    String fileName;
    String status;
    String clientId;
}
