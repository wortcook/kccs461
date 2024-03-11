package edu.umkc.cs461.hw1.algorithms;

import java.util.List;

/*
 * A frontier is a data structure that stores the nodes that are candidates for expansion.
 */
public interface Frontier<K>{

    /*
     * Initializes the frontier. Called before the search begins or when the search is reset.
     */
    default public void init(final SearchState searchState){;}

    /*
     * Adds the passed nodes to the frontier.
     * @param searchState the current search state
     * @param nodes the nodes to add to the frontier
     */
    default public void add(final SearchState searchState, List<K> nodes){;}

    /*
     * Returns true if the frontier is empty.
     * @param searchState the current search state
     * @return true if the frontier is empty
     */
    default public boolean isEmpty(final SearchState searchState) {
        return true;
    }

    /*
     * Removes and returns a node from the frontier.
     * @param searchState the current search state
     * @return a node from the frontier
     */
    default public K pull(final SearchState searchState) {
        return null;
    }
}
