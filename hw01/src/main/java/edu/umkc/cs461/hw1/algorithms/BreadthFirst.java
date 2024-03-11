package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;

import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

/*
 * Breadth First Search algorithm implementation
 */
public class BreadthFirst extends SearchState{

    /*
     * Constructor for the BreadthFirst search algorithm
     * @param start The city to start the search from
     * @param end The city to search for
     * @param graph The graph to search
     */
    public BreadthFirst(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    /*
     * Find the route(s) from the start city to the end city
     * @param findAllRoutes If true, find all routes, otherwise just find the first route
     * @param frontierIgnore The frontier to ignore
     * @return The result of the search
     */
    @Override
    public FindResult find(final boolean findAllRoutes, Frontier<Node> frontierIgnore){
        final List<Node> visitList = new LinkedList<Node>();


        //initialize the queue, routes, and visited set
        Queue<Node> queue = new ArrayDeque<Node>();
        List<List<City>> routes = new ArrayList<List<City>>();
        Set<City> visited = new HashSet<City>();
        
        //push the start node onto the queue
        final Node start = new Node(getStart(), null);
        final City end = getEnd();
        queue.add(start);

        //while the queue is not empty
        while(!queue.isEmpty()){

            //pop the top node off the queue
            Node curr = queue.remove();

            //get the current city
            City current = curr.city;

            //if we are not looking for all routes and we have already visited the current city then skip
            if(!findAllRoutes && visited.contains(current)){
                continue;
            }

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

            //sort connections by distance and return as a list
            getGraph().getConnections(current).entrySet().forEach(e -> {     
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
