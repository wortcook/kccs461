package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.ArrayList;

import edu.umkc.cs461.hw1.data.City;
import edu.umkc.cs461.hw1.data.BiDirectGraph;

public abstract class SearchState {
    private final City start;
    private final City end;
    private final BiDirectGraph<City> graph;

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

    public abstract List<City> findFirstRoute();
    public abstract List<List<City>> findAllRoutes();
}

