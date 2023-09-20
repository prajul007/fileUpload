package com.computhink.cvdps.service.impl;

import com.computhink.cvdps.constants.ApplicationConstants;
import com.computhink.cvdps.exceptions.FileUploadException;
import com.computhink.cvdps.model.DateFilterRequestBody;
import com.computhink.cvdps.model.FileDetails;
import com.computhink.cvdps.model.FileDetailsResponse;
import com.computhink.cvdps.model.UploadFileResponse;
import com.computhink.cvdps.repository.CustomFileUploadRepo;
import com.computhink.cvdps.repository.FileUploadRepo;
import com.computhink.cvdps.service.FileUploadService;
import com.computhink.cvdps.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    FileUploadRepo fileUploadRepo;

    @Autowired
    CustomFileUploadRepo customFileUploadRepo;

    @Override
    public UploadFileResponse storeFile(MultipartFile file,String clientId, String ipAddress){
        try {
            String taskId= UUID.randomUUID().toString();
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            fileUploadRepo.save(setFileDetails(taskId, fileName,clientId,ipAddress));
            uploadFile(file,taskId,fileName);
            customFileUploadRepo.updateFileUploadStatus(ApplicationConstants.STATUS_FINISHED,ApplicationConstants.STATUS_DESC_FINISHED,taskId);
            return setUploadFileResponse(file,taskId,fileName);
        } catch (Exception ex) {
            throw new RuntimeException("Exception occurred! + ",ex);
        }
    }

    private UploadFileResponse setUploadFileResponse(MultipartFile file, String taskId, String fileName) {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setFileName(fileName);
        uploadFileResponse.setTaskId(taskId);
        uploadFileResponse.setSize(file.getSize());
        uploadFileResponse.setFileType(file.getContentType());
        return uploadFileResponse;
    }

    private void uploadFile(MultipartFile file,String taskId, String fileName){
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(ApplicationConstants.FILE_UPLOAD_DESTINATION).toAbsolutePath().normalize();
            try {
                Files.createDirectories(fileStorageLocation);
            } catch (Exception ex) {
                throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
            }
            Path targetLocation = fileStorageLocation.resolve(taskId + "." + extension);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception exception) {
            log.error("FileUpload Exception Occurred while uploading file to the root folder: ",exception );
            throw new FileUploadException(exception);
        }
    }

    private FileDetails setFileDetails(String taskId, String fileName, String clientId, String ipAddress) {
        FileDetails fileDetails = new FileDetails();
        fileDetails.setFileName(fileName);
        fileDetails.setStatus(ApplicationConstants.STATUS_IN_PROGRESSS);
        fileDetails.setReceivedTs(DateUtil.getCurrentTimeStamp());
        fileDetails.setTaskId(taskId);
        fileDetails.setClientIpAddress(ipAddress);
        fileDetails.setUserId(clientId);
        fileDetails.setStatusDesc(ApplicationConstants.STATUS_DESC_IN_PROGRESSS);
        return fileDetails;
    }

    public List<FileDetailsResponse> filterByTimestamp(DateFilterRequestBody date){
        return fileUploadRepo.getTaskIdByDate(
                LocalDateTime.of(date.getFromYear(), date.getFromMonth(), date.getFromDay(), date.getFromHour(), date.getFromMinute(), date.getFromSecond()),
                LocalDateTime.of(date.getEndYear(),date.getEndMonth(), date.getEndDay(), date.getEndHour(), date.getEndMinute(), date.getEndSecond()));
    }

    public FileDetails getFileDetailsFilterByTaskId(String taskId){
        return fileUploadRepo.findByTaskId(taskId);
    }

    public List<FileDetails> getFileDetailsFilterByUserId(String userId){
        return fileUploadRepo.findByUserId(userId);
    }
}
