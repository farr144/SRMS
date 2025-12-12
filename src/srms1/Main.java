package srms1;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RecordManager manager = new RecordManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== University System Menu =====");
            System.out.println("1. Add Undergraduate Student");
            System.out.println("2. Add Postgraduate Student");
            System.out.println("3. Remove Student by ID");
            System.out.println("4. Display All Students");
            System.out.println("5. GPA Report");
            System.out.println("6. Ranking Report");
            System.out.println("7. Statistics Report");
            System.out.println("8. Generate ALL Reports to File (Background)");
            System.out.println("9. Toggle Auto-Save (Multithreading)");
            System.out.println("10. Save & Exit");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1: {
                        System.out.println("Enter: ID  Name  Dept  GPA  Level");
                        String uId = scanner.next();
                        String uName = scanner.next();
                        String uDept = scanner.next();
                        double uGpa = scanner.nextDouble();
                        int level = scanner.nextInt();

                        manager.addStudent(new UndergraduateStudent(uId, uName, uDept, uGpa, level));
                        System.out.println(">> Undergraduate Added.");
                        break;
                    }

                    case 2: {
                        System.out.println("Enter: ID  Name  Dept  GPA  Topic");
                        String pId = scanner.next();
                        String pName = scanner.next();
                        String pDept = scanner.next();
                        double pGpa = scanner.nextDouble();
                        String topic = scanner.next();

                        manager.addStudent(new PostgraduateStudent(pId, pName, pDept, pGpa, topic));
                        System.out.println(">> Postgraduate Added.");
                        break;
                    }

                    case 3: {
                        System.out.print("Enter Student ID to remove: ");
                        String rId = scanner.next();
                        if (manager.removeStudentById(rId)) {
                            System.out.println(">> Student removed.");
                        } else {
                            System.out.println(">> Student not found.");
                        }
                        break;
                    }

                    case 4:
                        manager.displayAllStudents();
                        break;

                    case 5:
                        System.out.println(manager.generateGpaReport());
                        break;

                    case 6:
                        System.out.println(manager.generateRankingReport());
                        break;

                    case 7:
                        System.out.println(manager.generateStatisticsReport());
                        break;

                    case 8:
                        System.out.println(manager.generateAllReportsToFileAsync());
                        break;

                    case 9: {
                        if (manager.isAutoSaveRunning()) {
                            System.out.println(manager.stopAutoSave());
                        } else {
                            System.out.print("Auto-save every how many seconds? ");
                            int seconds = scanner.nextInt();
                            System.out.println(manager.startAutoSave(seconds));
                        }
                        break;
                    }

                    case 10:
                        manager.saveToFile();
                        manager.shutdown();
                        System.out.println("Exiting... Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice! Try 1-10.");
                }

            } catch (InputMismatchException e) {
                System.out.println(">> Input Error: Please enter correct data types.");
                scanner.nextLine(); // clear buffer
            } catch (Exception e) {
                System.out.println(">> Error: " + e.getMessage());
            }
        }
    }
}
