# cabHailing

Wallet Microservice.
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
