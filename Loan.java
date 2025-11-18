import java.time.LocalDate;
import java.util.Optional;

public class Loan implements SerializableEntity {
    private final String id;
    private final String bookId;
    private final String borrowerId;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private boolean returned;

    public Loan(String id, String bookId, String borrowerId, LocalDate loanDate, LocalDate dueDate) {
        this.id = id;
        this.bookId = bookId;
        this.borrowerId = borrowerId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = false;
    }

    public Loan(String id, String bookId, String borrowerId, LocalDate loanDate, LocalDate dueDate, boolean returned) {
        this.id = id;
        this.bookId = bookId;
        this.borrowerId = borrowerId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = returned;
    }

    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void markReturned() {
        this.returned = true;
    }

    public boolean isActive() {
        return !returned;
    }

    @Override
    public String toRecord() {
        return String.join("|", id, bookId, borrowerId, loanDate.toString(), dueDate.toString(), Boolean.toString(returned));
    }

    public static Optional<Loan> fromRecord(String record) {
        if (record == null || record.isBlank()) {
            return Optional.empty();
        }
        String[] parts = record.split("\\|", -1);
        if (parts.length < 6) {
            return Optional.empty();
        }
        String id = parts[0];
        String bookId = parts[1];
        String borrowerId = parts[2];
        LocalDate loanDate = LocalDate.parse(parts[3]);
        LocalDate dueDate = LocalDate.parse(parts[4]);
        boolean returned = Boolean.parseBoolean(parts[5]);
        return Optional.of(new Loan(id, bookId, borrowerId, loanDate, dueDate, returned));
    }
}
