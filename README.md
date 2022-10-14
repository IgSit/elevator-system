## Docker setup - NOT WORKING

I have encountered a problem of 'connection refused' when building the app - and I am
really curious how to fix it because:
- on a brand-new Spring app the Dockerfile and docker-compose.yml run fine
- the app is also starting with all maven dependencies in pom.xml
- BUT when the files are added in src directory (all classes) I have this error

I would really like to know why it is happening - if you have any ideas, please do not hesitate
to share.

### Build and run (listens on localhost)
> docker-compose up --build

### Run tests
> docker run -it --rm -t elevator-system-app /bin/sh
>
> ./mvnw test

## Local setup
### Prerequisites

- MySQL installed
- Maven installed
- Java installed
- IntelliJ recommended in order to handle maven commands

### Steps

1. Create a database named _elevatorsystem_.
> mysql -u root -p
> 
> use mysql;
> 
> create database elevatorsystem;
2. Add all privileges for user called elevatorsystem.
> create user 'elevatorsystem'@'localhost' identified by 'elevatorsystem';
> 
> grant all privileges on elevatorsystem.* to 'elevatorsystem'@'localhost' with grant option;
>
> flush privileges;
3. Run maven install and build the app.
> mvn spring-boot:run

Go to http://localhost:8080/elevators - you should see the app running.

## Usage

### Add new elevators to the system
The number in the request is the number of elevators to be created.

> POST http://localhost:8080/elevators
> 
> Content-Type: application/json
> 
> 2

### Add new elevator request
THe floor you want the elevator to arrive.

> POST http://localhost:8080//elevator-moves
> 
> Content-Type: application/json
> 
> {
> "floor": 7
> }

### Make one simulation step
Move all running elevators by one floor.

> POST http://localhost:8080/step

### View planned moves for a specific elevator

> GET http://localhost:8080/elevator-moves?elevator_id=1

## The idea behind it
### Project design
When making this task I focused more on making a platform rather than designing complex 
algorithm. That's why I decided to make a Spring web API that is connected to the database and uses it
to hold the information about elevators and their moves.

I believe that this approach says something more about my skills and attitude - dividing system
into layers (controllers to handle requests, services and models for the system logic, 
repositories for data access), covering parts of a system with tests etc. However, I would surely treat
this app as something of a demo - not all functionalities are tested, it lacks integrations and unit tests,
the algorithm could be way better - but considering it an 'a-few-hour-task' I think it's enough.

### Algorithm
The time to arrive to a pending floor is considered a function cost.

For each new floor request, the algorithm:
1. looks for closest-based free elevator, if present
   - the cost is simply a difference between current position and request position
2. looks for the elevators that will pass by requested floor in their current moves
   - the idea behind it is that the elevator at that moment won't have to make excessive ride
     (not considering that the entering person will also demand some floor request right now)
   - for those elevators that will pass by it adds additional cost to those who have already some
   planned moves (to avoid overloads and minimize the impact of entering person adding new floor request)
   - this cost is proportional to the amount of moves planned by that elevator
   - takes elevator with minimal cost
3. for all elevators that have some moves already planned tries to add pending move
  to the planned ones in any position
   - considering an elevator that has three moves planned, it will try to add it as first
     second, third or fourth move
   - for each attempt calculates the cost of excessive road taken - it multiplies it adequately to the amount
   of moves that will be delayed by adding new one - to avoid someone waiting in the elevator for a long time
   - takes elevator with minimal cost
4. takes the lowest cost solution of these three (if all exist)

#### Time complexity

Introducing:
- E - number of elevators
- M - number of planned moves

The complexity of algorithm is O(E*M) which is high but considering the case study that:
- there aren't that many elevators (in the task it is said up to 16)
- there shouldn't be that many planned moves (limited by the number of building floors)

The algorithm besides being linear has small factors included in its complexity.