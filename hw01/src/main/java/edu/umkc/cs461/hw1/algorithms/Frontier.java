package edu.umkc.cs461.hw1.algorithms;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Frontier<K>{
    private final Function<SearchState, Void> initFcn;
    private final BiFunction<SearchState, List<K>, Void> addFcn;
    private final Function<SearchState, K> removeFcn;
    private final Function<SearchState, Boolean> isEmptyFcn;
    public Frontier(
        final Function<SearchState, Void> initFcn,
        final BiFunction<SearchState, List<K>, Void> addFcn,
        final Function<SearchState, K> removeFcn,
        final Function<SearchState, Boolean> isEmptyFcn
    ){
        this.initFcn = initFcn;
        this.addFcn = addFcn;
        this.removeFcn = removeFcn;
        this.isEmptyFcn = isEmptyFcn;
    }

    public void init(final SearchState searchState){
        initFcn.apply(searchState);
    }

    public void add(final SearchState searchState, List<K> nodes){
        addFcn.apply(searchState, nodes);
    }

    public boolean isEmpty(final SearchState searchState) {
        return isEmptyFcn.apply(searchState);
    }

    public K remove(final SearchState searchState) {
        return removeFcn.apply(searchState);
    }

    public boolean continueNode(final SearchState searchState, final K node){
        return true;
    }
}
