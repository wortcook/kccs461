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


/*
 * Generalized search algorithm that can be used to implement
 * BFS, DFS, A*, etc. The framing code provides for state
 * tracking and visitation. The frontier is managed by the
 * frontier object. The frontier object can be a stack, queue,
 * or priority queue. The frontier object is responsible for
 * managing the order of the nodes in the search space, handling
 * removal of nodes from the search space, providing for the
 * empty check.
 * The find method allows for the search to be stopped early
 * if the first route is found. It also allows for the search
 * to continue until all routes are found, assuming the implementation
 * of the Frontier allows exhaustive search, i.e. for findAll the
 * frontier would ensure that all connections are handled.
 */
public class FlexiSearch extends SearchState{

    public FlexiSearch(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    //Default implementation is BFS
    public FindResult find(final boolean findAllRoutes){
        final Queue<Node> frontierStack = new ArrayDeque<Node>();
        return find(findAllRoutes, new Frontier<Node>(){
            public void add(SearchState srchState, List<Node> nodes){
                frontierStack.addAll(nodes);
            }
            public Node pull(SearchState srchState){
                return frontierStack.remove();
            }
            public boolean isEmpty(SearchState srchState){
                return frontierStack.isEmpty();
            }
        });            
    }

    /*
     * This is the generalized search algorithm. It is responsible for
     * managing the search space and the frontier. The frontier is
     * responsible for managing the order of the nodes in the search
     * space, handling removal of nodes from the search space, and
     * providing for the empty check.
     * The search algorithm is responsible for managing the visitation
     * of nodes and the creation of routes. The search algorithm
     * can be stopped early if the first route is found. It also
     * allows for the search to continue until all routes are found,
     * 
     * @param findAllRoutes - if true, the search will continue until
     * all frontier defined routes are found. If false, the search will stop after the
     * first route is found. Note, if findAllRoutes is True, the result
     * will not include the nodes visited.
     * @param frontier - the frontier object that manages the order of the nodes in the search space
     * @return FindResult - the result of the search. See FindResult for more details.
     * 
     */
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
            Node curr = frontier.pull(this);
            City current = curr.city;

            if(!findAllRoutes && visited.contains(current)){
                continue;
            }
            visitList.add(curr);
            visited.add(current);
            if(current.equals(end)){
                routes.add(SearchState.createCityListFromNode(curr));
                if(!findAllRoutes){
                    return new FindResult(routes, visitList);
                }
            }

            //Add all the connections from the current city to the frontier
            List<Node> toAdd = 
                    getGraph() //from the search space
                    .getConnections(current) //get the connections from the current city
                    .entrySet() //get the connections as a set of entries
                    .stream() //convert the set to a stream
                    //next filter out any cities that are already in the path or we have already visited
                    .filter(e -> !SearchState.checkCityForVisit(e.getKey(), visited, curr, findAllRoutes, frontier))
                    //then convert each city into a node
                    .map(e -> new Node(e.getKey(), curr))
                    //collect the nodes into a list
                    .collect(Collectors.toList());
            if(!toAdd.isEmpty()){
                //this is where we add the nodes to the frontier.
                //The following comments heavily used co-pilot to fill out the text. I should be noted
                //that the comments accuruately reflect the design of the code.
                //The nodes (cities) are ordered alphabetically by default. This is the default heuristic.
                //The frontier can manage these additions in any way it sees fit.
                //For example, the frontier could add the nodes to a stack, queue, or priority queue.
                //For a stack, the nodes would be added in reverse order.
                //Which would make the search a depth first search.
                //For a queue, the nodes would be added in the order they are found.
                //Which would make the search a breadth first search.
                //For a priority queue, the nodes would be added in order of the heuristic.
                //Which would make the search an A* search.               
                frontier.add(this, toAdd);
            }
        }
        return new FindResult(routes, visitList);
    }
}
