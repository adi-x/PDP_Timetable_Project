package app;

import mpi.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        MPI.Init(args);
        if (MPI.COMM_WORLD.Rank() == 0) {
            long start = System.currentTimeMillis();

            List<Timetable> result = Timetable.generateTimetable();
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println("Parallel Elapsed time: %d ms".formatted(timeElapsed));
            System.out.println(result);
        } else {
            boolean finished = false;
            Object[] buffer = new Object[1];
            while (!finished) {
                MPI.COMM_WORLD.Recv(buffer, 0, 1, MPI.OBJECT, 0, 1);
                finished = (boolean) buffer[0];
                System.out.println(("[from %d] received " + finished).formatted(MPI.COMM_WORLD.Rank()));
                if (!finished) {
                    MPI.COMM_WORLD.Recv(buffer, 0, 1, MPI.OBJECT, 0, 0);
                    Payload payload = (Payload) buffer[0];
                    System.out.println(("[from %d] received " + payload).formatted(MPI.COMM_WORLD.Rank()));
                    Utilities.search_aux(new Timetable(payload.timetable), payload.classes, payload.day, payload.hour).thenAccept(timetable -> {
                        MPI.COMM_WORLD.Send(new Object[] {timetable != null ? timetable.toList() : new ArrayList<Pair<Day, List<Pair<Integer, List<SchoolClass>>>>>()}, 0, 1, MPI.OBJECT, 0, 0);
                        System.out.println("Sent timetable from " + MPI.COMM_WORLD.Rank());
                    });

                }
            }
        }

        MPI.Finalize();
    }
}
