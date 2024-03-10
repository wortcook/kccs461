package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import edu.umkc.cs461.hw1.data.BiDirectGraph;
import edu.umkc.cs461.hw1.data.City;

public class AStar extends SearchState{
    public AStar(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    @Override
    public FindResult find(final boolean findAllRoutes, Frontier<Node> frontierIgnore){
        final List<Node> visitList = new LinkedList<Node>();

        List<List<City>> routes = new ArrayList<List<City>>();
        Set<City> visited = new HashSet<City>();

        Node start = new Node(getStart(), null);
        final City end = getEnd();

        PriorityQueue<Node> queue = new PriorityQueue<Node>((n1, n2) -> {
            //f(n)         =       g(n)    +      h(n)
            //total cost   =   distance to node + distance from node to goal
            double n1Value = SearchState.costFromStart(n1) + n1.city.distanceFrom(end);
            double n2Value = SearchState.costFromStart(n2) + n2.city.distanceFrom(end);
            return Double.compare(n1Value, n2Value);
        });

        queue.add(start);


        while(!queue.isEmpty()){
            Node curr = queue.poll();
            City current = curr.city;
            visitList.add(curr);
            visited.add(current);

            

            if(current.equals(end)){
                routes.add(SearchState.createCityListFromNode(curr));
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
