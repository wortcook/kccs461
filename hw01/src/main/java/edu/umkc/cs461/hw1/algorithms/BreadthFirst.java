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
    public BreadthFirst(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    @Override
    public List<City> findRoute(){
        Set<City> visited = new HashSet<City>();
        Queue<City> queue = new ArrayDeque<City>();
        queue.add(getStart());
        while(!queue.isEmpty()){
            City current = queue.remove();
            if(current.equals(getEnd())){
                return new ArrayList<City>(visited);
            }
            visited.add(current);
            for(City neighbor : getGraph().getConnections(current).keySet()){
                if(!visited.contains(neighbor)){
                    queue.add(neighbor);
                }
            }
        }
        return new ArrayList<City>(visited);
    }
}
