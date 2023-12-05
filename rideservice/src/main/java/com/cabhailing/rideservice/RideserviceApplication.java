package com.cabhailing.rideservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class CabComparator implements Comparator<Cab> { 
	
	private int position;

	public CabComparator(int position) {
		this.position = position;
	}

	// override the compare() method 
	public int compare(Cab c1, Cab c2) 
	{ 
		// Now we have to calculate the distance betweec the c1's postition and current postition
		int dist1 = Math.abs(position - c1.getPosition());
		int dist2 = Math.abs(position - c2.getPosition());
		
		return dist1-dist2;
	} 
}

@SpringBootApplication
@RestController
public class RideserviceApplication {

	private final CabRepository cabRepository;

	@Autowired
	public RideserviceApplication(CabRepository cabRepository){
		this.cabRepository = cabRepository;
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
		SpringApplication.run(RideserviceApplication.class, args);
	}

	@GetMapping("/getCabs")
	public List<Cab> getCabs() {

		return this.cabRepository.findAll();

	}


	@GetMapping("/rideEnded")
	public boolean rideEnded(@RequestParam int rideId) {
		/*
		 * If the rideId corresponds to an ongoing ride.
		 * So the rideId should match and the cab should be in ongoing state
		 * If there is then make the ride end. This requires update
		 * Then send true. False Otherwise.
		 */

		List<Cab> cabWithRideId = this.cabRepository.findByRideId(rideId);

		if(cabWithRideId.size() == 1){
			
			Cab cab = cabWithRideId.get(0);

			if(cab.getState() == 2){

				cab.setState(0);
				cab.setPosition(cab.getDestinationLoc());
				cab.setRideId(-1);
				cab.setCustId(-1);
				cab.setDestinationLoc(-1);
				this.cabRepository.save(cab);

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

		List<Cab> cabsWithCabId = this.cabRepository.findByCabId(cabId);

		if(cabsWithCabId.size() == 1){

			Cab cab = cabsWithCabId.get(0);

			if(cab.getState() == -1){

				cab.setState(0);
				cab.setPosition(initialPos);
				this.cabRepository.save(cab);
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

		List<Cab> cabsWithCabId = this.cabRepository.findByCabId(cabId);

		if(cabsWithCabId.size() == 1){

			Cab cab = cabsWithCabId.get(0);

			if(cab.getState() == 0){

				cab.setState(-1);
				cab.setPosition(-1);
				cab.setCustId(-1);
				cab.setDestinationLoc(-1);
				cab.setRideId(-1);
				this.cabRepository.save(cab);
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
		 * 1. First generate a globally unique rideId
		 * 
		 */
		Random rand = new Random(System.currentTimeMillis());
		int rideId = rand.nextInt(1000000000); // Generating random upto 10^9, We can take the current time in millis also

		List<Cab> allCabs = this.cabRepository.findAll();
		Collections.sort(allCabs, new CabComparator(sourceLoc));


		// Now we have to ask the cabs in increasing order of their distance
		int asked = 0;
		for(Cab cab : allCabs){

			if(cab.getState() == 0){

				// We have to send the current cab the cab.getRequest()
				int cabId = cab.getCabId();
				Map<String, String> test1 = Map.of(
							"cabId", Integer.toString(cabId),
							"rideId", Integer.toString(rideId),
							"sourceLoc", Integer.toString(sourceLoc),
							"destinationLoc", Integer.toString(destinationLoc));
				String url_response = make_request( "requestRide" , 8080 , test1, "false");
				boolean response = Boolean.parseBoolean(url_response); // This is not used as per the requirement

				if(response == true){
					// This means that the cab has accepted the request.

					// We need to calculate fare, and try to cut the amount from the customers wallet
					int cab_position = cab.getPosition();
					int fare  = 10*( Math.abs(cab_position - sourceLoc) + Math.abs(sourceLoc - destinationLoc) ); 

					// We need to cut the fare from the customers wallet.
					Map<String, String> test2 = Map.of(
							"custId", Integer.toString(custId),
							"amount", Integer.toString(fare));
					String url_response2 = make_request( "deductAmount" , 8082 , test2, "false");
					boolean cut_possible = Boolean.parseBoolean(url_response2); // This is not used as per the requirement

					if(cut_possible){
						
						// We have to send rideStarted
						Map<String, String> test3 = Map.of(
							"cabId", Integer.toString(cabId),
							"rideId", Integer.toString(rideId));
						String url_response3 = make_request( "rideStarted" , 8080 , test3, "false");
						boolean ride_started = Boolean.parseBoolean(url_response3); // This is not used as per the requirement
						return rideId;

					}
					else{
						// We have to send rideStarted
						Map<String, String> test3 = Map.of(
							"cabId", Integer.toString(cabId),
							"rideId", Integer.toString(rideId));
						String url_response3 = make_request( "rideCancelled" , 8080 , test3, "false");
						boolean ride_cancelled = Boolean.parseBoolean(url_response3); // This is not used as per the requirement
						
						return -1;
					}
				}

				asked++;
			}

			if(asked == 3){
				break;
			}
		}

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
		List<Cab> cabs = this.cabRepository.findByCabId(cabId);

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

        List<Cab> giving_ride_Cabs= this.cabRepository.findByState(2);
        for(Cab cab: giving_ride_Cabs){

            int cabId = cab.getCabId();
            Map<String, String> test1 = Map.of(
						"cabId", Integer.toString(cabId));
            String url_response = make_request( "rideEnded" , 8080 , test1, "false");
            boolean response = Boolean.parseBoolean(url_response); // This is not used as per the requirement
        }

        List<Cab> signedInCabs = this.cabRepository.findByStateNot(-1);
        for(Cab cab: signedInCabs){

            int cabId = cab.getCabId();
            Map<String, String> test1 = Map.of(
						"cabId", Integer.toString(cabId));
            String url_response = make_request( "signOut" , 8080 , test1, "false");
            boolean response = Boolean.parseBoolean(url_response); // This is not used as per the requirement
        }
	}

}
