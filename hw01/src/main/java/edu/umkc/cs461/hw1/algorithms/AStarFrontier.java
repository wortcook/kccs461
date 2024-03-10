package edu.umkc.cs461.hw1.algorithms;

import edu.umkc.cs461.hw1.data.City;

import java.util.List;
import java.util.PriorityQueue;

public class AStarFrontier implements Frontier<SearchState.Node>{
    private final City goal;
    private final PriorityQueue<SearchState.Node> pqueue;

    public AStarFrontier(final City goal){
        this.goal = goal;
        this.pqueue = new PriorityQueue<SearchState.Node>((n1, n2) -> {
            //f(n)         =       g(n)    +      h(n)
            //total cost   =   distance to node + distance from node to goal
            double n1Value = SearchState.costFromStart(n1) + n1.city.distanceFrom(goal);
            double n2Value = SearchState.costFromStart(n2) + n2.city.distanceFrom(goal);
            return Double.compare(n1Value, n2Value);
        });
    }
    public void init(final SearchState searchState) {
        pqueue.clear();
    }

    public void add(final SearchState searchState, List<SearchState.Node> nodes) {
        pqueue.addAll(nodes);
    }

    public boolean isEmpty(final SearchState searchState) {
        return pqueue.isEmpty();
    }

    public SearchState.Node pull(final SearchState searchState) {
        return pqueue.poll();
    }


}
