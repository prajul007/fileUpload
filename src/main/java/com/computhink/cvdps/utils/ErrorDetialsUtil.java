package com.computhink.cvdps.utils;

import com.computhink.cvdps.model.ErrorDetails;

import java.util.Arrays;

import static com.computhink.cvdps.constants.ApplicationConstants.FILE_UPLOAD;
import static com.computhink.cvdps.constants.ApplicationConstants.GET_TASK_LOGS;
import static com.computhink.cvdps.constants.ApplicationConstants.GET_TASK_STATUS;

public class ErrorDetialsUtil {
    public static ErrorDetails setErrorDetailsForUpload(String taskId, String fileName, StackTraceElement[] stackTrace, String clientId) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setTaskId(taskId);
        errorDetails.setFileName(fileName);
        errorDetails.setUserId(clientId);
        errorDetails.setStackTrace(Arrays.toString(stackTrace));
        errorDetails.setCreateTs(DateUtil.getCurrentTimeStamp());
        errorDetails.setType(FILE_UPLOAD);
        return errorDetails;
    }

    public static ErrorDetails setErrorDetailsForGetTaskStatus(String taskId, StackTraceElement[] stackTrace, String clientId) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setTaskId(taskId);
        errorDetails.setUserId(clientId);
        errorDetails.setStackTrace(Arrays.toString(stackTrace));
        errorDetails.setCreateTs(DateUtil.getCurrentTimeStamp());
        errorDetails.setType(GET_TASK_STATUS);
        return errorDetails;
    }

    public static ErrorDetails setErrorDetailsForGetTaskLogs(StackTraceElement[] stackTrace, String clientId) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setUserId(clientId);
        errorDetails.setStackTrace(Arrays.toString(stackTrace));
        errorDetails.setCreateTs(DateUtil.getCurrentTimeStamp());
        errorDetails.setType(GET_TASK_LOGS);
        return errorDetails;
    }

}
