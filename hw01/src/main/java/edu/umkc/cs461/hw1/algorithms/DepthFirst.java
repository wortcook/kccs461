package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;


import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

public class DepthFirst extends SearchState{



    public DepthFirst(City start, City end, BiDirectGraph<City> graph) {
        super(start, end, graph);
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<City> findRoute(){
        Set<City> visited = new HashSet<City>();
        Stack<City> stack = new Stack<City>();
        stack.push(getStart());
        while(!stack.isEmpty()){
            City current = stack.pop();
            if(current.equals(getEnd())){
                return new ArrayList<City>(visited);
            }
            visited.add(current);
            for(City neighbor : getGraph().getConnections(current).keySet()){
                if(!visited.contains(neighbor)){
                    stack.push(neighbor);
                }
            }
        }
        return new ArrayList<City>(visited);
    }

}
