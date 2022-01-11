package app;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        List<Timetable> result = Timetable.generateTimetable();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Parallel Elapsed time: %d ms".formatted(timeElapsed));
        System.out.println(result);
    }
}
