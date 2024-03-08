package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

public abstract class SearchState {
    private final City start;
    private final City end;
    private final BiDirectGraph<City> graph;

    public class Node{
        public final City city;
        public final Node parent;
        private int depth = -1;
        public Node(City city, Node parent){
            this.city = city;
            this.parent = parent;
        }

        public int findDepth(){
            if(this.depth == -1){
                if(parent == null){
                    this.depth = 0;
                }else{
                    this.depth = parent.findDepth() + 1;
                }
            }
            return this.depth;
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

    private final static Map<Node, Double> costMap = new HashMap<Node, Double>();
    public static double costFromStart(final Node curr){
        if(costMap.containsKey(curr)){
            return costMap.get(curr);
        }
        double cost = 0;
        Node node = curr;
        while(node != null){
            cost += node.city.distanceFrom(node.parent == null ? node.city : node.parent.city);
            node = node.parent;
        }
        costMap.put(curr, cost);
        return cost;
    }
}

