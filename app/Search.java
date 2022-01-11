package app;

import java.util.List;

public class Search implements Runnable {
    Timetable table;
    List<SchoolClass> classes;
    Day day;
    int hour;

    public Search(Timetable table, List<SchoolClass> classes, Day day, int hour) {
        this.table = table;
        this.classes = classes;
        this.day = day;
        this.hour = hour;
    }

    @Override
    public void run() {
        Utilities.search_aux(table, classes, day, hour)
                .thenAccept(timetable -> {
                    this.table = timetable;
                });
    }
}
