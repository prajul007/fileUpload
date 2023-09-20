package com.computhink.cvdps.repository;

import com.computhink.cvdps.constants.ApplicationConstants;
import com.computhink.cvdps.model.FileDetails;
import com.computhink.cvdps.utils.DateUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class CustomFileUploadRepo {
    @Autowired
    MongoTemplate mongoTemplate;

    public void updateFileUploadStatus(String status,String statusDesc, String taskId) {
        Query query = new Query(Criteria.where(ApplicationConstants.TASK_ID).is(taskId));
        Update update = new Update();
        update.set(ApplicationConstants.STATUS, status);
        update.set(ApplicationConstants.STATUS_DESC, statusDesc);
        update.set(ApplicationConstants.COMPLETION_TS, DateUtil.getCurrentTimeStamp());

        UpdateResult result = mongoTemplate.updateFirst(query, update, FileDetails.class);

        if(result == null)
            log.info("No documents updated");
        else
            log.info(result.getModifiedCount() + " document(s) updated..");

    }
}
