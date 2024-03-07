package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

import edu.umkc.cs461.hw1.data.BiDirectGraph;
import edu.umkc.cs461.hw1.data.City;

public class BestFirst extends SearchState{
    public BestFirst(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    @Override
    public FindResult find(final boolean findAllRoutes, Frontier<Node> frontierIgnore, int maxDepthIgnore, int depthSkipIgnore){
        final List<Node> visitList = new LinkedList<Node>();

        Stack<Node> stack = new Stack<Node>();
        List<List<City>> routes = new ArrayList<List<City>>();
        Set<City> visited = new HashSet<City>();

        Node start = new Node(getStart(), null);
        stack.push(start);

        final City end = getEnd();

        while(!stack.isEmpty()){
            Node curr = stack.pop();
            City current = curr.city;
            visitList.add(curr);
            visited.add(current);
            if(current.equals(end)){
                routes.add(SearchState.createCityListFromNode(curr));
                if(!findAllRoutes){
                    return new FindResult(routes, visitList);
                }
            }

            //Basically best first is depth first search where we sort the connections
            //by the total cost of the node (distance to node + distance from node to goal)
            //i.e. cost(city) = distance(city) + distance(city, goal)
            //then we take the node with the lowest cost first
            //followed by the next lowest cost, etc.

            //sort connections by distance and return as a list
            Stream<Entry<City,Double>> connStream = getGraph().getConnections(current).entrySet().stream();
            connStream = connStream.sorted((e1, e2) ->{
                //f(n)         =       g(n)    +      h(n)
                //total cost   =   distance to node + distance from node to goal
                double e1Value = e1.getValue() + e1.getKey().distanceFrom(end);
                double e2Value = e2.getValue() + e2.getKey().distanceFrom(end);
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
