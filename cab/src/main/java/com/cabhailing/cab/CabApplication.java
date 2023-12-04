package com.cabhailing.cab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URISyntaxException;

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

	private String make_request( String service, int port, Map<String, String> parameters, String return_default){
		/*
		 * The purpose of this function is to send the request to localhost:<port>/<service>?<parameters>
		 * return_default will be sent incase the request is not processed. (Or not processed in time)
		 * The above thing in bracket is not being handled. Otherwise that will create inconsistencies
		 */

		// First form the url
		String url = "https://localhost:" + port + "/" + service;

		if(!parameters.isEmpty()){

			url+="?";

			for(Map.Entry<String, String> entry: parameters.entrySet()){

				String key = entry.getKey();
				String value = entry.getValue();

				if(!url.endsWith("?")){
					url+="&";
				}
				url+=key + "=" + value;
			}
		}

		System.out.println("Url Formed = " + url);

		HttpClient client = HttpClient.newHttpClient();

		try{

			HttpRequest request = HttpRequest.newBuilder()
			.uri(new URI(url))
			.GET()
			.build();

			try{
				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				return response.body();
			}
			catch(Exception e){
				System.out.println("Got exception while sending Request, Returning default return statement");
				e.printStackTrace();
				return return_default;
			}

		}
		catch(URISyntaxException e){
			System.out.println("The URL syntax is wrong, got this error " + e);
			return "";
		}
		

	}

	public static void main(String[] args) {
		SpringApplication.run(CabApplication.class, args);
	}


	@GetMapping("/requestRide")
	public boolean requestRide(@RequestParam int cabId, @RequestParam int rideId, @RequestParam int sourceLoc, @RequestParam int destinationLoc){
		
		if(this.cabs.containsKey(cabId)){

			try{
				this.cabs.get(cabId).getWriteLock();

				int state = this.cabs.get(cabId).getState();
				
				if(state == 0){

					int no_reqs = this.cabs.get(cabId).incNo_of_reqs();

					if(no_reqs%2 == 0){

						// This means we are accepting reqs
						// We will go to committed state

						this.cabs.get(cabId).setState(1);
						this.cabs.get(cabId).setRideId(rideId);
						this.cabs.get(cabId).setSourceLoc(sourceLoc);
						this.cabs.get(cabId).setDestinationLoc(destinationLoc);

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
			finally{
				this.cabs.get(cabId).releaseWriteLock();
			}

		}
		else{
			return false;
		}

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

			try{
				this.cabs.get(cabId).getWriteLock();

				int state = this.cabs.get(cabId).getState();
				int rideIdSaved = this.cabs.get(cabId).getRideId();

				if(rideIdSaved == rideId && state == 1){
					this.cabs.get(cabId).setState(2);
					this.cabs.get(cabId).incNo_of_rides();
					this.cabs.get(cabId).setPosition(this.cabs.get(cabId).getSourceLoc());
					return true;
				}
				else{
					return false;
				}

			}
			finally{
				this.cabs.get(cabId).releaseWriteLock();
			}
		}
		else
		{
			return false;
		}
	}

	@GetMapping("/rideCancelled")
	public boolean rideCancelled(@RequestParam int cabId, @RequestParam int rideId){
		
		if(this.cabs.containsKey(cabId)){

			try{
				this.cabs.get(cabId).getWriteLock();

				int state = this.cabs.get(cabId).getState();

				if(state == 1){

					// The cab is in committed state
					int rideIdSaved = this.cabs.get(cabId).getRideId();

					if(rideIdSaved == rideId){

						this.cabs.get(cabId).setState(0);
						// this.cabs.get(cabId).setPosition(this.cabs.get(cabId).getSourceLoc());
						return true;
					}
					else{
						return false;
					}

				}
				else {
					return false;
				}
			}
			finally{
				this.cabs.get(cabId).releaseWriteLock();
			}

		}
		else{
			return false;
		}

	}

	@GetMapping("/rideEnded")
	public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId){

		if(this.cabs.containsKey(cabId)){

			try{
				this.cabs.get(cabId).getWriteLock();

				int state = this.cabs.get(cabId).getState();
				if(state == 2){

					int rideIdSaved = this.cabs.get(cabId).getRideId();

					if(rideIdSaved == rideId){
						
						// According to the requirement i will first go to available state and then send request.
						// But i would prefer asking the rideService first. If it returns true then i would go to available state and change the postition
						// But currently going with the requirement part.

						this.cabs.get(cabId).setState(0); // Setting available state
						this.cabs.get(cabId).setPosition(this.cabs.get(cabId).getDestinationLoc()); 

						Map<String, String> test1 = Map.of(
						"rideId", Integer.toString(rideId));
						String url_response = make_request( "rideEnded" , 8081 , test1, "true");
						boolean response = Boolean.parseBoolean(url_response); // This is not used as per the requirement

						assert(response == true);

						return true;
						
					}
					else{
						// We got invalid rideId. The saved rideId did not match the rideId requested
						return false;
					}
				}
				else{
					// Cab is not in giving-ride state
					return false;
				}
			}
			finally{
				this.cabs.get(cabId).releaseWriteLock();
			}
		}
		else{
			return false;
		}

	}

	@GetMapping("/signIn")
	public boolean signIn(@RequestParam int cabId, @RequestParam int initialPos){


		if(this.cabs.containsKey(cabId)){

			// Get the writeLock for the cab first

			try{
				this.cabs.get(cabId).getWriteLock();

				// Then we have to check for its state
				int state = this.cabs.get(cabId).getState();

				if(state < 0){

					// Make a request to rideservice instance

					Map<String, String> test1 = Map.of(
						"cabId", Integer.toString(cabId),
						"initialPos", Integer.toString(initialPos));

					
					String url_response = make_request( "cabSignsIn" , 8081 , test1, "false");
					boolean response = Boolean.parseBoolean(url_response);

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
			finally{
				this.cabs.get(cabId).releaseWriteLock();
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
			// First get the write lock for this object. This is because this might potentially change the states

			try{
				this.cabs.get(cabId).getWriteLock();

				int state = this.cabs.get(cabId).getState();

				if(state >= 0){

					// Make a request to rideservice instance

					Map<String, String> test1 = Map.of("cabId", Integer.toString(cabId));

					String url_response = make_request( "cabSignsOut" , 8081 , test1, "false");
					boolean response = Boolean.valueOf(url_response);

					if(response == true){
						this.cabs.get(cabId).setState(-1); // Setting it to signed out
					}

					return response;
				}
				else{
					return false;
				}
			}
			finally{
				this.cabs.get(cabId).releaseWriteLock();
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

			try{
				this.cabs.get(cabId).getReadLock();
				return this.cabs.get(cabId).getNo_of_rides();
			}
			finally{
				this.cabs.get(cabId).releaseReadLock();
			}
			
		}
		else{
			// This is for the invalid cabId
			return -1;
		}
		
	}

}
