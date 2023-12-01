package com.cabhailing.cab;

public class Cab {
    
    private int state;
    private int no_of_reqs;
    private int position;
    private int no_of_rides;
    private int rideId;

    public int getRideId() {
        return rideId;
    }


    public void setRideId(int rideId) {
        this.rideId = rideId;
    }


    public Cab(){
        this.state = -1;
        this.no_of_reqs = 0;
        this.position = -1; // Means it does not matter at this point
        this.no_of_rides = 0;
        this.rideId = -1; // Means it does not matter at this point
    }


    public int getNo_of_rides() {

        /*
         * Give read lock for this
         */

        if(this.state >= 0){
            return this.no_of_rides;
        }
        else{
            return 0;
        }
    }

    public void setNo_of_rides(int no_of_rides) {
        this.no_of_rides = no_of_rides;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public int getNo_of_reqs() {
        return no_of_reqs;
    }
    public void setNo_of_reqs(int no_of_reqs) {
        this.no_of_reqs = no_of_reqs;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    
}
