package com.cabhailing.rideservice;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

/*
 * This is the MODEL Layer of MVC
 * This layer is responsible to model the database.
 */
@Entity
@Table
public class Cab {
    
    @Id
    private int cabId;
    private int state;
    private int position;
    private int rideId;
    private int custId;
    private int destinationLoc;
    
    public Cab(int cabId, int state, int position, int rideId, int custId, int destinationLoc) {
        this.cabId = cabId;
        this.state = state;
        this.position = position;
        this.rideId = rideId;
        this.custId = custId;
        this.destinationLoc = destinationLoc;
    }

    public Cab() {
    }

    public int getCabId() {
        return cabId;
    }

    public void setCabId(int cabId) {
        this.cabId = cabId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public int getDestinationLoc() {
        return destinationLoc;
    }

    public void setDestinationLoc(int destinationLoc) {
        this.destinationLoc = destinationLoc;
    }

    @Override
    public String toString() {
        return "Cab [cabId=" + cabId + ", state=" + state + ", position=" + position + ", rideId=" + rideId
                + ", custId=" + custId + ", destinationLoc=" + destinationLoc + "]";
    }

    
}
