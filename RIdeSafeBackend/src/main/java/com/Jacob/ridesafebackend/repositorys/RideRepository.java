package com.Jacob.ridesafebackend.repositorys;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.Ride;
@Repository
public interface RideRepository extends CrudRepository<Ride, String> {
	List<Ride> findAll();
	List<Ride> findByDriverIdAndStatus(String driverId, Ride.RideStatus status);
}