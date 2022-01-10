package app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class SchoolClass implements Serializable {
    private String Group, Subject;

    @Override
    public String toString() {
        return "%s: %s".formatted(Group, Subject);
    }
}
