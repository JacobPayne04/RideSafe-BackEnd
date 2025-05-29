package com.Jacob.ridesafebackend.repositorys;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.Query;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import com.Jacob.ridesafebackend.models.Driver;

@Repository
public interface DriverRepository extends MongoRepository<Driver,String>,CustomDriverRepository {
	List<Driver> findAll();
	
	@Query("{ 'email' : ?0 }")
	List<Driver> findAllByEmail(String email);
	
	Optional<Driver> findDriverByGoogleId(String googleId);
	
	
	Optional<Driver> findDriverByEmail(String email);

	List<Driver>findByIsAllowedToDriveFalse();
}

//Changed form extending crud repo to extending mongo 