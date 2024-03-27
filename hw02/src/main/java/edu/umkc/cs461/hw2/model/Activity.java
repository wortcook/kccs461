package edu.umkc.cs461.hw2.model;

import java.util.Set;

public record Activity(String name, int enrollment, Set<String> preferredFacilitators, Set<String> secondaryFacilitators){
    public Activity {
        if (enrollment <= 0) {
            throw new IllegalArgumentException("Enrollment must be greater than 0");
        }
    }

    //Everything below this line suggested by Co-Pilot
    public static boolean isFacilitatorPreferred(final Activity activity, final String facilitator) {
        return activity.preferredFacilitators().contains(facilitator);
    }

    public static boolean isFacilitatorSecondary(final Activity activity, final String facilitator) {
        return activity.secondaryFacilitators().contains(facilitator);
    }

    public static boolean isFacilitatorAvailable(final Activity activity, final String facilitator) {
        return isFacilitatorPreferred(activity, facilitator) || isFacilitatorSecondary(activity, facilitator);
    }

    public static boolean isFacilitatorAvailable(final Set<Activity> activities, final String facilitator) {
        return activities.stream().anyMatch(activity -> isFacilitatorAvailable(activity, facilitator));
    }

    public static boolean willFit(final Activity activity, final int attendees) {
        return activity.enrollment() >= attendees;
    }

    public static boolean willFit(final Set<Activity> activities, final int attendees) {
        return activities.stream().anyMatch(activity -> willFit(activity, attendees));
    }
}
