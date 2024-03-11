package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

import edu.umkc.cs461.hw1.data.BiDirectGraph;
import edu.umkc.cs461.hw1.data.City;

/*
 * Iterative deepening depth first search
 */
public class IDDFS extends SearchState {

    /*
     * Constructor for the IDDFS search algorithm
     * @param start The city to start the search from
     * @param end The city to search for
     * @param graph The graph to search
     */
    public IDDFS(final City start, final City end, final BiDirectGraph<City> graph) {
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
        final List<Node> visitList = new ArrayList<Node>();
        List<List<City>> routes = new ArrayList<List<City>>();
        //iterative deepening depth first search
        int depth = 0;
        int maxDepth = getGraph().getNodeCount(); //limit how many iterations we can do

        //while we haven't reached the max depth
        while (depth <= maxDepth){
            //Initialize the visited set and the stack
            Set<City> visited = new HashSet<City>();
            Stack<Node> stack = new Stack<Node>();
                
            //push the start node onto the stack
            Node start = new Node(getStart(), null);
            stack.push(start);

            //while the stack is not empty
            while(!stack.isEmpty()){

                //pop the top node off the stack
                Node curr = stack.pop();

                //get the current city
                City current = curr.city;

                //add the current node to the visit list
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

                //get the connections from the current city
                List<Node> toAdd = new ArrayList<Node>();

                //if we haven't reached the max for this iteration
                if(SearchState.checkDepth(curr, depth)){

                    //sort connections by distance and return as a list
                    Stream<Entry<City,Double>> connStream = getGraph().getConnections(current).entrySet().stream();
                    connStream.forEach(e -> {     
                            //if the neighbor (child) is not in the path (a parent, grandparent, etc. of the current node)
                            if(!SearchState.checkCityForVisit(e.getKey(), visited, curr, findAllRoutes)){
                                //add the neighbor to the queue
                                Node neighborNode = new Node(e.getKey(), curr);
                                toAdd.add(neighborNode);
                            }
                        });
                }

                //add the nodes to the stack but in reverse order so that
                //the stack will be a depth first search
                for(Node n: toAdd.reversed()){
                    stack.push(n);
                }
            }
            depth++;
        }
        return new FindResult(routes, visitList);

    }
}
