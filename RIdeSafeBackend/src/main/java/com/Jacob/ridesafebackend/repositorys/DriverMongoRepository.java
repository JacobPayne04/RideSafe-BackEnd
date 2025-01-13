package com.Jacob.ridesafebackend.repositorys;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.Driver;

@Repository
public interface DriverMongoRepository extends MongoRepository<Driver, String> {
	  // MongoDB-specific query with projection
    List<Driver> findByIsOnlineTrue();
}
