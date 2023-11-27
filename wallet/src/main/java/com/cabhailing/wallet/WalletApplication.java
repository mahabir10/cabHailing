package com.cabhailing.wallet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class WalletApplication {


	HashMap<Integer, Customer> customers;
	int initial_balance = 100;

	public WalletApplication(){
		/*
         *  The purpose of this constructor is to pick up the test file where we have the
         *  customer ids and their balance is mentioned. It will Make those many customers and their balances.
         *  
         *  The data structure that we will use is the hash. This will map the customer id to their objects. 
        */
        String test_file_path = "wallet\\src\\main\\resources\\test.txt";
        

		this.customers = new HashMap<Integer, Customer>();
		int stars = 0;

		List<Integer> customerIds = new ArrayList<Integer>();
		
		try {
			File myObj = new File(test_file_path);
			Scanner myReader = new Scanner(myObj);

			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();

				System.out.println(data);

				if(data.charAt(0) == '*'){
					stars++;
				}

				if(stars == 2 && data.charAt(0) != '*'){
					// This means that we are reading the custIds
					Integer custid = Integer.parseInt(data);
					customerIds.add(custid);
				}

				if(stars == 3 && data.charAt(0) != '*'){

					// This means that we are looking at the balance
					this.initial_balance = Integer.parseInt(data);
				}
			}
			myReader.close();

		} catch (FileNotFoundException e) {
			System.out.println("An error occurred. Reading the test file");
			e.printStackTrace();
		}

		// Now that we have read the customerIds and the balance
		for (Integer customerid : customerIds) {
			this.customers.put(customerid, new Customer(customerid, this.initial_balance));
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}


	@GetMapping("/getBalance")
	public int getBalance(@RequestParam int custId){
		/*
		 * Get the balance of the corresponding custId	 
		 * If this customer does not exists then return -1
		*/

		if(this.customers.containsKey(custId)){
			Customer curr = this.customers.get(custId);
			return curr.getBalance();
		}
		else{
			return -1;
		}
	}

	@GetMapping("/deductAmount")
	public boolean deductAmount(@RequestParam int custId, @RequestParam int amount){

		if(this.customers.containsKey(custId)){
			Customer curr = this.customers.get(custId);
			return curr.deductBalance(amount);
		}
		else{

			System.out.println("Checking into the amount of a wrong customer (Deduct)");
			return false;
		}

	}

	@GetMapping("/addAmount")
	public boolean addAmount(@RequestParam int custId, @RequestParam int amount){

		if(this.customers.containsKey(custId)){
			Customer curr = this.customers.get(custId);
			return curr.addBalance(amount);
		}
		else{

			System.out.println("Checking into the amount of a wrong customer (Add)");
			return false;
		}

	}

	@GetMapping("/reset")
	public void reset(){

		// Iterate through all the customers and then set their balances to initial balance
		for (Integer custId : this.customers.keySet()) {
			
			Customer curr = this.customers.get(custId);
			curr.setBalance(initial_balance);
		}

	}

}
