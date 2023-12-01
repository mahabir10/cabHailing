package com.cabhailing.cab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

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
		
	}

	@GetMapping("/rideStarted")
	public int rideStarted(@RequestParam int cabId, @RequestParam int rideId){
		
	}

	@GetMapping("/rideCancelled")
	public int rideCancelled(@RequestParam int cabId, @RequestParam int rideId){
		
	}

	@GetMapping("/rideEnded")
	public int rideEnded(@RequestParam int cabId, @RequestParam int rideId){
		
	}

	@GetMapping("/signIn")
	public int signIn(@RequestParam int cabId, @RequestParam int initialPos){
		
	}

	@GetMapping("/signOut")
	public int signOut(@RequestParam int cabId){
		
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
