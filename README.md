# cabHailing

**Cab Microservice.**
To run this service,

1. Build the wallet package: Use /mvnw package, from the cab directory to generate the jar file.
2. Build the Docker image using: docker build --build-arg JAR_FILE=target/*.jar -t cab .
3. Run the Docker image using: docker run -p 8080:8080 -v <absolute-path-of-test.txt>:/test.txt cab

<absolute-path-of-text.txt> This is the test file which will provide the customer ids and their balance information. So provide the absolute path where you want to run this docker image.

Now the wallet service is hosted at localhost:8080

Below are the end-points you can use to get the requirement of your choice.


1. boolean requestRide(int cabId, int rideId, int sourceLoc, int destinationLoc):
  Example - localhost:8080/requestRide?cabId=101&rideId=988734&sourceLoc=0&destinationLoc=10

  This is sent by rideservice. If the current cab is in available state and it is accepting request then we will generate and request instance. 
  This request instance will store the rideId, sourceLoc and destinationLoc.

  So, if ride gets cancelled then the cab will be at sourceLoc, If the ride ends then the cabs postition will be at destinationLoc.

  It returns true if the cab has accepted the request.

2. boolean rideStarted(int cabId, int rideId):
  Example - localhost:8080/rideStarted?cabId=101&rideId=988734

  This is also sent by rideService. If the cab is in commited state due to the previously requested rideId, then it goes to the giving ride state.
  The location is also updated to the sourceLoc of the request.

3. boolean rideCancelled(int cabId, int rideId):
  Example - localhost:8080/rideCancelled?cabId=101&rideId=988734

  This is also sent by rideService. If the cab is in commited state due to the previously requested rideId, then it goes to the available state.
  The location is also updated to the sourceLoc of the request.

4. boolean rideEnded(int cabId, int rideId):
  Example - localhost:8080/rideEnded?cabId=101&rideId=988734

  This is sent by the driver. If the cab is in giving ride state due to the previously requested rideId, then it goes to the available state.
  The location is also updated to the destinationLoc of the request.

5. boolean signIn(int cabId, int initialPos):
  Example - localhost:8080/signIn?cabId=101&initialPos=5

  Checks if the cab is in signedout state. If it is then it makes it requests the rideservice instance, and if the 
  message is true then the cab is signed in and updates the postition as initialPos.

6. boolean signOut(int cabId):
  Example - localhost:8080/signOut?cabId=101

  Checks if it is in signed in state, then it asks the rideservice to signout this cab.
  If the returned message is true, then it makes the cab signed out

7. int numRides(@RequestParam int cabId):
  Example - localhost:8080/numRides?cabId=101

  Returns the numberof rides given by the current cab. Also the currently giving ride also count as 1.




**Wallet Microservice.**
To run this service,

1. Build the wallet package: Use /mvnw package, from the wallet directory to generate the jar file.
2. Build the Docker image using: docker build --build-arg JAR_FILE=target/*.jar -t wallet .
3. Run the Docker image using: docker run -p 8082:8080 -v <absolute-path-of-test.txt>:/test.txt wallet

<absolute-path-of-text.txt> This is the test file which will provide the customer ids and their balance information. So provide the absolute path where you want to run this docker image.

Now the wallet service is hosted at localhost:8082 

Below are the end-points you can use to get the requirement of your choice.

1. int getBalance(int custId):
  Example - localhost:8082/getBalance?custId=203
  returns current wallet balance of custId. Inplace of custId give any id of the customer that you want to know the balance of.

2. bool deductAmount(int custId, int amount):
  Example - localhost:8082/deductAmount?custId=203&amount=1000

  If custId has balance >= amount, then reduce their balance by
  “amount” and return true, else return false. This service is used by
  RideService.requestRide.

  The RideService will be added later.

3. bool addAmount(custId, int amount):
  Example - localhost:8082/addAmount?custId=203&amount=1000

  Inverse of deductAmount.
  
  Both deductAmount and addAmount need to be processed in an
  isolated manner for a custId (i.e., different requests for the same custId
  should not overlap in time). 

  It is handled by using the ReentrantReadWriteLock feature of jave. So that both the read concurrent operations wont be serialised. But anything involving writing event is serialised.

4. void reset():
  Example - localhost:8082/reset

  Reset balances of all customers to the “initial” balance as given in the
  input text file (more details about this text file below). This end-point is
  mainly to help enable testing.
