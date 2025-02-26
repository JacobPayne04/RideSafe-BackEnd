package com.Jacob.ridesafebackend.repositorys;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.Driver;

@Repository
public interface DriverRepository extends CrudRepository<Driver,String> {
	List<Driver> findAll();
	
	@Query("{ 'email' : ?0 }")
	Optional<Driver> findByEmail(String email);
	
	Optional<Driver> findDriverByGoogleId(String googleId);

}

