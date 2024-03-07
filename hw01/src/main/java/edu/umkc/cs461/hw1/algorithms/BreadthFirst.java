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

public class BreadthFirst extends SearchState{

    public BreadthFirst(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    @Override
    public FindResult find(final boolean findAllRoutes, Frontier<Node> frontierIgnore){
        final List<Node> visitList = new LinkedList<Node>();

        Queue<Node> queue = new ArrayDeque<Node>();
        List<List<City>> routes = new ArrayList<List<City>>();
        Set<City> visited = new HashSet<City>();
        
        final Node start = new Node(getStart(), null);
        final City end = getEnd();
        queue.add(start);

        while(!queue.isEmpty()){
            Node curr = queue.remove();
            City current = curr.city;
            visitList.add(curr);
            visited.add(current);
            if(current.equals(end)){
                routes.add(SearchState.createCityListFromNode(curr));
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
