package com.computhink.cvdps.service;

import com.computhink.cvdps.model.DateFilterRequestBody;
import com.computhink.cvdps.model.FileDetails;
import com.computhink.cvdps.model.UploadFileResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface FileUploadService {


    UploadFileResponse storeFile(MultipartFile file, String clientId, String ipAddress) throws IOException ;

    Page<FileDetails> filterByTimestamp(DateFilterRequestBody date, Integer from, String name);

    FileDetails getFileDetailsFilterByTaskId(String taskId, String name);
    Page<FileDetails> getFileDetailsFilterByUserId(String userId,Integer from);
}
