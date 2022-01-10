package app.Parallelized;

class Class {
    String Group;
    String Subject;

    public Class(String Group, String Subject) {
        this.Group = Group;
        this.Subject = Subject;
    }

    @Override
    public boolean equals(Object obj) {
        Class p = (Class) obj;
        return Group.equals(p.Group) && Subject.equals(p.Subject);
    }

    @Override
    public String toString() {
        return Subject + "/" + Group;
    }
}
