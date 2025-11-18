public class Professor extends Person {
    public Professor(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getType() {
        return "PROFESSOR";
    }

    @Override
    public int maxActiveLoans() {
        return 5;
    }
}
