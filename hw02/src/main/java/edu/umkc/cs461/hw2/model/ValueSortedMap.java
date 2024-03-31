package edu.umkc.cs461.hw2.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

public class ValueSortedMap<K,V extends Comparable<V>> implements NavigableMap<K,V>{

    private final Map<K,V> internalMap = new HashMap<K,V>();

    private final Comparator<K> comparator = new Comparator<K>() {
        public int compare(final K o1,final K o2) {
            final V v1 = internalMap.get(o1);
            final V v2 = internalMap.get(o2);

            int retVal = v1.compareTo(v2);
            if(0 == retVal){
                return Math.random() > 0.5 ? 1 : -1;
            }
            return v1.compareTo(v2);
        }
    };

    private final NavigableMap<K,V> sortMap = new TreeMap<K,V>(this.comparator);

    @Override
    public Comparator<K> comparator() {
        return comparator;
    }

    @Override
    public K firstKey() {
        return sortMap.firstKey();
    }

    @Override
    public K lastKey() {
        return sortMap.lastKey();
    }

    @Override
    public Set<K> keySet() {
        return sortMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return sortMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return sortMap.entrySet();
    }

    @Override
    public int size() {
        return sortMap.size();
    }

    @Override
    public boolean isEmpty() {
        return sortMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return sortMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return sortMap.containsKey(value);
    }

    @Override
    public V get(Object key) {
        return sortMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        if(null==value){
            throw new IllegalArgumentException();
        }
        internalMap.put(key, value);
        return sortMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        sortMap.remove(key);
        return internalMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        internalMap.putAll(m);
        sortMap.putAll(m);
    }

    @Override
    public void clear() {
        sortMap.clear();
        internalMap.clear();
    }

    @Override
    public Entry<K, V> lowerEntry(K key) {
        return sortMap.lowerEntry(key);
    }

    @Override
    public K lowerKey(K key) {
        return sortMap.lowerKey(key);
    }

    @Override
    public Entry<K, V> floorEntry(K key) {
        return sortMap.floorEntry(key);
    }

    @Override
    public K floorKey(K key) {
        return sortMap.floorKey(key);
    }

    @Override
    public Entry<K, V> ceilingEntry(K key) {
        return sortMap.ceilingEntry(key);
    }

    @Override
    public K ceilingKey(K key) {
        return sortMap.ceilingKey(key);
    }

    @Override
    public Entry<K, V> higherEntry(K key) {
        return sortMap.higherEntry(key);
    }

    @Override
    public K higherKey(K key) {
        return sortMap.higherKey(key);
    }

    @Override
    public Entry<K, V> firstEntry() {
        return sortMap.firstEntry();
    }

    @Override
    public Entry<K, V> lastEntry() {
        return sortMap.lastEntry();
    }

    @Override
    public Entry<K, V> pollFirstEntry() {
        return sortMap.pollFirstEntry();
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        return sortMap.pollFirstEntry();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return sortMap.descendingMap();
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return sortMap.navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return sortMap.descendingKeySet();
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey,
            boolean toInclusive) {
        return sortMap.subMap(fromKey, fromInclusive, toKey, toInclusive);
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return sortMap.headMap(toKey, inclusive);
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return sortMap.tailMap(fromKey, inclusive);
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return sortMap.subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return sortMap.headMap(toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return sortMap.tailMap(fromKey);
    }
}
