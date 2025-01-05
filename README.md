# Visual Transit Simulator

## The Visual Transit Simulator Software

The current VTS software models vehicle transit around the University of Minnesota campus. Specifically, the software simulates the behavior of vehicles and passengers on campus. The VTS software currently supports two types of vehicles: buses and trains. Vehicles provide service for a line. A line is made by two routes: an outbound and an inbound route. Vehicles go along a route, make stops, and pick up/drop off passengers. The simulation operates over a certain number of time units. In each time unit, the VTS software updates the state of the simulation by creating passengers at stops, moving vehicles along the routes, allowing a vehicle to pick up passengers at a stop, etc. The simulation is configured using a *configuration* file that specifies the simulated lines, the stops of the routes, and how likely it is that a passenger will show up at a certain stop at each time unit. Routes must be defined in pairs, that is, there should be both an outbound and inbound route and the routes should be specified one after the other. The ending stop of the outbound route should be at the same location as the starting stop of the inbound route and the ending stop of the inbound route should be at the same location as the starting stop of the outbound route. However, stops between the starting and ending stops of outbound and inbound routes can be at different locations. After a vehicle has passed a stop, it is possible for passengers to show up at stops that the vehicle has already passed. For this reason, the simulator supports the creation of multiple vehicles and these vehicles will go and pick up the new passengers. Each vehicle has its own understanding of its own route, but the stops have relationships with multiple vehicles serving the same line. Vehicles do not make more than one trip in the line they serve. When a vehicle finishes both of its routes (outbound and inbound), the vehicle exits the simulation.

The VTS software is divided into two main modules: the *visualization module* and the *simulator module*. The visualization module displays the state of the simulation in a browser, while the simulator module performs the simulation. The visualization module is a web client application that runs in a browser and it is written in Javascript and HTML. The visualization module code is inside the `<dir>/app/src/main/webapp/web_graphics` directory of this repo (where `<dir>` is the root directory of the repo). The simulator module is a web server application written in Java. The simulator module code is inside the `<dir>/app/src/main/java/edu/umn/cs/csci3081w/project` directory. The simulator module is divided into two parts: *model classes* and the *webserver classes*. The model classes model real-world entities (e.g., the concept of a vehicle) and the code is inside the `<dir>/app/src/main/java/edu/umn/cs/csci3081w/project/model` directory. The webserver classes include the code that orchestrates the simulation and is inside the `<dir>/app/src/main/java/edu/umn/cs/csci3081w/project/webserver` directory. The visualization module and the simulator module communicate with each other using [websockets](https://www.baeldung.com/java-websockets).

The user of the VTS software interacts with the visualization module using the browser and can specific how long the simulation will run (i.e., how many time units) and how often new vehicles will be added to a route in the simulation. The users also specifies when to start and pause the simulation. The image below depicts the graphical user interface (GUI) of the current version of the VTS software.

![GUI of the VTS Software](/images/vts_iteration_3.png)

### VTS Software Details

#### Simulation Configuration
The simulation is based on the `<dir>/app/src/main/resources/config.txt` configuration file. The following excerpt of the configuration file defines a bus line and storage facility information.

```
LINE_START, BUS_LINE, Campus Connector

ROUTE_START, East Bound

STOP, Blegen Hall, 44.972392, -93.243774, .15
STOP, Coffman, 44.973580, -93.235071, .3
STOP, Oak Street at University Avenue, 44.975392, -93.226632, .025
STOP, Transitway at 23rd Avenue SE, 44.975837, -93.222174, .05
STOP, Transitway at Commonwealth Avenue, 44.980753, -93.180669, .05
STOP, State Fairgrounds Lot S-108, 44.983375, -93.178810, .01
STOP, Buford at Gortner Avenue, 44.984540, -93.181692, .01
STOP, St. Paul Student Center, 44.984630, -93.186352, 0

ROUTE_END

ROUTE_START, West Bound

STOP, St. Paul Student Center, 44.984630, -93.186352, .35
STOP, Buford at Gortner Avenue, 44.984482, -93.181657, .05
STOP, State Fairgrounds Lot S-108, 44.983703, -93.178846, .01
STOP, Transitway at Commonwealth Avenue, 44.980663, -93.180808, .01
STOP, Thompson Center & 23rd Avenue SE, 44.976397, -93.221801, .025
STOP, Ridder Arena, 44.978058, -93.229176, .05
STOP, Pleasant Street at Jones-Eddy Circle, 44.978366, -93.236038, .1
STOP, Bruininks Hall, 44.974549, -93.236927, .3
STOP, Blegen Hall, 44.972638, -93.243591, 0

ROUTE_END

LINE_END

STORAGE_FACILITY_START

SMALL_BUSES, 3
LARGE_BUSES, 2
ELECTRIC_TRAINS, 1
DIESEL_TRAINS, 5

STORAGE_FACILITY_END
```

The configuration line `LINE_START, BUS_LINE, Campus Connector` defines the beginning of the information belonging to a simulated line. The configuration line `ROUTE_START, East Bound` defines a the beginning of the information defining the outbound route. (The outbound route is always defined before the inbound route). The subsequent configuration lines are the stops in the route. Each stop has a name, a latitude, a longitude, and the probability to generate a passenger at the stop. For example, for `STOP, Blegen Hall, 44.972392, -93.243774, .15`, `Blegen Hall` is the name of the stop, `44.972392` is the latitude, `-93.243774` is the longitude, and `.15` (i.e., `0.15`) is the probability to generate a passenger at the stop. The last stop in a route has a probability to generate a passenger always equal to zero. The information inside `STORAGE_FACILITY_START` and `STORAGE_FACILITY_END` provides the number of small buses, large buses, electric trains, and diesel trains available for the simulation.

#### Running the VTS Software
To run the VTS software, you have to **first start the simulator module** and **then start the visualization module**. To start the simulator module, go to `<dir>` and run `./gradlew appRun` (or `./gradlew clean appRun`). To start the visualization module, open a browser and paste this link `http://localhost:7777/project/web_graphics/project.html` in its address bar. To stop the simulator module, press the enter/return key in the terminal where you started the module. To stop the visualization module, close the tab of browser where you started the module. In rare occasions, you might experience some issues in starting the simulator module because a previous instance of the module is still running. To kill old instances, run `ps aux | grep gretty | awk '{print $2}' | xargs -L 1 kill` (un Unix-like operating systems) and this command will terminate previous instances. (The command is killing the process of the web server container running the simulator module.) The command works on CSE lab machines.

#### Simulation Workflow
Because the VTS software is a web application, the software does not have a `main` method. When you load the visualization module in the browser, the visualization module opens a connection to the simulator module (using a websocket). The opening of the connection triggers the execution of the `WebServerSession.onOpen` method in the simulator module. When you click `Start` in the GUI of the visualization module, the module starts sending messages/commands to the simulator module. The messages/commands exchanged by the two modules are [JSON objects](https://www.w3schools.com/js/js_json_objects.asp). You can see the messages/commands created by the visualization module insdie `<dir>/app/src/main/webapp/web_graphics/sketch.js`. The simulator module processes messages received by the visualization model inside the `WebServerSession.onMessage` method. The simulator module sends messages to the visualization module using the `WebServerSession.sendJson` method. Finally, once you start the simulation you can restart it only by reloading the visualization module in the browser (i.e., reloading the web page of the visualization module).

## Resources
* [A Guide to the Java API for WebSocket](https://www.baeldung.com/java-websockets)
* [JSON objects](https://www.w3schools.com/js/js_json_objects.asp)
* [Google Maps](http://maps.google.com)
* [Reading and Writing CSVs in Java](https://stackabuse.com/reading-and-writing-csvs-in-java)
* [RGBA format](https://www.w3schools.com/css/css_colors_rgb.asp)
