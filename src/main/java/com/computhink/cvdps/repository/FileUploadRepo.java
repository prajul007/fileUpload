package com.computhink.cvdps.repository;


import com.computhink.cvdps.model.FileDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FileUploadRepo extends MongoRepository<FileDetails,String> {

    @Query(value = "{'receivedTs' : { $gte: ?0, $lte: ?1 } }")
    public Page<FileDetails> getTaskIdByDate(LocalDateTime from, LocalDateTime to, Pageable pageable);

    FileDetails findByTaskId(String taskId);
    Page<FileDetails> findByClientId(String userId, Pageable pageable);
}
