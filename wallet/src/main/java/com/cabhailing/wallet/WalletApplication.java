package com.cabhailing.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);

		/*
		 * Here we have to read the testcase file. in that testcase file we will be given the 
		 * customer ids and the balance they posses.
		 * So we have to create a list of customers to represent them.
		*/

	}

}
