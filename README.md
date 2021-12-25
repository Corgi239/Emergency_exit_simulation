# Introduction

Welcome to Emergency Exit Simuation!
This application simulates a crowd of people exiting a rectangular room through a doorway in an emergency exit situation.
The simulation Is presented through an interactive graphical user interface, through which you can view the current positions of the simulated people, as well adjust the various parameters of the simulation.
The initial state of the simulation can either be generated randomly, or imported from a configuration file.
The logic of the simulation is based on Steering Behaviors described in the article “Steering Behaviors For Autonomous Characters“ (C.W. Reynolds, 1999).

The application uses a custom text file format to store the parameters of the simulation.
Using files allows you to directly input the exact parameters of the room they wish to simulate, as well as the exact coordinates of the people within it.
The file is separated into 11 blocks that can be placed in arbitrary order, each of which starts with a header line and contains information about one of the simulation parameters.
This information must be presented as a single decimal number, or as a list of pairs of decimal numbers in the case of the initial_coordinates parameter.
Each pair must be on its own line and the numbers within the pair must be separated by a comma. No extra lines or values must be included in the file.

# Description of the parameters

*	#room_width – the horizontal dimensions of the room
*	#room_height – the vertical dimensions of the room
*	#relative_exit_size – the ratio of the exit size to the length of the right room wall
*	#exit_location – the vertical coordinate of the top point of the exit (the zero coordinate corresponds with the top of the room)
*	#max_speed – the maximum speed the people will be allowed to reach
*	#max_acceleration – the maximum acceleration the people will be allowed to reach
*	#search_radius – how far apart two people must be to not be considered neighbors
*	#seeking_weight – the weight coefficient of the seeking behavior
*	#separation_weight – the weight coefficient of the separation behavior
*	#containment_weight – the weight coefficient of the containment behavior
*	#initial_coordinates – the list of coordinates where people will be placed at the start of the simulation

Sample configuration file:
```
#room_width
  500.0
#room_height
  600.0
#relative_exit_size
  0.01
#exit_location
  50.0
#max_speed
  0.05
#max_acceleration
  0.0001
#search_radius
  25.0
#seeking_weight
  20.0
#separation_weight
  90.0
#containment_weight
  30.0
#initial_coordinates
  2.0   , 2.0
  10.5  , 28.1
```

# How to use

To lunch the simulation, first download the Emergency exit simulation-assembly-0.1.jar file (located at Emergency_exit_simulation/target/scala-2.13). You can then run the downloaded file with the following command-line prompt:

`java -jar <location of the downloaded .jar file>`
