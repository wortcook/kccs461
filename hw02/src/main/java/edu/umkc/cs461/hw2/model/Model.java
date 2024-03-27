package edu.umkc.cs461.hw2.model;

import java.util.Date;
import java.util.Map;

public record Model(
    Map<String,Activity> activities, 
    Map<String,String> facilitators,
    Map<String,Date> timeslots,
    Map<String,Room> locations
) {
}