package edu.umkc.cs461.hw1.algorithms;

import java.util.Stack;
import java.util.List;

public class StackFrontier implements Frontier<SearchState.Node> {
    private final Stack<SearchState.Node> stack = new Stack<>();

    public void init(final SearchState searchState) {
        stack.clear();
    }

    public void add(final SearchState searchState, List<SearchState.Node> nodes) {
        for (SearchState.Node node : nodes) {
            stack.push(node);
        }
    }

    public boolean isEmpty(final SearchState searchState) {
        return stack.isEmpty();
    }

    public SearchState.Node pull(final SearchState searchState) {
        return stack.pop();
    }
}
