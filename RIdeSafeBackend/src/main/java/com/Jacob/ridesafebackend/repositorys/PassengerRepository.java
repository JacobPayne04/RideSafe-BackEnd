package com.Jacob.ridesafebackend.repositorys;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.Passenger;

@Repository
public interface PassengerRepository extends CrudRepository<Passenger, String> {
	List<Passenger> findAll();
	
	Passenger findByEmail(String email);
}
