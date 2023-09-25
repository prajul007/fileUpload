package com.computhink.cvdps.controller;


import com.computhink.cvdps.model.DateFilterRequestBody;
import com.computhink.cvdps.model.FileDetails;
import com.computhink.cvdps.model.FileDetailsResponse;
import com.computhink.cvdps.model.Users.UserInfo;
import com.computhink.cvdps.repository.UserInfoRepository;
import com.computhink.cvdps.service.FileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    UserInfoRepository userInfoRepository;

    @PostMapping("/uploadSingleFile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> uploadSingleFile(@RequestParam("file") MultipartFile file,
                                                               HttpServletRequest httpServletRequest,
                                                               Authentication authentication) {
        if(file.getSize()>1048576){
            return new ResponseEntity<>("File Size should not be more than 1MB. ",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByEmail(authentication.getName());
        if(userInfoOptional.isPresent()){
            if(userInfoOptional.get().getClientIpAddress().equals(httpServletRequest.getRemoteAddr())){
                try{
                    return new ResponseEntity<>(fileUploadService.storeFile(file,authentication.getName(),httpServletRequest.getRemoteAddr()), HttpStatus.OK);
                } catch (Exception ex){
                    log.error("Exception Occurred while uploading single file to the root folder: ", ex);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Unable to match the ipAddress. Please Contact Admin.",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("Unable to upload file. Please Contact admin.",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/uploadMultipleFiles")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<ResponseEntity<?>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                                       Authentication authentication,
                                                       HttpServletRequest httpServletRequest) {
        return Arrays.asList(files)
                .stream()
                .map(file -> {
                    if(file.getSize()>1048576){
                        return new ResponseEntity<>("File Size should not be more than 1MB. ",HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    Optional<UserInfo> userInfoOptional = userInfoRepository.findByEmail(authentication.getName());
                    if(userInfoOptional.isPresent()){
                        if(userInfoOptional.get().getClientIpAddress().equals(httpServletRequest.getRemoteAddr())){
                            try{
                                return new ResponseEntity<>(fileUploadService.storeFile(file,authentication.getName(),httpServletRequest.getRemoteAddr()), HttpStatus.OK);
                            } catch (Exception ex){
                                log.error("Exception Occurred while uploading single file to the root folder: ", ex);
                                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        } else {
                            return new ResponseEntity<>("Unable to match the ipAddress. Please Contact Admin.",HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                    return new ResponseEntity<>("Unable to upload file. Please Contact admin.",HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .collect(Collectors.toList());
    }


    @PostMapping("/filterByTimestamp")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<FileDetailsResponse>> filterByTimestamp(@RequestBody DateFilterRequestBody dateFilterRequestBody) {
        try{
            return new ResponseEntity<>(fileUploadService.filterByTimestamp(dateFilterRequestBody),HttpStatus.OK);
        } catch (Exception ex){
            log.error("Exception Occurred while fetching file details based on timestamp from the db: ", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/fileDetails")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<ResponseEntity<?>> getFileDetailsFilterByTaskId(@RequestBody List<String> taskIds) {
        return taskIds.stream()
                .map(taskId -> {
                            try{
                                return new ResponseEntity<>(fileUploadService.getFileDetailsFilterByTaskId(taskId),HttpStatus.OK);
                            } catch (Exception ex){
                                log.error("Exception Occurred while fetching file details based on taskId from the db: ", ex);
                                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                }).collect(Collectors.toList());
    }

    @PostMapping("/fileUploadedByMe")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<FileDetails>> getFileDetailsFilterByUser(Authentication authentication) {
        try{
            return new ResponseEntity<>(fileUploadService.getFileDetailsFilterByUserId(authentication.getName()),HttpStatus.OK);
        } catch (Exception ex){
            log.error("Exception Occurred while fetching file details based on user from the db: ", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
