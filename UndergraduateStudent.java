package srms1;

public class UndergraduateStudent extends Student {

    private int level;

    public UndergraduateStudent(String id, String name, String department, double gpa, int level) {
        super(id, name, department, gpa);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Level: " + level);
    }
}