package edu.umkc.cs461.hw2.model;

import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModelLoader {
    public static final Set<String> facilitators = new HashSet<>(Arrays.asList("Lock","Glen","Banks","Richards","Shaw","Singer","Uther","Tyler","Numen","Zeldin"));
    public static final Set<String> activities = new HashSet<>(Arrays.asList("SLA100A","SLA100B","SLA191A","SLA191B","SLA201","SLA291","SLA303","SLA304","SLA394","SLA449","SLA451"));
    public static final Set<String> rooms = new HashSet<>(Arrays.asList("Slater 003","Roman 216","Loft 206","Roman 201","Loft 310","Beach 201","Beach 301","Logos 325","Frank 119"));
    public static final Set<String> times = new HashSet<>(Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00", "15:00"));

    public static Model loadModel(){
        //construct the Rooms
        Map<String,Room> rooms = new HashMap<>();
        rooms.put("Slater 003", new Room("Slater 003", 45));
        rooms.put("Roman 216", new Room("Roman 216", 30));
        rooms.put("Loft 206", new Room("Loft 206", 75));
        rooms.put("Roman 201", new Room("Roman 201", 50));
        rooms.put("Loft 310", new Room("Loft 310", 108));
        rooms.put("Beach 201", new Room("Beach 201", 60));
        rooms.put("Beach 301", new Room("Beach 301", 75));
        rooms.put("Logos 325", new Room("Logos 325", 450));
        rooms.put("Frank 119", new Room("Frank 119", 60));

        //construct the Activities
        Map<String,Activity> activities = new HashMap<>();
        activities.put("SLA100A", new Activity("SLA100A", 50, Set.of(("Glen"),"Lock","Banks"), Set.of("Numen","Richards")));
        activities.put("SLA100B", new Activity("SLA100B", 50, Set.of("Glen","Lock","Banks"), Set.of("Numen","Richards")));
        activities.put("SLA191A", new Activity("SLA191A", 50, Set.of("Glen","Lock","Banks"), Set.of("Numen","Richards")));
        activities.put("SLA191B", new Activity("SLA191B", 50, Set.of("Glen","Lock","Banks"), Set.of("Numen","Richards")));
        activities.put("SLA201", new Activity("SLA201",   50, Set.of("Glen","Banks","Zeldin","Shaw"), Set.of("Numen","Richards","Singer")));
        activities.put("SLA291", new Activity("SLA291",   50, Set.of("Lock","Banks","Zeldin","Singer"), Set.of("Numen","Richards","Shaw","Tyler")));
        activities.put("SLA303", new Activity("SLA303",   60, Set.of("Glen","Zeldin","Banks"), Set.of("Numen","Singer","Shaw")));
        activities.put("SLA304", new Activity("SLA304",   25, Set.of("Glen","Banks","Tyler"), Set.of("Numen","Singer","Shaw","Richards","Uther","Zeldin")));
        activities.put("SLA394", new Activity("SLA394",   20, Set.of("Tyler","Singer"), Set.of("Richards","Zeldin")));
        activities.put("SLA449", new Activity("SLA449",   60, Set.of("Tyler","Singer","Shaw"), Set.of("Zeldin","Uther")));
        activities.put("SLA451", new Activity("SLA451",  100, Set.of("Tyler","Singer","Shaw"), Set.of("Zeldin","Uther","Richards","Banks")));

        //construct the Timeslots
        //find the next Monday using java util Calendar to then get a date

        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        
        Map<String,Date> timeslots = new HashMap<>();
        //populate the timeslots for Monday, Wednesday, then Friday
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        // for(int i = 0; i < 3; i++){
            int i = 0;
            for(String time : times){
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, nextMonday.getYear());
                cal.set(Calendar.MONTH, nextMonday.getMonthValue()-1);
                cal.set(Calendar.DAY_OF_MONTH, nextMonday.getDayOfMonth() + (i*2));
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                timeslots.put(sdf.format(cal.getTime()) + " " + time, cal.getTime());
            }
        // }

        //construct the Facilitators
        Map<String,String> facilitators = new HashMap<>();
        for(String facilitator : ModelLoader.facilitators){
            facilitators.put(facilitator, facilitator);
        }

        return new Model(activities, facilitators, timeslots, rooms);
    }
}
