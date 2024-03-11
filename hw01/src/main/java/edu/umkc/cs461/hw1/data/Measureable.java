package edu.umkc.cs461.hw1.data;

/*
 * An interface for objects that can be measured, i.e. there is a distance from
 * one object to another.
 */
public interface Measureable<K> {
    public double distanceFrom(K other);
}