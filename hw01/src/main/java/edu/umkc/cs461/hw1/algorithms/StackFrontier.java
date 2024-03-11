package edu.umkc.cs461.hw1.algorithms;

import java.util.Stack;
import java.util.List;

/*
 * A frontier that uses a stack (LIFO) to store the nodes.
 * 
 */
public class StackFrontier implements Frontier<SearchState.Node> {
    private final Stack<SearchState.Node> stack = new Stack<>();

    /*
     * Clears the stack.
     */
    public void init(final SearchState searchState) {
        stack.clear();
    }

    /*
     * Adds the passed nodes to the stack. The nodes
     * are added in the reverse order they are passed so that
     * the first node is at the top of the stack.
     * @param searchState the current search state
     * @param nodes the nodes to add to the frontier
     */
    public void add(final SearchState searchState, List<SearchState.Node> nodes) {
        stack.addAll(nodes.reversed());
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
