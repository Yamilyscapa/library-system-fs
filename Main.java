import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibraryService service = new LibraryService(Path.of("data"));
        service.load();
        boolean running = true;
        while (running) {
            printMenu();
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> addBook(scanner, service);
                case "2" -> registerPerson(scanner, service);
                case "3" -> listBooks(service);
                case "4" -> listPeople(service);
                case "5" -> registerLoan(scanner, service);
                case "6" -> registerReturn(scanner, service);
                case "7" -> listLoans(service);
                case "8" -> {
                    service.save();
                    System.out.println("Datos guardados. ¡Hasta pronto!");
                    running = false;
                }
                default -> System.out.println("Opción no válida, intente de nuevo.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===== Biblioteca LIS-1022 =====");
        System.out.println("1. Registrar libro");
        System.out.println("2. Registrar persona");
        System.out.println("3. Listar libros");
        System.out.println("4. Listar personas");
        System.out.println("5. Registrar préstamo");
        System.out.println("6. Registrar devolución");
        System.out.println("7. Listar préstamos");
        System.out.println("8. Guardar y salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void addBook(Scanner scanner, LibraryService service) {
        System.out.print("Título del libro: ");
        String title = scanner.nextLine().trim();
        System.out.print("Autor: ");
        String author = scanner.nextLine().trim();
        if (title.isEmpty() || author.isEmpty()) {
            System.out.println("Los datos son obligatorios.");
            return;
        }
        Book book = service.addBook(title, author);
        service.save();
        System.out.println("Libro registrado con ID " + book.getId());
    }

    private static void registerPerson(Scanner scanner, LibraryService service) {
        System.out.print("Tipo (1-Estudiante, 2-Profesor): ");
        String type = scanner.nextLine().trim();
        System.out.print("Nombre: ");
        String name = scanner.nextLine().trim();
        System.out.print("Correo: ");
        String email = scanner.nextLine().trim();
        if (name.isEmpty() || email.isEmpty()) {
            System.out.println("Los datos son obligatorios.");
            return;
        }
        Optional<Person> existing = service.anyPersonByEmail(email);
        if (existing.isPresent()) {
            System.out.println("Ya existe una persona con ese correo: " + existing.get().getName());
            return;
        }
        Person person;
        if ("1".equals(type)) {
            person = service.addStudent(name, email);
        } else if ("2".equals(type)) {
            person = service.addProfessor(name, email);
        } else {
            System.out.println("Tipo inválido.");
            return;
        }
        service.save();
        System.out.println("Persona registrada con ID " + person.getId());
    }

    private static void listBooks(LibraryService service) {
        List<Book> books = service.listBooks();
        if (books.isEmpty()) {
            System.out.println("No hay libros cargados.");
            return;
        }
        System.out.println("\nLibros disponibles:");
        for (Book book : books) {
            System.out.println(book.getId() + " - " + book);
        }
    }

    private static void listPeople(LibraryService service) {
        List<Person> people = service.listPeople();
        if (people.isEmpty()) {
            System.out.println("No hay personas registradas.");
            return;
        }
        System.out.println("\nPersonas registradas:");
        for (Person person : people) {
            System.out.println(person.getId() + " - " + person);
        }
    }

    private static void registerLoan(Scanner scanner, LibraryService service) {
        System.out.print("ID del libro: ");
        String bookId = scanner.nextLine().trim();
        System.out.print("ID de la persona: ");
        String personId = scanner.nextLine().trim();
        Optional<Loan> loan = service.loanBook(bookId, personId);
        if (loan.isPresent()) {
            service.save();
            System.out.println("Préstamo registrado con ID " + loan.get().getId());
        } else {
            System.out.println("No se pudo registrar el préstamo. Verifique disponibilidad, límites y datos.");
        }
    }

    private static void registerReturn(Scanner scanner, LibraryService service) {
        System.out.print("ID del libro a devolver: ");
        String bookId = scanner.nextLine().trim();
        if (service.returnBook(bookId)) {
            service.save();
            System.out.println("Devolución exitosa.");
        } else {
            System.out.println("No hay préstamo activo para ese libro.");
        }
    }

    private static void listLoans(LibraryService service) {
        Collection<Loan> loans = service.getAllLoans();
        if (loans.isEmpty()) {
            System.out.println("No se han registrado préstamos.");
            return;
        }
        System.out.println("\nPréstamos registrados:");
        for (Loan loan : loans) {
            String status = loan.isActive() ? "Activo" : "Finalizado";
            System.out.println(loan.getId() + " - Libro:" + loan.getBookId() + " | Persona:" + loan.getBorrowerId()
                    + " | Vence:" + loan.getDueDate() + " | " + status);
        }
    }
}
