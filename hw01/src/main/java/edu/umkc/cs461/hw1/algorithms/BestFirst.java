package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

import edu.umkc.cs461.hw1.data.BiDirectGraph;
import edu.umkc.cs461.hw1.data.City;

/*
 * Best First Search algorithm implementation
 */
public class BestFirst extends SearchState{

    /*
     * Constructor for the BestFirst search algorithm
     * @param start The city to start the search from
     * @param end The city to search for
     * @param graph The graph to search
     */
    public BestFirst(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    /*
     * Find the route(s) from the start city to the end city
     * @param findAllRoutes If true, find all routes, otherwise just find the first route
     * @param frontierIgnore Dummy frontier param to satisfy the interface
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
        final City end = getEnd();

        //while the stack is not empty
        while(!stack.isEmpty()){

            //pop the top node off the stack
            Node curr = stack.pop();

            //get the current city
            City current = curr.city;

            //add the current node to the visit list
            visitList.add(curr);
            visited.add(current);

            //if we are not looking for all routes and we have already visited the current city then skip
            if(current.equals(end)){
                //if we have reached the end city
                routes.add(SearchState.createCityListFromNode(curr));

                //if we are not looking for all routes then return
                if(!findAllRoutes){
                    return new FindResult(routes, visitList);
                }
            }

            //Basically best first is depth first search where we sort the connections
            //by the total cost of the node (distance to node + distance from node to goal)
            //i.e. cost(city) = distance(city) + distance(city, goal)
            //then we take the node with the lowest cost first
            //followed by the next lowest cost, etc.

            Function<SearchState.Node,Double> heuristic = new Function<SearchState.Node,Double>(){
                public Double apply(SearchState.Node node){
                    return (null==node.getHeuristic()) ? node.city.distanceFrom(end) : node.getHeuristic();
                }
            };

            //sort connections by distance and return as a list
            Stream<Entry<City,Double>> connStream = getGraph().getConnections(current).entrySet().stream();
            //Note, this is a local sort only as opposed to A* which sorts the frontier globally.
            connStream = connStream.sorted((e1, e2) ->{
                double e1Value = heuristic.apply(new Node(e1.getKey(), curr));
                double e2Value = heuristic.apply(new Node(e2.getKey(), curr));
                return -Double.compare(e1Value, e2Value);
            });
            connStream.forEach(e -> {     
                    //if the neighbor (child) is not in the path (a parent, grandparent, etc. of the current node)
                    if(!SearchState.checkCityForVisit(e.getKey(), visited, curr, findAllRoutes)){
                        //add the neighbor to the queue
                        Node neighborNode = new Node(e.getKey(), curr);
                        stack.push(neighborNode);
                    }
                });
        }
        return new FindResult(routes, visitList);

    }
    
}
