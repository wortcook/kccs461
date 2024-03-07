package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;


public class FlexiSearch extends SearchState{

    public FlexiSearch(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    //Default implementation is BFS
    public FindResult find(final boolean findAllRoutes){
        final Queue<Node> frontierStack = new ArrayDeque<Node>();
        return find(findAllRoutes, new Frontier<Node>(
            (srchState) -> {frontierStack.clear();return null;},
            (srchState, nodes) -> {frontierStack.addAll(nodes);return null;},
            (srchState) -> frontierStack.remove(),
            (srchState) -> frontierStack.isEmpty()
        ));
    }

    @Override
    public FindResult find(final boolean findAllRoutes, final Frontier<Node> frontier){
        final List<Node> visitList = new LinkedList<Node>();

        //frontier frontier = new frontier();
        final List<List<City>> routes = new ArrayList<List<City>>();
        final Set<City> visited = new HashSet<City>();
       
        final Node start = new Node(getStart(), null);
        final City end = getEnd();
        frontier.init(this);
        List<Node> startList = new ArrayList<Node>();
        startList.add(start);
        frontier.add(this, startList);

        while(!frontier.isEmpty(this)){
            Node curr = frontier.remove(this);
            City current = curr.city; 
            visitList.add(curr);
            visited.add(current);
            if(current.equals(end)){
                routes.add(SearchState.createCityListFromNode(curr));
                if(!findAllRoutes){
                    return new FindResult(routes, visitList);
                }
            }

            if(frontier.continueNode(this,curr)){//Should process the connections to the current node
                //Add all the connections from the current city to the frontier
                frontier.add(this, 
                        getGraph() //from the search space
                        .getConnections(current) //get the connections from the current city
                        .entrySet() //get the connections as a set of entries
                        .stream() //convert the set to a stream
                        //next filter out any cities that are already in the path or we have already visited
                        .filter(e -> !SearchState.checkCityForVisit(e.getKey(), visited, curr, findAllRoutes))
                        //then convert each city into a node
                        .map(e -> new Node(e.getKey(), curr))
                        //collect the nodes into a list
                        .collect(Collectors.toList()));
            }
        }
        return new FindResult(routes, visitList);
    }
}
