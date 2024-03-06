package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;


import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

public class DepthFirst extends SearchState{
    
        private class Node{
            public final City city;
            public final Node parent;
            public Node(City city, Node parent){
                this.city = city;
                this.parent = parent;
            }
        }
    
        public DepthFirst(final City start, final City end, final BiDirectGraph<City> graph){
            super(start, end, graph);
        }
    
        @Override
        public List<City> findFirstRoute(){
            Set<City> visited = new HashSet<City>();
            Stack<Node> stack = new Stack<Node>();
            
            Node start = new Node(getStart(), null);
            stack.push(start);
    
            while(!stack.isEmpty()){
                Node curr = stack.pop();
                City current = curr.city;
                // System.out.println("Visiting " + current.getName());
                if(current.equals(getEnd())){
                    return createCityListFromNode(curr);
                }

                visited.add(current);

                getGraph().getConnections(current).entrySet().stream()
                    // .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                    .forEach(e -> {
                        if(!visited.contains(e.getKey())){
                            Node neighborNode = new Node(e.getKey(), curr);
                            stack.push(neighborNode);
                        }
                    });
            }
            return new ArrayList<City>();
        }

        @Override
        public List<List<City>> findAllRoutes(){
            Stack<Node> stack = new Stack<Node>();
            List<List<City>> routes = new ArrayList<List<City>>();
            
            Node start = new Node(getStart(), null);
            stack.push(start);
            while(!stack.isEmpty()){
                Node curr = stack.pop();
                City current = curr.city;
                if(current.equals(getEnd())){
                    routes.add(createCityListFromNode(curr));
                }

                //sort connections by distance and return as a list
                getGraph().getConnections(current).entrySet().stream()
                    // .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                    .forEach(e -> {
                        if(!NodeInPath(curr, e.getKey())){
                            Node neighborNode = new Node(e.getKey(), curr);
                            stack.push(neighborNode);
                        }
                    });
            }
            return routes;
        }        

        private boolean NodeInPath(Node node, City city){
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
