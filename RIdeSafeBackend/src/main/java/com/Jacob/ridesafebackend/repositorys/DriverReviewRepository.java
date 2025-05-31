package com.Jacob.ridesafebackend.repositorys;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.DriverReview;

@Repository
public interface DriverReviewRepository extends MongoRepository<DriverReview, String> {
    List<DriverReview> findByDriverId(String driverId);
}
