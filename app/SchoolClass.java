package app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class SchoolClass implements Serializable {
    private String Group, Subject;

    public SchoolClass(String group, String subject) {
        Group = group;
        Subject = subject;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(Group, Subject);
    }
}
