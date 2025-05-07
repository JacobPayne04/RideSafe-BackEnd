package com.Jacob.ridesafebackend.repositorys;

import java.util.List;

import com.Jacob.ridesafebackend.models.Driver;

public interface CustomDriverRepository {

    List<Driver> findDriversNearLocation(double longitude, double latitude);
}
