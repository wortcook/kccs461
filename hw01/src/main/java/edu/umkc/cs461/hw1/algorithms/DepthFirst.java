package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

public class DepthFirst extends SearchState{
    
    public DepthFirst(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    @Override
    public FindResult find(final boolean findAllRoutes, Frontier<Node> frontierIgnore){
        final List<Node> visitList = new LinkedList<Node>();

        Stack<Node> stack = new Stack<Node>();
        List<List<City>> routes = new ArrayList<List<City>>();
        Set<City> visited = new HashSet<City>();

        Node start = new Node(getStart(), null);
        stack.push(start);

        while(!stack.isEmpty()){
            Node curr = stack.pop();
            City current = curr.city;

            if(!findAllRoutes && visited.contains(current)){
                continue;
            }

            visitList.add(curr);
            visited.add(current);
            if(current.equals(getEnd())){
                routes.add(SearchState.createCityListFromNode(curr));
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
            for(Node n: toAdd.reversed()){
                stack.push(n);
            }
        }
        return new FindResult(routes, visitList);
    }        
}
