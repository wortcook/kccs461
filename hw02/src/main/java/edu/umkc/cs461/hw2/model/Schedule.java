package edu.umkc.cs461.hw2.model;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record Schedule(Map<Activity,Assignment> assignments) {
    public Schedule {
        if (assignments == null) {
            throw new IllegalArgumentException("Assignments must not be null");
        }
    }

    public static boolean checkAllActivitiesScheduled(final Schedule schedule, final Set<Activity> activities) {
        return activities.stream().allMatch(activity -> schedule.assignments().containsKey(activity));
    }


    public static final String header =
    """
      Time\t\t | Activity\t|
    +----------------+----------------+
    """;

    public static final String row =
    """
    |\t\t | %s\t|
    | %s\t\t | %s\t|
    |\t\t | %s \t|
    +----------------+----------------+
    """;


    // Co-Pilot prompt.
    //I want to print the activities in 3 columns, M W F with the rows representing timeslots.  
    //Within each timeslot I want to print the activity, the room, and the facilitator.
    public static String scheduleToString(final Schedule schedule) {
        StringBuilder sb = new StringBuilder();

        sb.append(header);

        //Get the activities from the schedule and sort by time
        var sortedActivities = schedule.assignments().values().stream()
            .sorted((a1, a2) -> a1.timeslot().compareTo(a2.timeslot()))
            .collect(Collectors.toList());

        //Dateformatter that prints the day of the week as a single letter then the time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        //loop through activities and apply the row template
        for (Assignment assignment : sortedActivities) {
            //Get the day of the week

            //Only print M W F
            String time = sdf.format(assignment.timeslot());
            String activity = assignment.activity().name();
            String room = assignment.location().name();
            String facilitator = assignment.facilitator();

            sb.append(row.formatted(activity, time, room, facilitator));
        }
        


        return sb.toString();
    }
}
