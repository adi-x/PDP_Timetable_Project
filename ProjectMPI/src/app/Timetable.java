package app;

import lombok.Data;
import mpi.Datatype;
import mpi.MPI;
import mpi.Request;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class Timetable implements Serializable {
    int maxPerDay;
    ConcurrentHashMap<Day, ConcurrentHashMap<Integer, List<SchoolClass>>> table;

    public Timetable(List<Pair<Day, List<Pair<Integer, List<SchoolClass>>>>> table) {
        this.table = new ConcurrentHashMap<>();
        if (table.size() > 0) {
            for (var e : table) {
                this.table.put(e.first, new ConcurrentHashMap<>());
                for (var k : e.second) {
                    this.table.get(e.first).put(k.first, k.second);
                }
            }
        }
    }

    public Timetable() {
        this.table = new ConcurrentHashMap<>();
        Arrays
            .stream(Day.values())
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

    public List<Pair<Day, List<Pair<Integer, List<SchoolClass>>>>> toList() {
        return Arrays
                .stream(Day.values())
                .filter(day -> day != Day.SATURDAY)
                .map(day -> {
                    List<Pair<Integer, List<SchoolClass>>> dailyList = new ArrayList<>();
                    for (int hour = 8; hour <= 20; hour += 2) {
                        dailyList.add(new Pair<>(hour, this.table.get(day).get(hour)));
                    }
                    return new Pair<>(day, dailyList);
                })
                .collect(Collectors.toList());
    }

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
        List<SchoolClass> classes = Files.readAllLines(Paths.get("input.txt")).stream().map((line) -> {
            String[] pieces = line.trim().split(";");
            return new SchoolClass(pieces[0], pieces[1]);
        }).collect(Collectors.toList());

        int id = 1;
        int reqIndex = 0;
        List<Object[]> objectsList = new ArrayList<>();
        Request[] requests = new Request[classes.size()];
        for (SchoolClass schoolClass : classes) {
            if (id == MPI.COMM_WORLD.Size())
                id = 1;
            Timetable t = new Timetable();
            t.table.get(Day.MONDAY).get(8).add(schoolClass);
            List<SchoolClass> clone = Utilities.copy(classes);
            clone.remove(schoolClass);

            System.out.println("[to %d] Sending false".formatted(id));
            MPI.COMM_WORLD.Send(new Object[] {false}, 0, 1, MPI.OBJECT, id, 1);
            System.out.println("[to %d] Sending payload".formatted(id));
            MPI.COMM_WORLD.Send(new Object[] {new Payload(t.toList(),clone, Day.MONDAY, 8)}, 0, 1, MPI.OBJECT, id, 0);

            Object[] objects = new Object[1];
            objectsList.add(objects);
            requests[reqIndex++] = MPI.COMM_WORLD.Irecv(objects, 0, 1, MPI.OBJECT, id++, 0);
        }
        System.out.println("Waiting to receive everything");

        Request.Waitall(requests);
        System.out.println("Received everything back");
        for (int i = 1; i < MPI.COMM_WORLD.Size(); i++) {
            MPI.COMM_WORLD.Send(new Object[] {true}, 0, 1, MPI.OBJECT, i, 1);
        }

        return objectsList.stream().map(objects -> new Timetable((List<Pair<Day, List<Pair<Integer, List<SchoolClass>>>>>) objects[0])).collect(Collectors.toList());

    }

}
