package com.cabhailing.rideservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RideserviceApplication {

	private final CabService cabService;

	@Autowired
	public RideserviceApplication(CabService cabService){
		this.cabService = cabService;
	}

	public static void main(String[] args) {
		SpringApplication.run(RideserviceApplication.class, args);
	}

	@GetMapping("/getCabs")
	public List<Cab> getCabs() {

		return cabService.getCabs();

	}


	@GetMapping("/rideEnded")
	public boolean rideEnded(@RequestParam int rideId) {
		/*
		 * If the rideId corresponds to an ongoing ride.
		 * So the rideId should match and the cab should be in ongoing state
		 * If there is then make the ride end. This requires update
		 * Then send true. False Otherwise.
		 */

		List<Cab> cabWithRideId = this.cabService.findByRideId(rideId);

		if(cabWithRideId.size() == 1){
			
			Cab cab = cabWithRideId.get(0);

			if(cab.getState() == 2){

				cab.setState(0);
				cab.setPosition(cab.getDestinationLoc());
				cab.setRideId(-1);
				cab.setCustId(-1);
				cab.setDestinationLoc(-1);
				this.cabService.saveCab(cab);

				return true;
			}
			else{
				return false;
			}

		}
		else{

			return false;

		}
	}

	@GetMapping("/cabSignsIn")
	public boolean cabSignsIn(@RequestParam int cabId, @RequestParam int initialPos) {
		/*
		 * If the cabId is a valid one and the cab is not already in signedIn state
		 * Requires Update
		*/

		List<Cab> cabsWithCabId = this.cabService.findByCabId(cabId);

		if(cabsWithCabId.size() == 1){

			Cab cab = cabsWithCabId.get(0);

			if(cab.getState() == -1){

				cab.setState(0);
				cab.setPosition(initialPos);
				this.cabService.saveCab(cab);
				return true;
			}
			else{
				return false;
			}

		}
		else{
			return false;
		}
	}

	@GetMapping("/cabSignsOut")
	public boolean cabSignsOut(@RequestParam int cabId){
		/*
		 * Response is trye iff cabId is valid and the 
		 * Cab is in available state.
		 * Requires Update
		*/

		List<Cab> cabsWithCabId = this.cabService.findByCabId(cabId);

		if(cabsWithCabId.size() == 1){

			Cab cab = cabsWithCabId.get(0);

			if(cab.getState() == 0){

				cab.setState(-1);
				cab.setPosition(-1);
				cab.setCustId(-1);
				cab.setDestinationLoc(-1);
				cab.setRideId(-1);
				this.cabService.saveCab(cab);
				return true;
			}
			else{
				return false;
			}

		}
		else{
			return false;
		}
	}

	@GetMapping("/requestRide")
	public int requestRide(@RequestParam int custId, @RequestParam int sourceLoc, @RequestParam int destinationLoc){
		/*
		 * This is the core logic of the implementation.
		 */
		return -1;
	}

	@GetMapping("/getCabStatus")
	public String getCabStatus(@RequestParam int cabId) {
		/*
		 * Returns a tuple of strings indicating the current state of the cab
		 * So get the cab object from the service layer. Form
		 * the out put here and send the output out
		 * (state last_known_position custId destinationLoc)
		 */
		List<Cab> cabs = this.cabService.findByCabId(cabId);

		if(cabs.isEmpty()){
			return "-1 -1 -1 -1";
		}
		else{
			Cab cab = cabs.get(0);
			return cab.getState() + " " + cab.getPosition() + " " + cab.getCustId() + " " + cab.getDestinationLoc();
		}

	}

	@GetMapping("/reset")
	public void reset() {
		/*
		 * 1. Give Cab.rideEnded to all the cabs that are in giving-ride state
		 * 2. Send Cab.signOut requests to all cabs that are in signedIn state
		 */

		cabService.reset();
	}

}
