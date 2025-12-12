package srms1;

public class PostgraduateStudent extends Student {

    private String researchTopic;

    public PostgraduateStudent(String id, String name, String department, double gpa, String researchTopic) {
        super(id, name, department, gpa);
        this.researchTopic = researchTopic;
    }

    public String getResearchTopic() {
        return researchTopic;
    }

    public void setResearchTopic(String researchTopic) {
        this.researchTopic = researchTopic;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Research Topic: " + researchTopic);
    }
}