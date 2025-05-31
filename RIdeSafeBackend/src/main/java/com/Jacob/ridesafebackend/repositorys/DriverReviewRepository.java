package com.Jacob.ridesafebackend.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.DriverReview;

@Repository
public interface DriverReviewRepository extends JpaRepository<DriverReview, String> {
    List<DriverReview> findByDriverId(String driverId);
}
