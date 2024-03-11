package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

/*
 * A frontier that uses a queue (FIFO) to store the nodes.
 */
public class QueueFrontier implements Frontier<SearchState.Node>{
    private final Queue<SearchState.Node> queue = new LinkedList<>();

    /*
     * Clears the queue.
     */
    public void init(final SearchState searchState) {
        queue.clear();
    }

    /*
     * Adds the passed nodes to the queue. The nodes are added in the order
     * they are passed. The first node is at the head of the queue.
     * @param searchState the current search state
     * @param nodes the nodes to add to the frontier
     */
    public void add(final SearchState searchState, List<SearchState.Node> nodes) {
        for (SearchState.Node node : nodes) {
            queue.add(node);
        }
    }

    /*
     * Returns true if the queue is empty.
     * @param searchState the current search state
     * @return true if the queue is empty
     */
    public boolean isEmpty(final SearchState searchState) {
        return queue.isEmpty();
    }

    /*
     * Removes and returns the head node from the queue.
     * @param searchState the current search state
     * @return the head node from the queue
     */
    public SearchState.Node pull(final SearchState searchState) {
        return queue.poll();
    }
}
