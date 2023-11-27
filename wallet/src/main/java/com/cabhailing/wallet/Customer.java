package com.cabhailing.wallet;

public class Customer {
    
    // This will contain the customer id and the wallet balance
    private int custId;
    private int balance;

    // Constructor
    public Customer(int custId, int balance){
        this.custId = custId;
        this.balance = balance;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean deductBalance(int amount){
        /*
         * This will return true if balance is successfully deducted.
         * Otherwise will return false
        */
        

        if( (this.balance < amount) || (amount < 0) ){
        return false;
        }
        else{
        this.balance-=amount;
        return true;
        }
    }

    public boolean addBalance(int amount){
        /*
         * This will add the given amount to the balance
         * Returns true if successfully added.
         * Returns false otherwise
        */

        if(amount < 0){
            return false;
        }
        else{
            this.balance+=amount;
            return true;
        }
    }
}