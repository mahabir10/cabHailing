CREATE TABLE Cab (

    cabId int PRIMARY KEY,
    state int,
    position int,
    rideId int,
    custId int,
    destinationLoc int

);

INSERT INTO Cab
    (cabId, state, position, rideId, custId, destinationLoc) 
VALUES
    (101, -1, -1, -1, -1, -1),
    (102, -1, -1, -1, -1, -1),
    (103, -1, -1, -1, -1, -1),
    (104, -1, -1, -1, -1, -1);