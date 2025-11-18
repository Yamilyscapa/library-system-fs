import java.util.Optional;

public class Book implements SerializableEntity {
    private final String id;
    private final String title;
    private final String author;
    private boolean available;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public Book(String id, String title, String author, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toRecord() {
        return String.join("|", id, title, author, Boolean.toString(available));
    }

    public static Optional<Book> fromRecord(String record) {
        if (record == null || record.isBlank()) {
            return Optional.empty();
        }
        String[] parts = record.split("\\|", -1);
        if (parts.length < 4) {
            return Optional.empty();
        }
        String id = parts[0];
        String title = parts[1];
        String author = parts[2];
        boolean available = Boolean.parseBoolean(parts[3]);
        return Optional.of(new Book(id, title, author, available));
    }

    @Override
    public String toString() {
        return title + " - " + author + (available ? " (disponible)" : " (prestado)");
    }
}
