package edu.umkc.cs461.hw2.rules;

import edu.umkc.cs461.hw2.model.*;

public interface Scorer {
    default double scoreAssignment( Model model, Schedule schedule, Assignment assignment){
        return 0.0;
    }

    default double scoreSchedule(final Model model, final Schedule schedule){
        double score = 0.0;
        for (Assignment assignment : schedule.assignments().values()) {
            score += scoreAssignment(model, schedule, assignment);
        }
        return score;
    }


    public static class ActivityInSamePlaceAndTimeScorer implements Scorer{
        //Note that this implementation double counts the penalty for each pair of activities that are in the same place and time
        //which is okay for this assignment because we cannot have more than one activity in the same place and time
        @Override
        public double scoreAssignment(Model model, Schedule schedule, Assignment assignment) {
            //loop through schedule and check if the activity is in the same place and time
            for (Assignment otherAssignment : schedule.assignments().values()) {
                if (otherAssignment != assignment) {
                    if (assignment.timeslot().equals(otherAssignment.timeslot()) && assignment.location().equals(otherAssignment.location())) {
                        return -0.5;
                    }
                }
            }
            return 0.0;
        }
    }    

    public static class RoomCapacityScorer implements Scorer{
        @Override
        public double scoreAssignment(Model model, Schedule schedule, Assignment assignment) {
            //check for over capacity 6x
            if(assignment.location().capacity() > 6 * assignment.activity().enrollment()){
                return -0.4;
            //check for over capacity 3x
            }else if(assignment.location().capacity() > 3 * assignment.activity().enrollment()){
                return -0.2;
            //check that the capacity is at least the enrollment
            }else if(assignment.location().capacity() < assignment.activity().enrollment()){
                return -0.5;
            }else{
                return 0.3;
            }
        }
    }

    public static class FacilitatorScorer implements Scorer{
        @Override
        public double scoreAssignment(Model model, Schedule schedule, Assignment assignment) {
            final String assignedFacilitator = assignment.facilitator();
            final Activity activity = assignment.activity();

            if(Activity.isFacilitatorPreferred(activity, assignedFacilitator)){
                return 0.5;
            }else if(Activity.isFacilitatorSecondary(activity, assignedFacilitator)){
                return 0.2;
            }else{
                return -0.1;
            }
        }
    }

    public static class FacilitatorLoadScorer implements Scorer{
        //Note that for this implementation we multi-count the penalty for each assignment that a facilitator has
        //this is simply a choice of algorithm. though in a way it does make sense, a facilitator with 3 assignments
        //should be penalized more than a facilitator with 1 assignment
        @Override
        public double scoreAssignment(Model model, Schedule schedule, Assignment assignment) {
            double score = 0.0;
            final String assignedFacilitator = assignment.facilitator();

            int facilitatorAssignmentCount = 1;

            //Loop through the other assignments and collect the metrics
            boolean noTimeOverlap = true;
            for (Assignment otherAssignment : schedule.assignments().values()) {
                //skip ourselves
                if (otherAssignment != assignment) {
                    //This faciliator has another assignment
                    if (otherAssignment.facilitator().equals(assignedFacilitator)) {
                        facilitatorAssignmentCount++;
                        //check for same time
                        if (Assignment.areTimesEqual(assignment, otherAssignment)) {
                            noTimeOverlap = false;
                            score -= 0.2;
                        }
                    }
                }
            }

            //facilitator only scheduled for one assignment
            if(noTimeOverlap){
                score += 0.2;
            }

            //facilitoror has more than 4 assignments
            if(facilitatorAssignmentCount > 4){
                score -= 0.5;
            }else if(facilitatorAssignmentCount < 3){
                if(!"Tyler".equals(assignedFacilitator)){
                    score -= 0.4;
                }
            }



            return score;
        }
    }
}
