package com.cabhailing.rideservice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CabRepository extends JpaRepository<Cab, Integer>{
    

    List<Cab> findByCabId(int cabId);
    List<Cab> findByState(int state);
    List<Cab> findByStateNot(int state);
    List<Cab> findByRideId(int rideId);

}
