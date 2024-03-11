# Homework Assignment #1
Search

To run (assuming Bazel installed):

bazel run //:hw1

See ./data/coordinates.csv for list of available cities
Enter the start city:
Abilene

Enter the end city:
Zenda

Enter the output file name (this is where the JSON results are written to)
AbileneToZendaFindFirst.json

Find all routes? (y/n): 
n

See AbileneToZendaFindFirst.json for the structured results. The remaining results will look something like the following:

```
A*: 
Time: 1358ms for 1000000 iterations
First Route: 
Distance: 2.763884968219526
Abilene -> Salina -> McPherson -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Salina -> McPherson -> Hutchinson -> Lyons -> Marion -> Newton -> Haven -> Junction_City -> Pratt -> Zenda
```

1. The first line indicates the algorithm, in this case, A*.
2. The next line shows the time taken for how many iterations. In this case, 1.358s to complete 1,000,000 iterations.
3. The total distance of the first route found is show next.
4. The first route found is displayed next. Given that this is A* we further expect in this case it is the shortest route.
5. Finally, the order of the nodes visited is shown. This allows us to animate the search algorithm if we wish
to observe it's behavior graphically.


The block below shows the output from a route from Abilene to Zelda, showing the first route for each
algorithm.

Each algorithm also has a "Flexi" version of it.  See Flexi.md for a design discussion.

```
Loading data...
Data loaded.
Finding route from Abilene to Zenda

BFS: 
Time: 1890ms for 1000000 iterations
First Route: 
Distance: 2.763884968219526
Abilene -> Salina -> McPherson -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Hays -> Salina -> Junction_City -> Marion -> Lyons -> McPherson -> Manhattan -> Hillsboro -> Hutchinson -> Newton -> Florence -> Topeka -> El_Dorado -> Pratt -> Haven -> Andover -> Emporia -> Towanda -> Zenda

Flexi BFS: 
Time: 2486ms for 1000000 iterations
First Route: 
Distance: 2.763884968219526
Abilene -> Salina -> McPherson -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Hays -> Salina -> Junction_City -> Marion -> Lyons -> McPherson -> Manhattan -> Hillsboro -> Hutchinson -> Newton -> Florence -> Topeka -> El_Dorado -> Pratt -> Haven -> Andover -> Emporia -> Towanda -> Zenda


DFS: 
Time: 1270ms for 1000000 iterations
First Route: 
Distance: 5.3414337431109065
Abilene -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Hays -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Haven -> Hutchinson -> Pratt -> Zenda


Flexi DFS: 
Time: 2294ms for 1000000 iterations
First Route: 
Distance: 5.3414337431109065
Abilene -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Hays -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Haven -> Hutchinson -> Pratt -> Zenda


IDDFS: 
Time: 7935ms for 1000000 iterations
First Route: 
Distance: 5.3414337431109065
Abilene -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Hays -> Salina -> Junction_City -> Marion -> Abilene -> Hays -> Salina -> Lyons -> McPherson -> Junction_City -> Marion -> Manhattan -> Abilene -> Hays -> Salina -> Lyons -> Hillsboro -> McPherson -> Hutchinson -> Newton -> Florence -> Marion -> Junction_City -> Marion -> Manhattan -> Topeka -> Abilene -> Hays -> Salina -> Lyons -> Hillsboro -> El_Dorado -> McPherson -> McPherson -> Hutchinson -> Newton -> Pratt -> Florence -> Newton -> Haven -> Andover -> Emporia -> Florence -> Marion -> Manhattan -> Junction_City -> Marion -> Abilene -> Hays -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Towanda -> McPherson -> Hutchinson -> Florence -> Marion -> McPherson -> Junction_City -> Marion -> Manhattan -> Topeka -> Abilene -> Hays -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Haven -> Hutchinson -> Andover -> McPherson -> Emporia -> Towanda -> McPherson -> Florence -> Marion -> Manhattan -> McPherson -> Junction_City -> Marion -> Abilene -> Hays -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Haven -> Hutchinson -> Pratt -> Florence -> McPherson -> Andover -> Augusta -> Towanda -> Winfield -> Mulvane -> Leon -> McPherson -> Marion -> Emporia -> Towanda -> McPherson -> McPherson -> Junction_City -> Marion -> Manhattan -> Topeka -> Abilene -> Hays -> Salina -> Lyons -> Hillsboro -> El_Dorado -> Newton -> Haven -> Hutchinson -> Pratt -> Zenda


Flexi IDDFS: 
Time: 2658ms for 1000000 iterations
First Route: 
Distance: 2.763884968219526
Abilene -> Salina -> McPherson -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Hays -> Salina -> Junction_City -> Marion -> Lyons -> Hillsboro -> McPherson -> Hutchinson -> Newton -> Florence -> Manhattan -> Topeka -> El_Dorado -> Towanda -> Pratt -> Zenda


Best First: 
Time: 1412ms for 1000000 iterations
First Route: 
Distance: 4.497023212988593
Abilene -> Salina -> Lyons -> Hillsboro -> McPherson -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Salina -> Lyons -> Hillsboro -> McPherson -> Hutchinson -> Pratt -> Zenda


Flexi Best First:
Time: 2573ms for 1000000 iterations
First Route: 
Distance: 4.497023212988593
Abilene -> Salina -> Lyons -> Hillsboro -> McPherson -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Salina -> Lyons -> Hillsboro -> McPherson -> Hutchinson -> Pratt -> Zenda


A*: 
Time: 1358ms for 1000000 iterations
First Route: 
Distance: 2.763884968219526
Abilene -> Salina -> McPherson -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Salina -> McPherson -> Hutchinson -> Lyons -> Marion -> Newton -> Haven -> Junction_City -> Pratt -> Zenda


Flexi A*
Time: 2811ms for 1000000 iterations
First Route: 
Distance: 2.763884968219526
Abilene -> Salina -> McPherson -> Hutchinson -> Pratt -> Zenda
Visited: 
Abilene -> Salina -> McPherson -> Hutchinson -> Lyons -> Marion -> Newton -> Haven -> Junction_City -> Pratt -> Zenda
```
