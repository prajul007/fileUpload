package com.computhink.cvdps.repository;


import com.computhink.cvdps.model.FileDetails;
import com.computhink.cvdps.model.FileDetailsResponse;
import com.computhink.cvdps.model.Users.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileUploadRepo extends MongoRepository<FileDetails,String> {

    @Query(value = "{'receivedTs' : { $gte: ?0, $lte: ?1 } }",fields="{ 'taskId' : 1, 'fileName': 1, 'status': 1, 'userId': 1 }")
    public Page<FileDetailsResponse> getTaskIdByDate(LocalDateTime from, LocalDateTime to, Pageable pageable);

    FileDetails findByTaskId(String taskId);
    Page<FileDetails> findByUserId(String userId,Pageable pageable);
}
