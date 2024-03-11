package edu.umkc.cs461.hw1.algorithms;

import edu.umkc.cs461.hw1.data.City;

import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * Best First Search algorithm implementation
 */
public class BestFirstFrontier implements Frontier<SearchState.Node> {

    private final City end;

    /*
     * Constructor for the BestFirst search algorithm
     * @param end The city to search for. Used to calculate the heuristic
     */
    public BestFirstFrontier(final City end){
        this.end = end;
    }

    private final Stack<SearchState.Node> stack = new Stack<>();


    /*
     * Clears the stack.
     */
    public void init(final SearchState searchState) {
        stack.clear();
    }

    /*
     * Adds the passed nodes to the stack. The nodes
     * are added in the order they are passed so that
     * the first node is at the top of the stack.
     * @param searchState the current search state
     * @param nodes the nodes to add to the frontier
     */
    public void add(final SearchState searchState, List<SearchState.Node> nodes) {
        Function<SearchState.Node,Double> heuristic = new Function<SearchState.Node,Double>(){
            public Double apply(SearchState.Node node){
                return (null==node.getHeuristic()) ? node.city.distanceFrom(end) : node.getHeuristic();
            }
        };

        //sort the nodes by the heuristic value and add them to the stack in reverse order
        //so that the node with the lowest heuristic value is at the top of the stack
        stack.addAll(
            nodes.stream().sorted((n1,n2) -> {
                double n1Value = heuristic.apply(n1);
                double n2Value = heuristic.apply(n2);
                return -Double.compare(n1Value, n2Value);
            }).collect(Collectors.toList())
        );
    }

    /*
     * Returns true if the stack is empty.
     * @param searchState the current search state
     * @return true if the stack is empty
     */
    public boolean isEmpty(final SearchState searchState) {
        return stack.isEmpty();
    }

    /*
     * Removes and returns the top node from the stack.
     * @param searchState the current search state
     * @return the top node from the stack
     */
    public SearchState.Node pull(final SearchState searchState) {
        return stack.pop();
    }
}
