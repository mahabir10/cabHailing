package com.cabhailing.cab;

public class Request {
    
    private int rideId;
    private int sourceLoc;
    
    public int getSourceLoc() {
        return sourceLoc;
    }

    public void setSourceLoc(int sourceLoc) {
        this.sourceLoc = sourceLoc;
    }

    private int destinationLoc;
    
    public Request(int rideId, int sourceLoc,int destinationLoc) {
        this.rideId = rideId;
        this.destinationLoc = destinationLoc;
        this.sourceLoc = sourceLoc;
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
