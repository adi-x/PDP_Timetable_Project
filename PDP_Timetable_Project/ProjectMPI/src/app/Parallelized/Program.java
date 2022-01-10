package app.Parallelized;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

class Program {
    public static int nr_classes;
    public static int maxPerDay = 3;

    static Timetable search() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/home/ionut/workspace/avaloq/SchoolTimetable/src/main/java/input.txt"));
        var classes = bufferedReader.lines().map(line -> {
            var l = line.trim().split(";");
            return new Class(l[1], l[0]);
        }).collect(Collectors.toList());
        nr_classes = classes.size();
        var l = new ArrayList<Thread>();
        var s = new ArrayList<Search>();
        var start = System.currentTimeMillis();
        for (Class c : classes) {
            Timetable t = new Timetable(maxPerDay);
            t.add(Day.MONDAY, 8, c);

            var clone = new ArrayList<>(classes);
            clone.remove(c);

            Search search = new Search(t, clone, Day.MONDAY, 8);
            s.add(search);
            Thread thread = new Thread(search);
            l.add(thread);
        }
        for(var t: l){
            t.start();
        }
        for (var t: l){
            t.join();
        }
        Timetable t= null;
        for(var search: s){
            if( search.t != null){
                t = search.t;
            }
        }
        var end = System.currentTimeMillis();
        var time = end - start;
        System.out.println(time);

        return t;
    }
}
