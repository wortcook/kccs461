package edu.umkc.cs461.hw2.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import edu.umkc.cs461.hw2.rules.ScheduleScorer;

/*
 * Global model containing the list of activities, facilitators, timeslots, and locations.
 */
public record Model(
    Map<String,Activity> activities,
    Map<String,String> facilitators,
    Map<String,Date> timeslots,
    Map<String,Room> locations
) {
    public Model {
        if (activities == null) {
            throw new IllegalArgumentException("Activities must not be null");
        }
        if (facilitators == null) {
            throw new IllegalArgumentException("Facilitators must not be null");
        }
        if (timeslots == null) {
            throw new IllegalArgumentException("Timeslots must not be null");
        }
        if (locations == null) {
            throw new IllegalArgumentException("Locations must not be null");
        }
    }

    public static Room getRandomRoom(final Model model) {
        return model.locations().values().stream().skip((int)(model.locations().size()*Math.random())).findFirst().get();
    }

    public static String getRandomFacilitator(final Model model) {
        return model.facilitators().values().stream().skip((int)(model.facilitators().size()*Math.random())).findFirst().get();
    }

    public static Date getRandomTimeslot(final Model model) {
        return model.timeslots().values().stream().skip((int)(model.timeslots().size()*Math.random())).findFirst().get();
    }

    private static final Map<Schedule,Double> globalScoreMap = Collections.synchronizedMap(new HashMap<>());

    public static Double fetchScore(final Schedule schedule, final Model model) {
        Double retVal = globalScoreMap.get(schedule);
        
        if(null == retVal){
            retVal = ScheduleScorer.scoreSchedule(model, schedule).score();
            globalScoreMap.put(schedule, retVal);
        }


        return retVal;
    }

    public static List<Schedule> sortPopulation(final List<Schedule> population, final Model model){
        Schedule[] schedules = population.toArray(new Schedule[0]);
        Arrays.parallelSort(schedules, (s1, s2) -> {
            return fetchScore(s1, model).compareTo(fetchScore(s2, model));
        });
        return Arrays.asList(schedules);
    }
}