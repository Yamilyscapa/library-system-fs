import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LibraryService {
    private final FileStorage<Book> bookStorage;
    private final FileStorage<Person> personStorage;
    private final FileStorage<Loan> loanStorage;

    private final List<Book> books = new ArrayList<>();
    private final List<Person> people = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    public LibraryService(Path dataDirectory) {
        this.bookStorage = new FileStorage<>(dataDirectory.resolve("books.csv"), Book::fromRecord);
        this.personStorage = new FileStorage<>(dataDirectory.resolve("people.csv"), Person::fromRecord);
        this.loanStorage = new FileStorage<>(dataDirectory.resolve("loans.csv"), Loan::fromRecord);
    }

    public void load() {
        books.clear();
        books.addAll(bookStorage.loadAll());
        people.clear();
        people.addAll(personStorage.loadAll());
        loans.clear();
        loans.addAll(loanStorage.loadAll());
    }

    public void save() {
        bookStorage.saveAll(books);
        personStorage.saveAll(people);
        loanStorage.saveAll(loans);
    }

    public Book addBook(String title, String author) {
        Book book = new Book(generateId("BOOK"), title, author);
        books.add(book);
        return book;
    }

    public Person addStudent(String name, String email) {
        Person student = new Student(generateId("STU"), name, email);
        people.add(student);
        return student;
    }

    public Person addProfessor(String name, String email) {
        Person professor = new Professor(generateId("PRO"), name, email);
        people.add(professor);
        return professor;
    }

    public Optional<Book> findBook(String id) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                return Optional.of(book);
            }
        }
        return Optional.empty();
    }

    public Optional<Person> findPerson(String id) {
        for (Person person : people) {
            if (person.getId().equals(id)) {
                return Optional.of(person);
            }
        }
        return Optional.empty();
    }

    public List<Book> listBooks() {
        return new ArrayList<>(books);
    }

    public List<Person> listPeople() {
        return new ArrayList<>(people);
    }

    public List<Loan> listActiveLoans() {
        List<Loan> active = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.isActive()) {
                active.add(loan);
            }
        }
        return active;
    }

    public Optional<Loan> loanBook(String bookId, String borrowerId) {
        Optional<Book> bookOpt = findBook(bookId);
        if (bookOpt.isEmpty() || !bookOpt.get().isAvailable()) {
            return Optional.empty();
        }
        Optional<Person> personOpt = findPerson(borrowerId);
        if (personOpt.isEmpty()) {
            return Optional.empty();
        }
        int currentLoans = countActiveLoans(borrowerId);
        if (currentLoans >= personOpt.get().maxActiveLoans()) {
            return Optional.empty();
        }
        Loan loan = new Loan(generateId("LOAN"), bookId, borrowerId, LocalDate.now(), LocalDate.now().plusDays(14));
        loans.add(loan);
        bookOpt.get().setAvailable(false);
        return Optional.of(loan);
    }

    public boolean returnBook(String bookId) {
        for (Loan loan : loans) {
            if (loan.getBookId().equals(bookId) && loan.isActive()) {
                loan.markReturned();
                findBook(bookId).ifPresent(book -> book.setAvailable(true));
                return true;
            }
        }
        return false;
    }

    private int countActiveLoans(String borrowerId) {
        int count = 0;
        for (Loan loan : loans) {
            if (loan.getBorrowerId().equals(borrowerId) && loan.isActive()) {
                count++;
            }
        }
        return count;
    }

    private String generateId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Optional<Person> anyPersonByEmail(String email) {
        for (Person person : people) {
            if (person.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(person);
            }
        }
        return Optional.empty();
    }

    public Collection<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }
}
