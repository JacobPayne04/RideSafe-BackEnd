package com.Jacob.ridesafebackend.repositorys;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.Jacob.ridesafebackend.models.DriverRating;

public interface DriverRatingRepository extends MongoRepository<DriverRating, String> {
    Optional<DriverRating> findByPassengerIdAndDriverId(String passengerId, String driverId);
}
