public class Student extends Person {
    public Student(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getType() {
        return "STUDENT";
    }

    @Override
    public int maxActiveLoans() {
        return 3;
    }
}
