package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.HashMap;

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

    public City getStart() {
        return start;
    }

    public City getEnd() {
        return end;
    }

    public BiDirectGraph<City> getGraph(){
        return graph;
    }

    public FindResult find(){
        return find(false);
    }

    public FindResult find(final boolean findAllRoutes){
        return find(findAllRoutes, null);
    }

    public abstract FindResult find(final boolean findAllRoutes, Frontier<Node> frontier);


    //Utility methods
    public static boolean isNodeInPath(Node node, City city){
        while(node != null){
            if(node.city.equals(city)){
                return true;
            }
            node = node.parent;
        }
        return false;
    }

    public static List<City> createCityListFromNode(Node node){
        List<City> path = new ArrayList<City>();
        while(node != null){
            path.add(0, node.city);
            node = node.parent;
        }
        return path;
    }

    public static boolean checkCityForVisit(final City theCity, final Set<City> visited, final Node checkPath, boolean usePath){
        return checkCityForVisit(theCity, visited, checkPath, usePath, null);
    }
    public static boolean checkCityForVisit(final City theCity, final Set<City> visited, final Node checkPath, boolean usePath, Frontier<Node> frontier){
        if(usePath){
            return isNodeInPath(checkPath, theCity);
        }else{
            return visited.contains(theCity);
        }
    }

    public static boolean checkDepth(Node node, final int depth){
        return node.findDepth() <= depth;
    }

}

