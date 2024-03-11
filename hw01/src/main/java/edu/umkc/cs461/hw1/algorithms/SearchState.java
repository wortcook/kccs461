package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.ArrayList;

import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

/*
 * Search helper class that provides a common interface for the different search algorithms
 * and provides some utility methods for the algorithms to use.
 * 
 */
public abstract class SearchState {
    private final City start;
    private final City end;
    private final BiDirectGraph<City> graph;

    /*
     * Node class that represents a node in the search tree. It is possible
     * that the tree will contain the same city in different locations depending
     * on the search algorithm used.
     */
    public class Node{
        /*
         * The city that this node represents
         */
        public final City city;

        /*
         * The parent node of this node. This is used to track the path.
         * If null then the node is the start node, i.e. the start city.
         */
        public final Node parent;

        /*
         * The number of steps from the start node to this node.
         */
        private int depth = -1;

        /*
         * The cost(distance) from the start node to this node.
         */
        private double cost = 0;

        /**
         * The heuristic value for this node, this is an 
         * optimization so that the heuristic is only calculated
         * once.
         */
        private Double heuristic = null;

        /*
         * Constructor for the node
         * @param city The city that this node represents
         * @param parent The parent node of this node
         */
        public Node(City city, Node parent){
            this.city = city;
            this.parent = parent;
        }

        /*
         * Find the depth of this node in the tree
         * @return The depth of this node in the tree
         */
        public int findDepth(){
            if( this.parent == null){
                this.depth = 0;
            }else{
                this.depth = this.parent.findDepth() + 1;
            }
            return this.depth;
        }

        /*
         * Calculate the cost from the start node to this node
         * @return The cost from the start node to this node
         */
        public double costFromStart(){
            if(this.parent == null){
                this.cost = 0.0;
            }else{
                double cost = this.city.distanceFrom(this.parent.city) + this.parent.cost;
                this.cost = cost;
            }
    
            return this.cost;
        }

        /**
         * Get the heuristic value for this node
         * @return The heuristic value for this node
         */
        public Double getHeuristic(){
            return heuristic;
        }

        /**
         * Set the heuristic value for this node
         * @param heuristic The heuristic value supplier for this node
         */
        public double getHeuristic(Supplier<Double> heuristic){
            if(null == this.heuristic){
                this.heuristic = heuristic.get();
            }
            return this.heuristic;
        }
    }

    /*
     * The result of the search
     */
    public static class FindResult{

        /*
         * The routes found by the search. If findAllRoutes is true, then
         * this list will contain all the routes found by the search. If
         * findAllRoutes is false, then this list will contain only the
         * first route found by the search.
         */
        public final List<List<City>> routes;

        /*
         * The nodes visited by the search. This list will contain all the
         * nodes visited by the search. If findAllRoutes is false, then
         * this list will contain only the nodes visited by the search
         * that are part of the first route found by the search.
         * If findAllRoutes is true, then this list will be empty.
         */
        public final List<Node> visitList;


        /*
         * Constructor for the result of the search
         * @param routes The routes found by the search
         * @param visitList The nodes visited by the search
         */
        public FindResult(final List<List<City>> routes, final List<Node> visitList){
            this.routes = routes;
            this.visitList = visitList;
        }
    }



    /*
     * Constructor for the search state. This is the base constructure that
     * all search algorithms will use.
     * @param start The start city for the search
     * @param end The end city for the search
     * @param graph The graph that the search will be performed on
     */
    public SearchState(final City start, final City end, final BiDirectGraph<City> graph){
        this.start = start;
        this.end = end;
        this.graph = graph;
    }

    /*
     * Get the start city for the search
     * @return The start city for the search
     */
    public City getStart() {
        return start;
    }

    /*
     * Get the end city for the search
     * @return The end city for the search
     */
    public City getEnd() {
        return end;
    }

    /*
     * Get the graph for the search
     * @return The graph for the search
     */
    public BiDirectGraph<City> getGraph(){
        return graph;
    }

    /*
     * Find the route(s) from the start city to the end city. Default implementation
     * is to find the first route with a null frontier passed.
     * @return The result of the search
     */
    public FindResult find(){
        return find(false);
    }

    /*
     * Find the route(s) from the start city to the end city. Default implementation
     * is to use a null frontier.
     * @param findAllRoutes If true, find all routes, otherwise just find the first route
     * @return The result of the search
     */
    public FindResult find(final boolean findAllRoutes){
        return find(findAllRoutes, null);
    }

    /*
     * This is the generalized search algorithm. It is responsible for
     * managing the search space and the frontier. The frontier is
     * responsible for managing the order of the nodes in the search
     * space, handling removal of nodes from the search space, and
     * providing for the empty check.
     * @param findAllRoutes - if true, the search will continue until
     * all frontier defined routes are found. If false, the search will stop after the
     * first route is found. Note, if findAllRoutes is True, the result
     * will not include the nodes visited.
     * @param frontier - the frontier object that manages the order of the nodes in the search space
     * @return FindResult - the result of the search. See FindResult for more details.
     */
    public abstract FindResult find(final boolean findAllRoutes, Frontier<Node> frontier);


    //Utility methods

    /*
     * Check if a city has been visited in a given path
     * i.e. starting with a leaf node, does the city exist
     * anywhere in the ancestor nodes.
     * @param node The node to start the search from
     * @param city The city to search for
     */
    public static boolean isNodeInPath(Node node, City city){
        while(node != null){
            if(node.city.equals(city)){
                return true;
            }
            node = node.parent;
        }
        return false;
    }

    /*
     * Create a list of cities from a node. This is used to create the route
     * from the start city to the end city. The node is the end of the path
     * i.e. the end city and the start city is the root of the path.
     * @param node The node to start the search from
     */
    public static List<City> createCityListFromNode(Node node){
        List<City> path = new ArrayList<City>();
        while(node != null){
            path.add(0, node.city);
            node = node.parent;
        }
        return path;
    }

    /*
     * Check if a city has been visited in a given set of visited cities using a null frontier
     * @param theCity The city to check for visitation
     * @param visited The set of visited cities
     * @param checkPath The path to check for visitation
     * @param usePath If true, use the path to check for visitation, otherwise use the visited set
     * @return True if the city has been visited, otherwise false
     */
    public static boolean checkCityForVisit(final City theCity, final Set<City> visited, final Node checkPath, boolean usePath){
        return checkCityForVisit(theCity, visited, checkPath, usePath, null);
    }

    /*
     * Check if a city has been visited in a given set of visited cities. If usePath is true
     * then the path is used to check for visitation, otherwise the visited set is used.
     * Note that for this implementation frontier is passed but not currently used.
     * @param theCity The city to check for visitation
     * @param visited The set of visited cities
     * @param checkPath The path to check for visitation
     * @param usePath If true, use the path to check for visitation, otherwise use the visited set
     * @param frontier The frontier. Not currently used.
     */
    public static boolean checkCityForVisit(final City theCity, final Set<City> visited, final Node checkPath, boolean usePath, Frontier<Node> frontier){
        if(usePath){
            return isNodeInPath(checkPath, theCity);
        }else{
            return visited.contains(theCity);
        }
    }

    /*
     * Check if the depth of a node is less than or equal to a given depth
     * @param node The node to check the depth of
     * @param depth The depth to check against
     * @return True if the depth of the node is less than or equal to the given depth, otherwise false
     */
    public static boolean checkDepth(Node node, final int depth){
        return node.findDepth() <= depth;
    }

}

