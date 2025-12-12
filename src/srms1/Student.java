package srms1;

public class Student {
    private String id;
    private String name;
    private String department;
    private double gpa;

    public Student(String id, String name, String department, double gpa) {
        this.id = id;
        this.name = name;
        this.department = department;
        try {
            setGpa(gpa);
        } catch (InvalidGPAException e) {
            System.out.println("Error creating student " + name + ": " + e.getMessage());
            this.gpa = 0.0; 
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getGpa() { return gpa; }
    
    public void setGpa(double gpa) throws InvalidGPAException {
        if (gpa < 0 || gpa > 4.0) {
            throw new InvalidGPAException("GPA must be between 0.0 and 4.0");
        }
        this.gpa = gpa;
    }

    public void displayInfo() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Department: " + department);
        System.out.println("GPA: " + gpa);
    }

}
