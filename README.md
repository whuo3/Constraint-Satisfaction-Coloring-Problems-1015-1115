# Project Name

Map(CSP - Constraint Satisfaction Problems) Coloring

# Installation

To compile the code:

javac *.java

To run the code:

java Game

## Work

Generate random instances of map-coloring problems as follows: scatter N points on the unit square; select a point X at random, connect X by a line segment to the nearest point Y such that X is not already connected to Y and the segment crosses no other segment. Repeat the previous step until no more connections are possible. The points represent regions on the map and the lines connect neighbors. 

Try to color each map with four colors using backtracking search 

(a) without forward checking, using random variable and value assignment order
 
(b) with forward checking and any other variable and value ordering heuristics that can increase search efficiency

## License

Weijie Huo