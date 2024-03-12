package edu.umkc.cs461.hw1.algorithms;

import java.util.Collections;
import java.util.LinkedList;

public class RandomFrontier implements Frontier<SearchState.Node>{
    private LinkedList<SearchState.Node> list = new LinkedList<>();

    public void init(final SearchState searchState){
        list.clear();
    }

    public void add(SearchState srchState, java.util.List<SearchState.Node> nodes){
        list.addAll(nodes);
    }

    public SearchState.Node pull(SearchState srchState){
        Collections.shuffle(list);
        return list.removeFirst();
    }

    public boolean isEmpty(SearchState srchState){
        return list.isEmpty();
    }
}
