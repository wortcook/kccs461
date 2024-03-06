package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;


import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

public class BreadthFirst extends SearchState{

    private class Node{
        public final City city;
        public final Node parent;
        public Node(City city, Node parent){
            this.city = city;
            this.parent = parent;
        }
    }

    public BreadthFirst(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    @Override
    public List<City> findFirstRoute(){
        Set<City> visited = new HashSet<City>();
        Queue<Node> queue = new ArrayDeque<Node>();
        
        Node start = new Node(getStart(), null);
        queue.add(start);

        while(!queue.isEmpty()){
            Node curr = queue.remove();
            City current = curr.city;
            // System.out.println("Visiting " + current.getName());
            if(current.equals(getEnd())){
                return createCityListFromNode(curr);
            }
            visited.add(current);
            for(City neighbor : getGraph().getConnections(current).keySet()){
                if(!visited.contains(neighbor)){
                    Node neighborNode = new Node(neighbor, curr);
                    queue.add(neighborNode);
                }
            }
        }
        return new ArrayList<City>();
    }

    @Override
    public List<List<City>> findAllRoutes(){
        Queue<Node> queue = new ArrayDeque<Node>();
        List<List<City>> routes = new ArrayList<List<City>>();
        
        Node start = new Node(getStart(), null);
        queue.add(start);

        while(!queue.isEmpty()){
            Node curr = queue.remove();
            City current = curr.city;
            // System.out.println("Visiting " + current.getName());
            if(current.equals(getEnd())){
                routes.add(createCityListFromNode(curr));
            }
            for(City neighbor : getGraph().getConnections(current).keySet()){
                if(!nodeInPath(curr, neighbor)){
                    Node neighborNode = new Node(neighbor, curr);
                    queue.add(neighborNode);
                }
            }
        }
        return routes;
    }

    private boolean nodeInPath(Node node, City city){
        while(node != null){
            if(node.city.equals(city)){
                return true;
            }
            node = node.parent;
        }
        return false;
    }

    private List<City> createCityListFromNode(Node node){
        List<City> path = new ArrayList<City>();
        while(node != null){
            path.add(0, node.city);
            node = node.parent;
        }
        return path;
    }
}
