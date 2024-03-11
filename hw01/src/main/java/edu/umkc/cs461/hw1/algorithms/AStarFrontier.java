package edu.umkc.cs461.hw1.algorithms;

import edu.umkc.cs461.hw1.data.City;

import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

/*
 * A frontier that uses a priority queue to store the nodes.
 */
public class AStarFrontier implements Frontier<SearchState.Node>{
    private final PriorityQueue<SearchState.Node> pqueue;

    /*
     * Constructor for the A* search algorithm
     * @param goal The city to search for. Used to calculate the heuristic
     */
    public AStarFrontier(final City goal){
        this.pqueue = new PriorityQueue<SearchState.Node>((n1, n2) -> {
            //f(n)         =       g(n)    +      h(n)
            //total cost   =   distance to node + distance from node to goal

            final Function<SearchState.Node,Double> heuristic = new Function<SearchState.Node,Double>(){
                public Double apply(SearchState.Node node){
                    //return the heuristic value if it is set, otherwise calculate it
                    //using the distance from the start to the node and the distance from the node to the goal
                    return (null==node.getHeuristic()) ? node.costFromStart() + node.city.distanceFrom(goal) : node.getHeuristic();
                }
            };

            double n1Value = heuristic.apply(n1);
            double n2Value = heuristic.apply(n2);

            return Double.compare(n1Value, n2Value);
        });
    }

    /*
     * Clears the priority queue.
     */
    public void init(final SearchState searchState) {
        pqueue.clear();
    }

    /*
     * Adds the passed nodes to the priority queue.
     * @param searchState the current search state
     * @param nodes the nodes to add to the frontier
     */
    public void add(final SearchState searchState, List<SearchState.Node> nodes) {
        pqueue.addAll(nodes);
    }

    /*
     * Returns true if the priority queue is empty.
     * @param searchState the current search state
     * @return true if the priority queue is empty
     */
    public boolean isEmpty(final SearchState searchState) {
        return pqueue.isEmpty();
    }

    /*
     * Removes and returns the head node from the priority queue.
     * @param searchState the current search state
     * @return the head node from the priority queue
     */
    public SearchState.Node pull(final SearchState searchState) {
        return pqueue.poll();
    }
}
