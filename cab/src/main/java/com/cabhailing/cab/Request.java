package com.cabhailing.cab;

public class Request {
    
    private int rideId;
    private int destinationLoc;
    
    public Request(int rideId, int destinationLoc) {
        this.rideId = rideId;
        this.destinationLoc = destinationLoc;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public int getDestinationLoc() {
        return destinationLoc;
    }

    public void setDestinationLoc(int destinationLoc) {
        this.destinationLoc = destinationLoc;
    }

}
