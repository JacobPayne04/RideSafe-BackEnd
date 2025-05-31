package com.Jacob.ridesafebackend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Jacob.ridesafebackend.models.DriverReview;
import com.Jacob.ridesafebackend.repositorys.DriverReviewRepository;

@Service
public class DriverReviewService {

    private final DriverReviewRepository reviewRepository;

    public DriverReviewService(DriverReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public DriverReview saveReview(DriverReview review) {
        return reviewRepository.save(review);
    }

    public List<DriverReview> getReviewsByDriverId(String driverId) {
        return reviewRepository.findByDriverId(driverId);
    }
}