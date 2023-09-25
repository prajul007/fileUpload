package com.computhink.cvdps.service;

import com.computhink.cvdps.model.DateFilterRequestBody;
import com.computhink.cvdps.model.FileDetails;
import com.computhink.cvdps.model.FileDetailsResponse;
import com.computhink.cvdps.model.UploadFileResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface FileUploadService {


    UploadFileResponse storeFile(MultipartFile file, String clientId, String ipAddress) throws IOException ;

    Page<FileDetailsResponse> filterByTimestamp(DateFilterRequestBody date,Integer from);

    FileDetails getFileDetailsFilterByTaskId(String taskId);
    Page<FileDetails> getFileDetailsFilterByUserId(String userId,Integer from);
}
