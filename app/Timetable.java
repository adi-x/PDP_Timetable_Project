package app;

import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
public class Timetable implements Serializable {
    int maxPerDay;
    ConcurrentHashMap<Day, ConcurrentHashMap<Integer, List<SchoolClass>>> table;

//    public Timetable(List<Pair<Day, List<Pair<Integer, List<SchoolClass>>>>> table) {
//        this.table = new ConcurrentHashMap<>();
//        if (table.size() > 0) {
//            for (var e : table) {
//                this.table.put(e.first, new ConcurrentHashMap<>());
//                for (var k : e.second) {
//                    this.table.get(e.first).put(k.first, k.second);
//                }
//            }
//        }
//    }

    public Timetable() {
        this.table = new ConcurrentHashMap<>();
        Arrays.stream(Day.values())
            .filter(day -> day != Day.SATURDAY)
            .forEach(day -> {
                this.table.put(day, new ConcurrentHashMap<>());
                for (int hour = 8; hour <= 20; hour += 2) {
                    this.table.get(day).put(hour, new ArrayList<>());
                }
            });
    }

    public Timetable(int maxPerDay) {
        this.maxPerDay = maxPerDay;
    }

//    public List<Pair<Day, List<Pair<Integer, List<SchoolClass>>>>> toList() {
//        return Arrays
//                .stream(Day.values())
//                .filter(day -> day != Day.SATURDAY)
//                .map(day -> {
//                    List<Pair<Integer, List<SchoolClass>>> dailyList = new ArrayList<>();
//                    for (int hour = 8; hour <= 20; hour += 2) {
//                        dailyList.add(new Pair<>(hour, this.table.get(day).get(hour)));
//                    }
//                    return new Pair<>(day, dailyList);
//                })
//                .collect(Collectors.toList());
//    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays
                .stream(Day.values())
                .filter(day -> day != Day.SATURDAY)
                .forEach(day -> {
                    stringBuilder.append(day.toString()).append(":\r\n");
                    for (int hour = 8; hour <= 20; hour += 2) {
                        stringBuilder.append("\t").append(hour).append(".00 :\r\n");
                        List<SchoolClass> classes = this.table.getOrDefault(day, new ConcurrentHashMap<>()).getOrDefault(hour, null);
                        if (classes == null)
                            continue;
                        for (SchoolClass c : classes) {
                            stringBuilder.append("\t\t").append(c).append("\r\n");
                        }
                        stringBuilder.append("\r\n");
                    }
                });
        return stringBuilder.toString();
    }

    public static List<Timetable> generateTimetable() throws IOException {
        List<SchoolClass> classes = Files.readAllLines(Paths.get("/home/ionut/workspace/UNI/PDP/ProiectTimetablePDP/PDP_Timetable_Project/ProjectMPI/input.txt")).stream().map((line) -> {
            String[] pieces = line.trim().split(";");
            return new SchoolClass(pieces[0], pieces[1]);
        }).collect(Collectors.toList());

        var timetables = new ArrayList<Timetable>();
        for (SchoolClass schoolClass : classes) {
            Timetable t = new Timetable();
            t.table.get(Day.MONDAY).get(8).add(schoolClass);
            List<SchoolClass> clone = Utilities.copy(classes);
            clone.remove(schoolClass);

            Search search = new Search(t, clone, Day.MONDAY, 8);
            Thread thread = new Thread(search);
            thread.start();

            timetables.add(search.table);
        }

        return timetables;

    }

}
