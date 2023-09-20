package com.computhink.cvdps.controller;


import com.computhink.cvdps.model.DateFilterRequestBody;
import com.computhink.cvdps.model.FileDetails;
import com.computhink.cvdps.model.FileDetailsResponse;
import com.computhink.cvdps.model.UploadFileResponse;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    FileUploadService fileUploadService;

    @PostMapping("/uploadSingleFile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UploadFileResponse> uploadSingleFile(@RequestParam("file") MultipartFile file,
                                                               HttpServletRequest httpServletRequest,
                                                               Authentication authentication) {
        System.out.println(authentication.getCredentials());
        try {
            return new ResponseEntity<>(fileUploadService.storeFile(file,authentication.getName(),httpServletRequest.getRemoteAddr()), HttpStatus.OK);
        } catch (Exception ex){
            log.error("Exception Occurred while uploading single file to the root folder: ", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/uploadMultipleFiles")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                                        Authentication authentication,
                                                        HttpServletRequest httpServletRequest) {
        return Arrays.asList(files)
                .stream()
                .map(file -> {
                    try {
                        return fileUploadService.storeFile(file,authentication.getName(),httpServletRequest.getRemoteAddr());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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

    @PostMapping("/fileDetails/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<FileDetails> getFileDetailsFilterByTaskId(@PathVariable("taskId") String taskId) {
        try{
            return new ResponseEntity<>(fileUploadService.getFileDetailsFilterByTaskId(taskId),HttpStatus.OK);
        } catch (Exception ex){
            log.error("Exception Occurred while fetching file details based on taskId from the db: ", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
