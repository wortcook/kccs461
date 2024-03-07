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

public class IDDFS extends SearchState {

    public IDDFS(final City start, final City end, final BiDirectGraph<City> graph) {
        super(start, end, graph);
    }

    @Override
    public FindResult find(final boolean sortByDistance, final boolean findAllRoutes){
        final List<Node> visitList = new ArrayList<Node>();
        List<List<City>> routes = new ArrayList<List<City>>();
        //iterative deepening depth first search
        int depth = 0;
        int maxDepth = getGraph().getNodeCount(); //limit how many iterations we can do

        while (depth <= maxDepth){
            Set<City> visited = new HashSet<City>();
            Stack<Node> stack = new Stack<Node>();
                
            Node start = new Node(getStart(), null);
            stack.push(start);

            while(!stack.isEmpty()){
                Node curr = stack.pop();
                City current = curr.city;
                // System.out.println("Visiting " + current.getName());
                visitList.add(curr);
                visited.add(current);
                if(current.equals(getEnd())){
                    routes.add(SearchState.createCityListFromNode(curr));
                    if(!findAllRoutes){
                        return new FindResult(routes, visitList);
                    }
                }

                if(SearchState.checkDepth(curr, depth)){
                    Stream<Entry<City,Double>> connStream = getGraph().getConnections(current).entrySet().stream();
                    if(sortByDistance){
                        connStream = connStream.sorted((e1, e2) -> -e1.getValue().compareTo(e2.getValue()));
                    }
                    connStream.forEach(e -> {     
                            //if the neighbor (child) is not in the path (a parent, grandparent, etc. of the current node)
                            if(!SearchState.checkCityForVisit(e.getKey(), visited, curr, findAllRoutes)){
                                //add the neighbor to the queue
                                Node neighborNode = new Node(e.getKey(), curr);
                                stack.push(neighborNode);
                            }
                        });
                }
            }
            depth++;
        }
        return new FindResult(routes, visitList);

    }
}
