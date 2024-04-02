package edu.umkc.cs461.hw2.rules;

import java.util.HashMap;
import java.util.Map;

import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Schedule;

public class ScheduleScorer {
    public static Scorer.ScheduleScore scoreSchedule(final Model model, final Schedule schedule) {
        Double score = 0.0;

        Map<String, Double> scoreInfo = new HashMap<>();

        Scorer.ScheduleScore schedScore = null;

        schedScore = new Scorer.ActivityInSamePlaceAndTimeScorer().scoreSchedule(model, schedule);
        score += schedScore.score();
        scoreInfo.putAll(schedScore.scoreBreakdown());

        schedScore = new Scorer.RoomCapacityScorer().scoreSchedule(model, schedule);
        score += schedScore.score();
        scoreInfo.putAll(schedScore.scoreBreakdown());

        schedScore = new Scorer.FacilitatorScorer().scoreSchedule(model, schedule);
        score += schedScore.score();
        scoreInfo.putAll(schedScore.scoreBreakdown());

        schedScore = new Scorer.FacilitatorLoadScorer().scoreSchedule(model, schedule);
        score += schedScore.score();
        scoreInfo.putAll(schedScore.scoreBreakdown());

        schedScore = new Scorer.ActivityTimingScorer().scoreSchedule(model, schedule);
        score += schedScore.score();
        scoreInfo.putAll(schedScore.scoreBreakdown());

        return new Scorer.ScheduleScore(score, scoreInfo);
    }
}
