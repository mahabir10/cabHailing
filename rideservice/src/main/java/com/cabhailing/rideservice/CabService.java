package com.cabhailing.rideservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class CabService {
    
    private final CabRepository cabRepository;
    @Autowired
    public CabService(CabRepository cabRepository) {
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

    public void saveCab(Cab cab){
        this.cabRepository.save(cab);
    }

    public List<Cab> getCabs(){
        return cabRepository.findAll();
    }

    public List<Cab> findByRideId(int rideId){
        return this.cabRepository.findByRideId(rideId);
    }

    public List<Cab> findByCabId(int cabId){
        return this.cabRepository.findByCabId(cabId);
    }

    public void reset(){
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
// new Cab(101, 0, 10, -1, -1, -1)

// To Know
// How to update the database.
