#! /bin/sh
# this is to test order of requests
# every test case should begin with these two steps
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset
testPassed="yes"
#cab 101 signs in
resp=$(curl -s "http://localhost:8080/signIn?cabId=101&initialPos=0")
if [ "$resp" = "true" ];
then
echo "Cab 101 signed in"
else
echo "Cab 101 could not sign in"
testPassed="no"
fi
#cab 102 signs in
resp=$(curl -s "http://localhost:8080/signIn?cabId=102&initialPos=50")
if [ "$resp" = "true" ];
then
echo "Cab 102 signed in"
else
echo "Cab 102 could not sign in"
testPassed="no"
fi
#cab 103 signs in
resp=$(curl -s "http://localhost:8080/signIn?cabId=103&initialPos=100")
if [ "$resp" = "true" ];
then
echo "Cab 103 signed in"
else
echo "Cab 103 could not sign in"
testPassed="no"
fi
#cust 201 now requests from position 74, So the cab 102 should accept the ride first
#customer 201 requests a ride
rideId=$(curl -s \
"http://localhost:8081/requestRide?custId=201&sourceLoc=74&destinationLoc=10")
if [ "$rideId" != "-1" ];
then
echo "Ride by customer 201 started"
else
echo "Ride to customer 201 denied"
testPassed="no"
fi
#Check the status of 101 and 103 cabs, they should be in available state
#Status of cab 101
resp=$(curl -s \
"http://localhost:8081/getCabStatus?cabId=101")
if [ "$resp" != "available 0" ];
then
echo "Invalid Status for the cab 101"
testPassed="no"
else
echo "Correct Status for the cab 101"
fi
#Status of cab 102
resp=$(curl -s \
"http://localhost:8081/getCabStatus?cabId=102")
if [ "$resp" != "giving-ride 74 201 10" ];
then
echo "Invalid Status for the cab 102"
testPassed="no"
else
echo "Correct Status for the cab 102"
fi
#Status of cab 103
resp=$(curl -s \
"http://localhost:8081/getCabStatus?cabId=103")
if [ "$resp" != "available 100" ];
then
echo "Invalid Status for the cab 103"
testPassed="no"
else
echo "Correct Status for the cab 103"
fi
echo "Test Passing Status: " $testPassed