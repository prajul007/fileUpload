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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    FileUploadRepo fileUploadRepo;

    @Autowired
    CustomFileUploadRepo customFileUploadRepo;

    @Value("${base.path}")
    private String basePath;

    @Override
    public UploadFileResponse storeFile(MultipartFile file,String clientId, String ipAddress){
        try {
            String taskId= UUID.randomUUID().toString();
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String uploadDirectory = getUploadDirectoryBasedOnFilePrefix(fileName);
            fileUploadRepo.save(setFileDetails(taskId, fileName,clientId,ipAddress,extension,uploadDirectory));
            uploadFile(file,taskId,extension,uploadDirectory);
            customFileUploadRepo.updateFileUploadStatus(ApplicationConstants.STATUS_PENDING,ApplicationConstants.STATUS_DESC_PENDING,taskId);
            return setUploadFileResponse(file,taskId,fileName);
        } catch (Exception ex) {
            throw new RuntimeException("Exception occurred! + ",ex);
        }
    }

    private String getUploadDirectoryBasedOnFilePrefix(String fileName) {
        if(fileName==null) {
            log.error("FileUpload Exception Occurred while uploading file to the root folder: ");
            throw new RuntimeException("FileUpload Exception Occurred while uploading file to the root folder: ");
        }
        if(fileName.length() > 7){
            String isPublic = org.apache.commons.lang3.StringUtils.substring(fileName, 0, 6);
            if(isPublic.equals("public")) {
                return ApplicationConstants.FILE_UPLOAD_PUBLIC_DIR;
            }
        }
        if(fileName.length() > 8){
            String isPrivate = org.apache.commons.lang3.StringUtils.substring(fileName, 0, 7);
            if(isPrivate.equals("private")) {
                return ApplicationConstants.FILE_UPLOAD_PRIVATE_DIR;
            }
        }
        return ApplicationConstants.FILE_UPLOAD_DEFAULT_DIR;

    }

    private UploadFileResponse setUploadFileResponse(MultipartFile file, String taskId, String fileName) {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setFileName(fileName);
        uploadFileResponse.setTaskId(taskId);
        uploadFileResponse.setSize(file.getSize());
        uploadFileResponse.setFileType(file.getContentType());
        return uploadFileResponse;
    }

    private void uploadFile(MultipartFile file, String taskId, String extension, String uploadDirectory){

        try {
            Path fileStorageLocation = Paths.get(basePath + "/" + uploadDirectory).toAbsolutePath().normalize();
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

    private FileDetails setFileDetails(String taskId, String fileName, String clientId, String ipAddress, String extension, String uploadDirectory) {
        FileDetails fileDetails = new FileDetails();
        fileDetails.setFileName(fileName);
        fileDetails.setStatus(ApplicationConstants.STATUS_IN_PROGRESSS);
        fileDetails.setReceivedTs(DateUtil.getCurrentTimeStamp());
        fileDetails.setTaskId(taskId);
        fileDetails.setClientIpAddress(ipAddress);
        fileDetails.setUserId(clientId);
        fileDetails.setTaskResult(ApplicationConstants.STATUS_DESC_IN_PROGRESSS);
        fileDetails.setUploadDir(uploadDirectory);
        fileDetails.setFileExtension(extension);
        return fileDetails;
    }

    public Page<FileDetailsResponse> filterByTimestamp(DateFilterRequestBody date,Integer from){
        Integer fromYear = date.getFromYear();
        Integer endYear = date.getEndYear() ==null ? fromYear : date.getEndYear();
        Integer fromMonth = date.getFromMonth() == null ? 1 : date.getFromMonth();
        Integer endMonth = date.getEndMonth() == null ? 12: date.getEndMonth();
        Integer fromDay = date.getFromDay() == null? 1: date.getFromDay();
        Integer endDay = date.getEndDay() == null? getNoOfDaysBasedOnMonthAndYear(endMonth, endYear) : date.getEndDay();
        Integer fromHour = date.getFromHour() == null? 0: date.getFromHour();
        Integer endHour = date.getEndHour() == null? 23: date.getEndHour();
        Integer fromMinute = date.getFromMinute() == null? 0: date.getFromMinute();
        Integer endMinute = date.getEndMinute() == null? 59: date.getEndMinute();
        Integer fromSecond = date.getFromSecond() == null? 0: date.getFromSecond();
        Integer endSecond = date.getEndSecond() == null? 59: date.getEndSecond();

        return fileUploadRepo.getTaskIdByDate(
                LocalDateTime.of(fromYear,fromMonth,fromDay,fromHour,fromMinute,fromSecond),
                LocalDateTime.of(endYear,endMonth,endDay,endHour,endMinute,endSecond),
                PageRequest.of(from,ApplicationConstants.PAGE_SIZE));
    }

    public FileDetails getFileDetailsFilterByTaskId(String taskId){
        return fileUploadRepo.findByTaskId(taskId);
    }

    public Page<FileDetails> getFileDetailsFilterByUserId(String userId,Integer from){
        return fileUploadRepo.findByUserId(userId, PageRequest.of(from,ApplicationConstants.PAGE_SIZE));
    }

    public Integer getNoOfDaysBasedOnMonthAndYear(Integer month, Integer year){
        if(month == 2){
            if((year % 400 == 0) || (year % 100 != 0) && (year % 4 == 0)) return 29;
            return 28;
        }
        List<Integer> datesBasedOnMonths = Arrays.asList(31,28,31,30,31,30,31,31,30,31,30,31);
        return datesBasedOnMonths.get(month-1);
    }
}
