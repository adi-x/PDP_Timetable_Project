package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Utilities {
    static boolean check(Timetable timetable) {
        return timetable != null && timetable.table
                .entrySet()
                .stream()
                .allMatch((Map.Entry<Day, ConcurrentHashMap<Integer, List<SchoolClass>>> pair) ->
                        pair
                                .getValue()
                                .entrySet()
                                .stream()
                                .allMatch((Map.Entry<Integer, List<SchoolClass>> pair2) ->
                                        pair2
                                                .getValue()
                                                .size()
                                        ==
                                        pair2
                                                .getValue()
                                                .stream()
                                                .map(SchoolClass::getGroup)
                                                .distinct()
                                                .count()
                                        &&
                                        pair2
                                                .getValue()
                                                .size()
                                        ==
                                        pair2
                                                .getValue()
                                                .stream()
                                                .map(SchoolClass::getSubject)
                                                .distinct()
                                                .count()
                                )
                );
    }
    static Timetable copy(Timetable timetable) {
        Timetable newTimetable = new Timetable();
        Arrays
                .stream(Day.values())
                .filter(day -> day != Day.SATURDAY)
                .forEach(day -> timetable.table.
                        get(day)
                        .forEach((key, value) ->
                                value
                                        .forEach(c ->
                                                newTimetable.table
                                                        .get(day)
                                                        .get(key)
                                                        .add(new SchoolClass(c.getGroup(), c.getSubject()))
                                        )
                        )
                );
        return newTimetable;
    }

    static List<SchoolClass> copy(List<SchoolClass> c) {
        return new ArrayList<>(c);
    }

    static Day nextDay(Day d) {
        return Day.values()[d.ordinal() + 1];
    }

    static CompletableFuture<Timetable> search_aux(final Timetable t, final List<SchoolClass> classes,final Day day1,final int hour1) {
        return CompletableFuture.supplyAsync(() -> {
            Day day = day1;
            int hour = hour1;
            if (hour > 20) {
                day = nextDay(day);
                hour = 8;
            }
            if (day == Day.SATURDAY || classes.size() == 0) {
                return check(t) ? t : null;
            }
            for (SchoolClass schoolClass : classes) {
                List<SchoolClass> clonedClasses = copy(classes);
                clonedClasses.remove(schoolClass);
                Timetable timetableCloned = copy(t);
                timetableCloned.table.get(day).get(hour).add(schoolClass);

                Timetable res = null;
                try {
                    res = search_aux(timetableCloned, clonedClasses, day, hour).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                if (!check(res) || classes.size() != 0) {
                    try {
                        res = search_aux(timetableCloned, clonedClasses, day, hour + 2).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                return res;
            }
            return null;
        });

    }


}
