package edu.umkc.cs461.hw2.rules;

import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Schedule;

public class ScheduleScorer {
    public static Double scoreSchedule(final Model model, final Schedule schedule) {
        Double score = 0.0;

        score+= new Scorer.ActivityInSamePlaceAndTimeScorer().scoreSchedule(model, schedule);
        score+= new Scorer.RoomCapacityScorer().scoreSchedule(model, schedule);
        score+= new Scorer.FacilitatorScorer().scoreSchedule(model, schedule);

        return score;
    }
}
