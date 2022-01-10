package app.Parallelized;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class Timetable {
    int maxPerDay;
    public int classes = 0;
    ConcurrentHashMap<Day, ConcurrentHashMap<Integer, List<Class>>> Table;

    public Timetable(int maxPerDay) {
        maxPerDay = maxPerDay;
        Table = new ConcurrentHashMap<>();
        for (Day day : Day.values()) {
            if (day != Day.SATURDAY) {
                Table.put(day, new ConcurrentHashMap<>());
                for (int hour = 8; hour <= 20; hour += 2) {
                    Table.get(day).put(hour, new ArrayList<>());
                }
            }
        }
    }

    public void add(Day day, int hour, Class c) {
        classes++;
        Table.get(day).get(hour).add(c);
    }

    public boolean check() {
        return true;
    }

    public Timetable copy() {
        Timetable newTable = new Timetable(maxPerDay);
        newTable.classes = classes;
        for (Day day : Day.values()) {
            if (day != Day.SATURDAY) {
                this.Table.get(day)
                        .forEach((key, value) -> value
                                .forEach(c -> newTable
                                        .Table
                                        .get(day)
                                        .get(key)
                                        .add(new Class(c.Group, c.Subject))));
            }
        }
        return newTable;
    }



    @Override
    public String toString() {
        String s = "";

        final Object[][] table = new String[6][10];

        int j = 1;

        table[0] = new String[10];
        table[0][0] = "X";
        for (Integer hour = 8; hour <= 20; hour += 2) {
            table[0][j] = hour.toString();
            j++;
        }
        int i = 1;
        for (Day day : Day.values()) {
            if (day != Day.SATURDAY) {
                table[i] = new String[]{"-", "-", "-", "-", "-", "-", "-", "-"};
                table[i][0] = day.toString();
                j = 1;
                for (Integer hour = 8; hour <= 20; hour += 2) {
                    for (Class c : Table.get(day).get(hour)) {
                        table[i][j] = c.toString();
                    }
                    j++;
                }
                i++;
            }
        }

        for (Object[] row : table) {
            System.out.format("%15s%15s%15s%15s%15s%15s%15s%15s%n", row);
        }

        return s;
    }
}
