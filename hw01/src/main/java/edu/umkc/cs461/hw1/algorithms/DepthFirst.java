package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

/*
 * Depth First Search algorithm implementation
  */
public class DepthFirst extends SearchState{
 
    /*
     * Constructor for the DepthFirst search algorithm
     * @param start The city to start the search from
     * @param end The city to search for
     * @param graph The graph to search
     */
    public DepthFirst(final City start, final City end, final BiDirectGraph<City> graph){
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

        //initialize the stack, routes, and visited set
        Stack<Node> stack = new Stack<Node>();
        List<List<City>> routes = new ArrayList<List<City>>();
        Set<City> visited = new HashSet<City>();

        //push the start node onto the stack
        Node start = new Node(getStart(), null);
        stack.push(start);

        //while the stack is not empty
        while(!stack.isEmpty()){

            //pop the top node off the stack
            Node curr = stack.pop();

            //get the current city
            City current = curr.city;

            //if we are not looking for all routes and we have already visited the current city then skip
            if(!findAllRoutes && visited.contains(current)){
                continue;
            }

            //add the current node to the visit list and the visited set
            visitList.add(curr);
            visited.add(current);

            //if we have reached the end city
            if(current.equals(getEnd())){
                //add the route to the list of routes
                routes.add(SearchState.createCityListFromNode(curr));

                //if we are only looking for the first route, return the result
                if(!findAllRoutes){
                    return new FindResult(routes, visitList);
                }
            }

            //sort connections by distance and return as a list
            List<Node> toAdd = new ArrayList<>();
            getGraph().getConnections(current).entrySet().forEach(e -> {     
                    //if the neighbor (child) is not in the path (a parent, grandparent, etc. of the current node)
                    if(!SearchState.checkCityForVisit(e.getKey(), visited, curr, findAllRoutes)){
                        //add the neighbor to the queue
                        Node neighborNode = new Node(e.getKey(), curr);
                        toAdd.add(neighborNode);
                    }
                });

            //add the neighbors to the stack in reverse order so that the first neighbor is on top
            for(Node n: toAdd.reversed()){
                stack.push(n);
            }
        }
        return new FindResult(routes, visitList);
    }        
}
