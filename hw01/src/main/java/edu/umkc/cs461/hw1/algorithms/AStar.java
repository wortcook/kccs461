package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Set;
import java.util.stream.Stream;

import edu.umkc.cs461.hw1.data.BiDirectGraph;
import edu.umkc.cs461.hw1.data.City;

/*
 * A* search algorithm implementation
 */
public class AStar extends SearchState{

    /*
     * Constructor for the A* search algorithm
     * @param start The city to start the search from
     * @param end The city to search for
     * @param graph The graph to search
     */
    public AStar(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }


    /*
     * Find the route(s) from the start city to the end city
     * @param findAllRoutes If true, find all routes, otherwise just find the first route
     * @param frontierIgnore Dummy frontier param to satisfy the interface
     * @return The result of the search
     */
    @Override
    public FindResult find(final boolean findAllRoutes, Frontier<Node> frontierIgnore){
        final List<Node> visitList = new LinkedList<Node>();

        //initialize the queue, routes, and visited set
        List<List<City>> routes = new ArrayList<List<City>>();
        Set<City> visited = new HashSet<City>();

        //push the start node onto the queue
        Node start = new Node(getStart(), null);

        //get the end city
        final City end = getEnd();

        //initialize the priority queue
        PriorityQueue<Node> queue = new PriorityQueue<Node>((n1, n2) -> {
            //f(n)         =       g(n)    +      h(n)
            //total cost   =   distance to node + distance from node to goal

            final Function<SearchState.Node,Double> heuristic = new Function<SearchState.Node,Double>(){
                public Double apply(SearchState.Node node){
                    return (null==node.getHeuristic()) ? node.costFromStart() + node.city.distanceFrom(end) : node.getHeuristic();
                }
            };

            double n1Value = heuristic.apply(n1);
            double n2Value = heuristic.apply(n2);

            return Double.compare(n1Value, n2Value);
        });

        //add the start node to the queue
        queue.add(start);


        //while the queue is not empty
        while(!queue.isEmpty()){

            //pop the top node off the queue
            Node curr = queue.poll();

            //get the current city
            City current = curr.city;

            //add the current node to the visit list
            visitList.add(curr);
            visited.add(current);

            
            //if we have reached the end city
            if(current.equals(end)){
                //add the route to the list of routes
                routes.add(SearchState.createCityListFromNode(curr));

                //if we are only looking for the first route, return the result
                if(!findAllRoutes){
                    return new FindResult(routes, visitList);
                }
            }

            //Basically A* is depth first search where we sort the connections
            //by the total cost of the node (distance to node + distance from node to goal)
            //i.e. cost(city) = distance(city) + distance(city, goal)
            //then we take the node with the lowest cost first
            //followed by the next lowest cost, etc.

            //sort connections by distance and return as a list
            Stream<Entry<City,Double>> connStream = getGraph().getConnections(current).entrySet().stream();
            connStream.forEach(e -> {     
                    //if the neighbor (child) is not in the path (a parent, grandparent, etc. of the current node)
                    if(!SearchState.checkCityForVisit(e.getKey(), visited, curr, findAllRoutes)){
                        //add the neighbor to the queue
                        Node neighborNode = new Node(e.getKey(), curr);
                        queue.add(neighborNode);
                    }
                });
        }
        return new FindResult(routes, visitList);

    }
    
}
