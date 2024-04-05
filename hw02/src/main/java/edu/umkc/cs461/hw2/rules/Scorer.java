package edu.umkc.cs461.hw2.rules;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import edu.umkc.cs461.hw2.model.*;

/**
 * Interface for scoring a schedule.
 */
public interface Scorer {

    public static final long ONE_HOUR = 60 * 60 * 1000;
    public static final long TWO_HOURS = 2 * 60 * 60 * 1000;
    public static final long FOUR_HOURS = 4 * 60 * 60 * 1000;

    /**
     * Represents the score of a schedule.
     */
    public record ScheduleScore(double score, Map<String, Double> scoreBreakdown){
        public ScheduleScore {
            Objects.requireNonNull(scoreBreakdown);
        }
    }

    /**
     * Scores an assignment in the context of a schedule.
     * @param model - the model, i.e. the domain of the data
     * @param schedule - the schedule to which the assignment belongs
     * @param assignment - the assignment to score
     * @return the score of the assignment
     */
    default ScheduleScore scoreAssignment( Model model, Schedule schedule, Assignment assignment){
        return new ScheduleScore(0.0, Map.of());
    }

    /**
     * Scores an entire schedule by iterating through the assignments and scoring each one.
     * @param model
     * @param schedule
     * @return
     */
    default ScheduleScore scoreSchedule(final Model model, final Schedule schedule){
        ConcurrentHashMap<String, Double> scoreBreakdown = new ConcurrentHashMap<>(schedule.assignments().size(), 1.0f, Runtime.getRuntime().availableProcessors());

        double score = schedule.assignments().values().parallelStream().mapToDouble(assignment -> {
            ScheduleScore assignmentScore = scoreAssignment(model, schedule, assignment);
            scoreBreakdown.putAll(assignmentScore.scoreBreakdown());
            return assignmentScore.score();
        }).sum();
            
        return new ScheduleScore(score, scoreBreakdown);
    }


    public static class ActivityInSamePlaceAndTimeScorer implements Scorer{
        //Note that this implementation double counts the penalty for each pair of activities that are in the same place and time
        //which is okay for this assignment because we cannot have more than one activity in the same place and time
        @Override
        public ScheduleScore scoreAssignment(Model model, Schedule schedule, Assignment assignment) {
            //loop through schedule and check if the activity is in the same place and time
            for (Assignment otherAssignment : schedule.assignments().values()) {
                if (otherAssignment != assignment) {
                    if (assignment.timeslot().equals(otherAssignment.timeslot()) && assignment.location().equals(otherAssignment.location())) {
                        String scoreDescription = assignment.activity().name() + " and " + otherAssignment.activity().name() + " are in the same place and time";
                        return new ScheduleScore(-10.0, Map.of(scoreDescription, -10.0));
                    }
                }
            }
            return new ScheduleScore(0.0, Collections.EMPTY_MAP);
        }
    }    

    public static class RoomCapacityScorer implements Scorer{
        @Override
        public ScheduleScore scoreAssignment(Model model, Schedule schedule, Assignment assignment) {

            Map<String, Double> scoreBreakdown = new HashMap<>();
            double score = 0.0;

            //check for over capacity 6x
            if(assignment.location().capacity() > 6 * assignment.activity().enrollment()){
                scoreBreakdown.put(
                    assignment.location().name() + " for " + assignment.activity().name()+ " has a capacity of " + assignment.location().capacity() + " which is more than 6x the enrollment of " + assignment.activity().enrollment(),
                    -0.5
                );
                score += -0.5;
            //check for over capacity 3x
            }else if(assignment.location().capacity() > 3 * assignment.activity().enrollment()){
                scoreBreakdown.put(
                    assignment.location().name() + " for " + assignment.activity().name()+ " has a capacity of " + assignment.location().capacity() + " which is more than 3x the enrollment of " + assignment.activity().enrollment(),
                    -0.2
                );
                score += -0.2;
            //check that the capacity is at least the enrollment
            }else if(assignment.location().capacity() < assignment.activity().enrollment()){
                scoreBreakdown.put(
                    assignment.location().name() + " for " + assignment.activity().name() + " has a capacity of " + assignment.location().capacity() + " which is less than the enrollment of " + assignment.activity().enrollment(),
                    -0.5
                );
                score += -0.5;
            }else{
                scoreBreakdown.put(
                    assignment.location().name() + " for " + assignment.activity().name() + " has a capacity of " + assignment.location().capacity() + " which is at least the enrollment of " + assignment.activity().enrollment(),
                    0.3
                );
                score += 0.3;
            }

            return new ScheduleScore(score, scoreBreakdown);
        }
    }

    public static class FacilitatorScorer implements Scorer{
        @Override
        public ScheduleScore scoreAssignment(Model model, Schedule schedule, Assignment assignment) {
            final String assignedFacilitator = assignment.facilitator();
            final Activity activity = assignment.activity();

            double score = 0.0;
            Map<String, Double> scoreBreakdown = new HashMap<>();

            if(Activity.isFacilitatorPreferred(activity, assignedFacilitator)){
                scoreBreakdown.put(
                    assignedFacilitator + " is a preferred facilitator for " + activity.name(),
                    0.5
                );
                score += 0.5;
            }else if(Activity.isFacilitatorSecondary(activity, assignedFacilitator)){
                scoreBreakdown.put(
                    assignedFacilitator + " is a secondary facilitator for " + activity.name(),
                    0.2
                );
                score += 0.2;
            }else{
                scoreBreakdown.put(
                    assignedFacilitator + " is not a preferred or secondary facilitator for " + activity.name(),
                    -0.1
                );
                score -= 0.1;
            }

            return new ScheduleScore(score, scoreBreakdown);
        }
    }

    public static class FacilitatorLoadScorer implements Scorer{
        //Note that for this implementation we multi-count the penalty for each assignment that a facilitator has
        //this is simply a choice of algorithm. though in a way it does make sense, a facilitator with 3 assignments
        //should be penalized more than a facilitator with 1 assignment
        @Override
        public ScheduleScore scoreAssignment(Model model, Schedule schedule, Assignment assignment) {
            double score = 0.0;
            final String assignedFacilitator = assignment.facilitator();

            int facilitatorAssignmentCount = 1;

            Map<String, Double> scoreBreakdown = new HashMap<>();

            //Loop through the other assignments and collect the metrics
            boolean noTimeOverlap = true;
            for (Assignment otherAssignment : schedule.assignments().values()) {
                //skip ourselves
                if (!otherAssignment.equals(assignment)) {
                    //This faciliator has another assignment
                    if (otherAssignment.facilitator().equals(assignedFacilitator)) {
                        facilitatorAssignmentCount++;
                        //check for same time
                        if (Assignment.areTimesEqual(assignment, otherAssignment)) {
                            scoreBreakdown.put(
                                assignedFacilitator + " is assigned " + assignment.activity().name() + " and " + otherAssignment.activity().name() + " at the same time",
                                -10.0
                            );
                            noTimeOverlap = false;
                            score -= 10.0;
                        }else{//Same facilitator, different time
                            //if the activities are consequitive
                            if(Math.abs(assignment.timeslot().getTime() - otherAssignment.timeslot().getTime()) == ONE_HOUR){
                                final Room otherLocation = otherAssignment.location();
                                final Room thisLocation = assignment.location();
                                //Check for Beach or Roman
                                if(
                                    otherLocation.name().startsWith("Roman") ||
                                    otherLocation.name().startsWith("Beach") ||
                                    thisLocation.name().startsWith("Roman") ||
                                    thisLocation.name().startsWith("Beach")
                                ){
                                    //if the rooms are in the same building
                                    if(otherLocation.name().substring(0, 4).equals(thisLocation.name().substring(0, 4))){
                                        scoreBreakdown.put("Facilitator " + assignedFacilitator + " is assigned " + assignment.activity().name() + " and " + otherAssignment.activity().name() + " consecutively in the same building", 0.5);
                                        score += 0.5;
                                    //otherwise one is in beach and the other in roman
                                    }else{
                                        scoreBreakdown.put("Facilitator " + assignedFacilitator + " is assigned " + assignment.activity().name() + " and " + otherAssignment.activity().name() + " consecutively in different buildings (Roman/Beach)", -0.4);
                                        score -= 0.4;
                                    }
                                }else{
                                    scoreBreakdown.put("Facilitator " + assignedFacilitator + " is assigned " + assignment.activity().name() + " and " + otherAssignment.activity().name() + " consecutively", 0.5);
                                    score += 0.5;
                                }
                            }
                        }
                    }
                }
            }

            //facilitator only scheduled for one assignment
            if(noTimeOverlap){
                final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                scoreBreakdown.put("Facilitator " + assignedFacilitator + " is only scheduled for one activity at " +sdf.format(assignment.timeslot()), 0.2);
                score += 0.2;
            }

            //facilitoror has more than 4 assignments
            if(facilitatorAssignmentCount > 4){
                scoreBreakdown.put("Facilitator " + assignedFacilitator + " has more than 4 assignments", -0.5);
                score -= 0.5;
            }else if(facilitatorAssignmentCount < 3){
                if(!"Tyler".equals(assignedFacilitator)){
                    scoreBreakdown.put("Facilitator " + assignedFacilitator + " has less than 3 assignments", -0.2);
                    score -= 0.4;
                }
            }

            return new ScheduleScore(score, scoreBreakdown);
        }
    }

    public static class ActivityTimingScorer implements Scorer{
        @Override
        public ScheduleScore scoreAssignment(Model model, Schedule schedule, Assignment assignment) {

            double retScore = 0.0;

            Map<String, Double> scoreBreakdown = new HashMap<>();

            ScheduleScore sla101Score = roomBasedScoring("SLA101", "SLA191", model, schedule, assignment);

            retScore += sla101Score.score();
            scoreBreakdown.putAll(sla101Score.scoreBreakdown());

            ScheduleScore sla191Score = roomBasedScoring("SLA191", "SLA101", model, schedule, assignment);

            retScore += sla191Score.score();
            scoreBreakdown.putAll(sla191Score.scoreBreakdown());

            return new ScheduleScore(retScore, scoreBreakdown);
        }

        private ScheduleScore roomBasedScoring(String activityPrefix, String otherActivityPrefix, Model model, Schedule schedule, Assignment assignment) {
            final String activityName = assignment.activity().name();

            Map<String, Double> scoreBreakdown = new HashMap<>();

            double retScore = 0.0;
            //Check SLA101 activities
            if(activityName.startsWith(activityPrefix)){
                //go through the model and find the other SLA101 section
                for(Activity otherActivity : model.activities().values()){
                    //A/B sections check
                    if(otherActivity.name().startsWith(activityPrefix) && !otherActivity.name().equals(activityName)){
                        Date otherTime = schedule.assignments().get(otherActivity).timeslot();
                        Date thisTime = assignment.timeslot();

                        //if the activities are 4 or more hours apart
                        if(Math.abs(otherTime.getTime() - thisTime.getTime()) >= FOUR_HOURS){
                            scoreBreakdown.put("Activities " + activityName + " and " + otherActivity.name() + " are 4 or more hours apart", 0.5);
                            retScore += 0.5;
                        }

                        if(otherTime.equals(thisTime)){
                            scoreBreakdown.put("Activities " + activityName + " and " + otherActivity.name() + " are at the same time", -0.5);
                            retScore -= 0.5;
                        }
                    }
                    
                    //SLA191 check
                    if(otherActivity.name().startsWith(otherActivityPrefix)){
                        Assignment otherAssignment = schedule.assignments().get(otherActivity);
                        Date otherTime = otherAssignment.timeslot();
                        Date thisTime = assignment.timeslot();

                        //if the activities are consecutive, 1 hour apart
                        if(Math.abs(otherTime.getTime() - thisTime.getTime()) == ONE_HOUR){
                            final Room otherLocation = otherAssignment.location();
                            final Room thisLocation = assignment.location();
                            //Check for Beach or Roman
                            if(
                                otherLocation.name().startsWith("Roman") ||
                                otherLocation.name().startsWith("Beach") ||
                                thisLocation.name().startsWith("Roman") ||
                                thisLocation.name().startsWith("Beach")
                            ){
                                //if the rooms are in the same building
                                if(otherLocation.name().substring(0, 4).equals(thisLocation.name().substring(0, 4))){
                                    scoreBreakdown.put("Activities " + activityName + " and " + otherActivity.name() + " are consecutive", 0.5);
                                    retScore += 0.5;
                                //otherwise one is in beach and the other in roman
                                }else{
                                    scoreBreakdown.put("Activities " + activityName + " and " + otherActivity.name() + " are consecutive and are in different buildings (Roman/Beach)", -0.4);
                                    retScore -= 0.4;
                                }
                            }else{
                                scoreBreakdown.put("Activities " + activityName + " and " + otherActivity.name() + " are consecutive", 0.5);
                                retScore += 0.5;
                            }
                        }else if(Math.abs(otherTime.getTime() - thisTime.getTime()) == TWO_HOURS){
                            scoreBreakdown.put("Activities " + activityName + " and " + otherActivity.name() + " are 2 hours apart", 0.25);
                            retScore += 0.25;
                        }else if(Math.abs(otherTime.getTime() - thisTime.getTime()) == 0){
                            scoreBreakdown.put("Activities " + activityName + " and " + otherActivity.name() + " are at the same time", -0.25);
                            retScore -= 0.25;
                        }
                    }
                }
            }

            return new ScheduleScore(retScore, scoreBreakdown);
        }
    }
}
