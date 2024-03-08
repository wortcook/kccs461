package edu.umkc.cs461.hw1.algorithms;

import java.util.List;

public interface Frontier<K>{
    default public void init(final SearchState searchState){;}
    default public void add(final SearchState searchState, List<K> nodes){;}
    default public boolean isEmpty(final SearchState searchState) {
        return true;
    }
    default public K pull(final SearchState searchState) {
        return null;
    }
}
