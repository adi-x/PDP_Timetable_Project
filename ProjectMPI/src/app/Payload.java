package app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Payload implements Serializable {
    public List<Pair<Day, List<Pair<Integer, List<SchoolClass>>>>> timetable;
    public List<SchoolClass> classes;
    public Day day;
    public int hour;
}
