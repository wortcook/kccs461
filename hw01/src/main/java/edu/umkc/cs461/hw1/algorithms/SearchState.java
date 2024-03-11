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

        public double costFromStart(){
            if(this.parent == null){
                this.cost = 0.0;
            }else{
                double cost = this.city.distanceFrom(this.parent.city) + this.parent.cost;
                this.cost = cost;
            }
    
            return this.cost;
        }

        public Double getHeuristic(){
            return heuristic;
        }

        public double getHeuristic(Supplier<Double> heuristic){
            return heuristic.get();
        }
    }

    public static class FindResult{
        public final List<List<City>> routes;
        public final List<Node> visitList;

        public FindResult(final List<List<City>> routes, final List<Node> visitList){
            this.routes = routes;
            this.visitList = visitList;
        }
    }



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

