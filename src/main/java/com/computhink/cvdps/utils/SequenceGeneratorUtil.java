//package com.computhink.cvdps.utils;
//
//import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
//import static org.springframework.data.mongodb.core.query.Criteria.where;
//import static org.springframework.data.mongodb.core.query.Query.query;
//
//import java.util.Objects;
//
//import com.computhink.cvdps.model.Users.UserInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//
//public class SequenceGeneratorUtil {
//
//    private MongoOperations mongoOperations;
//
//    @Autowired
//    public SequenceGeneratorService(MongoOperations mongoOperations) {
//        this.mongoOperations = mongoOperations;
//    }
//
//    public long generateSequence(String seqName) {
//
//        UserInfo counter = mongoOperations.findAll(UserInfo.class);
//        return !Objects.isNull(counter) ? counter.getSeq() : 1;
//
//    }
//}
