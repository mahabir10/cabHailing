package com.cabhailing.wallet;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Customer {
    
    // This will contain the customer id and the wallet balance
    private int custId;
    private int balance;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    // Constructor
    public Customer(int custId, int balance){
        this.custId = custId;
        this.balance = balance;
    }

    public int getCustId() {

        try{
            lock.readLock().lock();
            return custId;
        }
        finally{
            lock.readLock().unlock();
        }
        
    }

    public void setCustId(int custId) {
        try{
            lock.writeLock().lock();
            this.custId = custId;
        }
        finally{
            lock.writeLock().unlock();
        }
    }

    public int getBalance() {
        try{
            lock.readLock().lock();
            return balance;
        }
        finally{
            lock.readLock().unlock();
        }
        
    }

    public void setBalance(int balance) {
        try{
            lock.writeLock().lock();
            this.balance = balance;
        }
        finally{
            lock.writeLock().unlock();
        }
    }

    public boolean deductBalance(int amount){
        /*
         * This will return true if balance is successfully deducted.
         * Otherwise will return false
        */
        
        try{
            lock.writeLock().lock();
            if( (this.balance < amount) || (amount < 0) ){
                return false;
            }
            else{
                this.balance-=amount;
                return true;
            }
        }
        finally{
            lock.writeLock().unlock();
        }
        
    }

    synchronized public boolean addBalance(int amount){
        /*
         * This will add the given amount to the balance
         * Returns true if successfully added.
         * Returns false otherwise
        */

        try{
            lock.writeLock().lock();
            if(amount < 0){
                return false;
            }
            else{
                this.balance+=amount;
                return true;
            }
        }
        finally{
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return "Customer [custId=" + custId + ", balance=" + balance + "]";
    }

}