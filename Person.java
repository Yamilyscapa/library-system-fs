import java.util.Optional;

public abstract class Person implements SerializableEntity, Borrower {
    private final String id;
    private final String name;
    private final String email;

    protected Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public abstract String getType();

    @Override
    public String toRecord() {
        return String.join("|", getType(), id, name, email);
    }

    public static Optional<Person> fromRecord(String record) {
        if (record == null || record.isBlank()) {
            return Optional.empty();
        }
        String[] parts = record.split("\\|", -1);
        if (parts.length < 4) {
            return Optional.empty();
        }
        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        String email = parts[3];
        if ("STUDENT".equalsIgnoreCase(type)) {
            return Optional.of(new Student(id, name, email));
        }
        if ("PROFESSOR".equalsIgnoreCase(type)) {
            return Optional.of(new Professor(id, name, email));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return getType() + " - " + name + " (" + email + ")";
    }
}
