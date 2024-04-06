package edu.umkc.cs461.hw2.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
 * Represents an assignment of an activity to a timeslot, facilitator, and location.
 */
public record Assignment(Activity activity, Date timeslot, String facilitator, Room location) implements Comparable<Assignment>{
    public Assignment {
        Objects.requireNonNull(activity);
        Objects.requireNonNull(timeslot);
        Objects.requireNonNull(facilitator);
        Objects.requireNonNull(location);
    }

    @Override
    public int compareTo(Assignment o) {
        return activity.compareTo(o.activity);
    }

    public static boolean areTimesEqual(final Assignment a1, final Assignment a2) {
        return a1.timeslot().equals(a2.timeslot());
    }

    public static boolean areActivitiesEqual(final Assignment a1, final Assignment a2) {
        return a1.activity().equals(a2.activity());
    }

    public static boolean areFacilitatorsEqual(final Assignment a1, final Assignment a2) {
        return a1.facilitator().equals(a2.facilitator());
    }

    public static boolean areLocationsEqual(final Assignment a1, final Assignment a2) {
        return a1.location().equals(a2.location());
    }

    public static int hourDistanceBetween(final Assignment a1, final Assignment a2) {
        final long diffInMillies = a1.timeslot().getTime() - a2.timeslot().getTime();
        return (int) Math.abs((diffInMillies / (1000 * 60 * 60))); // milliseconds to hours
    }

    public static void sortAssignmentsByTime(final List<Assignment> assignments) {
        assignments.sort(Comparator.comparing(Assignment::timeslot));
    }
}
