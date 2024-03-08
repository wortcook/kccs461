package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class QueueFrontier implements Frontier<SearchState.Node>{
    private final Queue<SearchState.Node> queue = new LinkedList<>();

    public void init(final SearchState searchState) {
        queue.clear();
    }

    public void add(final SearchState searchState, List<SearchState.Node> nodes) {
        for (SearchState.Node node : nodes) {
            queue.add(node);
        }
    }

    public boolean isEmpty(final SearchState searchState) {
        return queue.isEmpty();
    }

    public SearchState.Node pull(final SearchState searchState) {
        return queue.poll();
    }
}
