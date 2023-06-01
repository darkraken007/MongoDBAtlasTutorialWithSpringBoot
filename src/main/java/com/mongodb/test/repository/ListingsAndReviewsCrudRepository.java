package com.mongodb.test.repository;

import com.mongodb.test.model.ListingsAndReviews;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableMongoRepositories
public interface ListingsAndReviewsCrudRepository extends CrudRepository<ListingsAndReviews, String> {



}
