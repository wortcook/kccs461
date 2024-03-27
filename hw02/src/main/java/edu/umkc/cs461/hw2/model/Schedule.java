package edu.umkc.cs461.hw2.model;

import java.util.Map;
import java.util.Set;

public record Schedule(Map<Activity,Assignment> assignments) {
    public Schedule {
        if (assignments == null) {
            throw new IllegalArgumentException("Assignments must not be null");
        }
    }

    public static boolean checkAllActivitiesScheduled(final Schedule schedule, final Set<Activity> activities) {
        return activities.stream().allMatch(activity -> schedule.assignments().containsKey(activity));
    }
}
