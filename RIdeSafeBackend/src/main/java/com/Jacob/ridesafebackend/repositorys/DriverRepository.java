package com.Jacob.ridesafebackend.repositorys;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.Driver;

@Repository
public interface DriverRepository extends CrudRepository<Driver,String> {
	List<Driver> findAll();

	Driver findByEmail(String email);

}

