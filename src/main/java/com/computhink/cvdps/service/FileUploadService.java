package com.computhink.cvdps.service;

import com.computhink.cvdps.model.DateFilterRequestBody;
import com.computhink.cvdps.model.FileDetails;
import com.computhink.cvdps.model.FileDetailsResponse;
import com.computhink.cvdps.model.UploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface FileUploadService {


    UploadFileResponse storeFile(MultipartFile file, String clientId, String ipAddress) throws IOException ;

    List<FileDetailsResponse> filterByTimestamp(DateFilterRequestBody date);

    FileDetails getFileDetailsFilterByTaskId(String taskId);
    List<FileDetails> getFileDetailsFilterByUserId(String userId);
}
