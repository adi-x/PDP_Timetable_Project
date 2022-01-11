package app;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Response {
    private List<Pair<Day, List<Pair<Integer, List<SchoolClass>>>>> table;
}
