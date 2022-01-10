package app.Parallelized;

enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

    public Day getNext() {
        return Day.values()[(ordinal() + 1) % values().length];
    }
}
