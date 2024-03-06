package edu.umkc.cs461.hw1.algorithms;

import java.util.List;

import edu.umkc.cs461.hw1.data.BiDirectGraph;
import edu.umkc.cs461.hw1.data.City;

public class BestFirst extends SearchState{
    public BestFirst(final City start, final City end, final BiDirectGraph<City> graph){
        super(start, end, graph);
    }

    @Override
    public List<City> findFirstRoute(){
        //best first search using distance as the heuristic
        return null;
    }

    @Override
    public List<List<City>> findAllRoutes() {
        return null;
    }

}
