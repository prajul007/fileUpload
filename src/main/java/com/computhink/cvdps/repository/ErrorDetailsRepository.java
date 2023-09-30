package com.computhink.cvdps.repository;

import com.computhink.cvdps.model.ErrorDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ErrorDetailsRepository extends MongoRepository<ErrorDetails,String> {

}
