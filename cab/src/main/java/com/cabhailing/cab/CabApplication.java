package com.cabhailing.cab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CabApplication {


	HashMap<Integer, Cab> cabs;

	public CabApplication(){
		/*
		 * The purpose of this constructor is to read the test file and initialize the cabs with their cabIds
		 */

		String val = System.getenv("Docker_Env");
		System.out.println("Enviromet value = " + val);

		this.cabs = new HashMap<Integer, Cab>();
		int stars = 0;

		try {
						
			InputStream is = null;
			BufferedReader reader;

			if(val == null){
				// This is not docker
				is = getClass().getClassLoader().getResourceAsStream("test.txt");
				reader = new BufferedReader(new InputStreamReader(is));
			}
			else{
				// This means we are inside docker
				String docker_test_path = "./test.txt";
				FileReader in = new FileReader(docker_test_path);
				reader = new BufferedReader(in);
			}

			String data;

			while ((data = reader.readLine()) != null) {

				System.out.println(data);

				if(data.charAt(0) == '*'){
					stars++;
				}

				if(stars == 1 && data.charAt(0) != '*'){
					// Got the cabId
					Integer cabid = Integer.parseInt(data);
					this.cabs.put(cabid, new Cab());
				}

			}
			reader.close();

			if(is != null)
			{
				is.close();
			}

		} catch (Exception e) {
			System.out.println("An error occurred. Reading the test file");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(CabApplication.class, args);
	}


	@GetMapping("/requestRide")
	public int requestRide(@RequestParam int cabId, @RequestParam int rideId, @RequestParam int sourceLoc, @RequestParam int destinationLoc){
		return 0;
	}

	@GetMapping("/rideStarted")
	public boolean rideStarted(@RequestParam int cabId, @RequestParam int rideId){

		/*
			This request is triggered by RideService.requestRide. If cabId is valid
			and if this cab is currently in committed state due to a previously
			received Cab.requestRide request for the same rideId, then move into
			giving-ride state and return true, otherwise do not change state and
			return false.
		 */

		if(this.cabs.containsKey(cabId)){

			int state = this.cabs.get(cabId).getState();
			int rideIdSaved = this.cabs.get(cabId).getRideId();

			if(rideIdSaved == rideId && state == 1){

				this.cabs.get(cabId).setState(2);
				return true;
			}
			else{
				return false;
			}

		}
		else
		{
			return false;
		}
	}

	@GetMapping("/rideCancelled")
	public int rideCancelled(@RequestParam int cabId, @RequestParam int rideId){
		return 0;
	}

	@GetMapping("/rideEnded")
	public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId){

	}

	@GetMapping("/signIn")
	public boolean signIn(@RequestParam int cabId, @RequestParam int initialPos){

		/*
		 * This is similar to the one we have dealt below. We have not dealt it yet
		 * 
		 */

		if(this.cabs.containsKey(cabId)){

			// Then we have to check for its state
			int state = this.cabs.get(cabId).getState();

			if(state < 0){

				// Make a request to rideservice instance

				Map<String, String> test1 = Map.of(
					"cabId", Integer.toString(cabId),
					"initialPos", Integer.toString(initialPos));

				boolean response = make_request( "cabSignsOut" , 8081 , test1);

				if(response == true){
					this.cabs.get(cabId).setState(0); // Setting it to signed in
					this.cabs.get(cabId).setPosition(initialPos); // Setting the initial pos
				}

				return response;
			}
			else{
				return false;
			}

		}
		else{
			return false;
		}
	}

	@GetMapping("/signOut")
	public boolean signOut(@RequestParam int cabId){
		/*
		 * Cab Driver sends this request.
		 * If the cabId is valid and is in signedIn state, request RideService.cabSignsOut
		 * Forward the message to the Driver. 
		 * if the response was true then go to signOut stage.
		 * 
		 * If i lock the object belonging to cabId, then as we are waiting for the RideService, there will be unnecessary waiting period.
		 * Lets for now not take the locks. As all the things here are getting changed by the rideservice 
		 */

		if(this.cabs.containsKey(cabId)){

			// Then we have to check for its state
			int state = this.cabs.get(cabId).getState();

			if(state >= 0){

				// Make a request to rideservice instance

				Map<String, String> test1 = Map.of("cabId", Integer.toString(cabId));
				boolean response = make_request( "cabSignsOut" , 8081 , test1);

				if(response == true){
					this.cabs.get(cabId).setState(-1); // Setting it to signed out
				}

				return response;
			}
			else{
				return false;
			}

		}
		else{
			return false;
		}

	}

	@GetMapping("/numRides")
	public int numRides(@RequestParam int cabId){

		/*
		 * If CabId is invalid then return -1.
		 * If the cab is in signedIn state then return the no_of_rides it had since the last login.
		 * Else return 0
		 */

		if(this.cabs.containsKey(cabId)){
			return this.cabs.get(cabId).getNo_of_rides();
		}
		else{
			// This is for the invalid cabId
			return -1;
		}
		
	}

}
