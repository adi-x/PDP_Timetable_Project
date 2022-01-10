package app.Parallelized;

import java.util.ArrayList;
import java.util.List;

class Search implements Runnable {
    Timetable t;
    List<Class> classes;
    Day day;
    int hour;

    public Search(Timetable t, List<Class> classes, Day day, int hour) {
        this.t = t;
        this.classes = classes;
        this.day = day;
        this.hour = hour;
    }

    @Override
    public void run() {
        if (hour > 20) {
            day = day.getNext();
            hour = 8;
        }

        if (day == Day.SATURDAY) {
            if(!t.check())
                this.t = null;
        }

        for (Class c : classes) {
            var classesClone = new ArrayList<>(classes);
            classesClone.remove(c);
            var tableClone = t.copy();
            tableClone.add(day, hour, c);

            Search search = new Search(tableClone, classesClone, day, hour);
            search.run();
            this.t = search.t;
            if (!search.t.check()) {
                search = new Search(tableClone, classesClone, day, hour+2);
                search.run();
                this.t = search.t;
            }
            return;
        }
    }
}
