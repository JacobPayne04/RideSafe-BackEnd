package com.Jacob.ridesafebackend.repositorys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.Jacob.ridesafebackend.models.Driver;
import org.springframework.data.geo.Point;

@Repository
public class CustomDriverRepositoryImpl  implements CustomDriverRepository {
	
	@Autowired 
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<Driver> findDriversNearLocation(double longitude, double latitude){
		Query query = new Query(Criteria.where("location")
			.nearSphere(new Point(longitude,latitude))
			.maxDistance(10.0/3963.2));
		
		query.addCriteria(Criteria.where("isOnline").is(true));
		
		return mongoTemplate.find(query, Driver.class);
	}
}
