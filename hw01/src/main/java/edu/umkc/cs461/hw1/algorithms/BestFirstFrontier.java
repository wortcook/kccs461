package edu.umkc.cs461.hw1.algorithms;

import edu.umkc.cs461.hw1.data.City;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class BestFirstFrontier implements Frontier<SearchState.Node> {

    private final City end;

    public BestFirstFrontier(final City end){
        this.end = end;
    }

    private final Stack<SearchState.Node> stack = new Stack<>();

    public void init(final SearchState searchState) {
        stack.clear();
    }

    public void add(final SearchState searchState, List<SearchState.Node> nodes) {
        stack.addAll(
            nodes.stream().sorted((n1,n2) -> {
                double n1Value = n1.city.distanceFrom(end);
                double n2Value = n2.city.distanceFrom(end);
                return -Double.compare(n1Value, n2Value);
            }).collect(Collectors.toList())
        );
    }

    public boolean isEmpty(final SearchState searchState) {
        return stack.isEmpty();
    }

    public SearchState.Node pull(final SearchState searchState) {
        return stack.pop();
    }

    
}
