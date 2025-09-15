package com.Jacob.ridesafebackend.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.Passenger;

@Repository
public interface PassengerRepository extends MongoRepository<Passenger, String> {
    List<Passenger> findAll();
    
    @Query("{ 'email' : ?0 }")
    List<Passenger> findAllByEmail(String email);

    Optional<Passenger> findPassengerByGoogleId(String googleId);
}
