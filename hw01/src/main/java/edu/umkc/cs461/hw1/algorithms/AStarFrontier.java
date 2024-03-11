package edu.umkc.cs461.hw1.algorithms;

import edu.umkc.cs461.hw1.data.City;

import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

public class AStarFrontier implements Frontier<SearchState.Node>{
    private final PriorityQueue<SearchState.Node> pqueue;

    public AStarFrontier(final City goal){
        this.pqueue = new PriorityQueue<SearchState.Node>((n1, n2) -> {
            //f(n)         =       g(n)    +      h(n)
            //total cost   =   distance to node + distance from node to goal

            final Function<SearchState.Node,Double> heuristic = new Function<SearchState.Node,Double>(){
                public Double apply(SearchState.Node node){
                    return (null==node.getHeuristic()) ? node.costFromStart() + node.city.distanceFrom(goal) : node.getHeuristic();
                }
            };

            double n1Value = heuristic.apply(n1);
            double n2Value = heuristic.apply(n2);

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
