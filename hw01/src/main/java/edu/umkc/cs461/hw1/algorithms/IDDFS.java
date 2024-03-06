package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.w3c.dom.Node;

import edu.umkc.cs461.hw1.data.BiDirectGraph;
import edu.umkc.cs461.hw1.data.City;


public class IDDFS extends SearchState {

    private class Node{
        public final City city;
        public final Node parent;
        public Node(City city, Node parent){
            this.city = city;
            this.parent = parent;
        }
    }

    public IDDFS(final City start, final City end, final BiDirectGraph<City> graph) {
        super(start, end, graph);
    }

    @Override
    public List<City> findFirstRoute() {
        //iterative deepening depth first search
        int depth = 0;
        int maxDepth = getGraph().getNodeCount();

        while (depth <= maxDepth){
            Set<City> visited = new HashSet<City>();
            Stack<Node> stack = new Stack<Node>();
            
            Node start = new Node(getStart(), null);
            stack.push(start);

            int maxSearchDepth = depth;

            while(!stack.isEmpty()){
                Node curr = stack.pop();
                City current = curr.city;
                // System.out.println("Visiting " + current.getName());
                if(current.equals(getEnd())){
                    return createCityListFromNode(curr);
                }
                visited.add(current);

                if(maxSearchDepth == 0){
                    continue;
                }else{
                    maxSearchDepth--;
                }

                //sort connections by distance and return as a list
                getGraph().getConnections(current).entrySet().stream()
                    // .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                    .forEach(e -> {
                        if(!visited.contains(e.getKey())){
                            Node neighborNode = new Node(e.getKey(), curr);
                            stack.push(neighborNode);
                        }
                });
            }
            depth++;
        }
        return new ArrayList<City>();
    }

    @Override
    public List<List<City>> findAllRoutes() {
        return null;
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
