package edu.umkc.cs461.hw2.model;

/**
 * Represents a room that can be scheduled.
 */
public record Room(String name, int capacity) implements Comparable<Room>{
    public Room {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
    }

    @Override
    public int compareTo(Room o) {
        return name.compareTo(o.name);
    }

    public static boolean willFit(final Room room, final Activity activity) {
        return room.capacity() >= activity.enrollment();
    }
}
