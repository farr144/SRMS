package srms1;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RecordManager {
    private final ArrayList<Student> students;
    private final String FILE_NAME = "students_data.txt";

    // --- Multithreading ---
    private ScheduledExecutorService autoSaveScheduler;
    private ScheduledFuture<?> autoSaveTask;
    private final ExecutorService reportExecutor;

    public RecordManager() {
        students = new ArrayList<>();
        loadFromFile();
        reportExecutor = Executors.newSingleThreadExecutor();
    }

    // --- CRUD ---
    public void addStudent(Student s) {
        students.add(s);
    }

    public boolean removeStudentById(String id) {
        return students.removeIf(s -> s.getId().equals(id));
    }

    public Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students in the system.");
            return;
        }
        for (Student s : students) {
            s.displayInfo();
            System.out.println("------------------------");
        }
    }

    // --- Persistence ---
    public void saveToFile() {
        saveToFile(false);
    }

    private void saveToFile(boolean quiet) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                String line = s.getId() + "," + s.getName() + "," + s.getDepartment() + "," + s.getGpa();

                if (s instanceof UndergraduateStudent) {
                    line += ",U," + ((UndergraduateStudent) s).getLevel();
                } else if (s instanceof PostgraduateStudent) {
                    line += ",P," + ((PostgraduateStudent) s).getResearchTopic();
                } else {
                    // fallback
                    line += ",S,";
                }

                writer.write(line);
                writer.newLine();
            }
            if (!quiet) {
                System.out.println(">> Data saved successfully to " + FILE_NAME);
            }
        } catch (IOException e) {
            if (!quiet) {
                System.out.println(">> Error saving file: " + e.getMessage());
            }
        }
    }

    private void loadFromFile() {
        students.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;

                String id = parts[0];
                String name = parts[1];
                String dept = parts[2];
                double gpa = Double.parseDouble(parts[3]);
                String type = parts[4];

                if (type.equals("U")) {
                    if (parts.length < 6) continue;
                    int level = Integer.parseInt(parts[5]);
                    students.add(new UndergraduateStudent(id, name, dept, gpa, level));
                } else if (type.equals("P")) {
                    if (parts.length < 6) continue;
                    String topic = parts[5];
                    students.add(new PostgraduateStudent(id, name, dept, gpa, topic));
                } else {
                    students.add(new Student(id, name, dept, gpa));
                }
            }
        } catch (Exception e) {
            System.out.println(">> Error loading file: " + e.getMessage());
        }
    }

    // --- Reports ---
    private List<Student> sortedByGpaDesc() {
        ArrayList<Student> copy = new ArrayList<>(students);
        copy.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        return copy;
    }

    public String generateGpaReport() {
        if (students.isEmpty()) return "=== GPA Report ===\n(No students)\n";

        StringBuilder sb = new StringBuilder();
        sb.append("=== GPA Report (High -> Low) ===\n");
        for (Student s : sortedByGpaDesc()) {
            sb.append(String.format("%s | %s | %s | GPA: %.2f\n",
                    s.getId(), s.getName(), s.getDepartment(), s.getGpa()));
        }
        return sb.toString();
    }

    public String generateRankingReport() {
        if (students.isEmpty()) return "=== Ranking Report ===\n(No students)\n";

        List<Student> sorted = sortedByGpaDesc();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Ranking Report (by GPA) ===\n");

        int rank = 0;
        double lastGpa = Double.NaN;

        for (int i = 0; i < sorted.size(); i++) {
            Student s = sorted.get(i);
            double g = s.getGpa();

            if (i == 0 || Math.abs(g - lastGpa) > 1e-9) {
                rank = i + 1;
                lastGpa = g;
            }

            sb.append(String.format("#%d  %s | %s | %s | GPA: %.2f\n",
                    rank, s.getId(), s.getName(), s.getDepartment(), g));
        }
        return sb.toString();
    }

    public String generateStatisticsReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Statistics Report ===\n");

        if (students.isEmpty()) {
            sb.append("(No students)\n");
            return sb.toString();
        }

        int n = students.size();
        double sum = 0.0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        Map<String, Integer> deptCounts = new LinkedHashMap<>();
        int ugCount = 0;
        int pgCount = 0;

        for (Student s : students) {
            double g = s.getGpa();
            sum += g;
            min = Math.min(min, g);
            max = Math.max(max, g);

            deptCounts.put(s.getDepartment(), deptCounts.getOrDefault(s.getDepartment(), 0) + 1);

            if (s instanceof UndergraduateStudent) ugCount++;
            else if (s instanceof PostgraduateStudent) pgCount++;
        }

        double avg = sum / n;

        sb.append("Total students: ").append(n).append("\n");
        sb.append(String.format("GPA avg: %.2f | min: %.2f | max: %.2f\n", avg, min, max));
        sb.append("Undergraduate: ").append(ugCount).append(" | Postgraduate: ").append(pgCount).append("\n");

        sb.append("\n-- Students by Department --\n");
        for (Map.Entry<String, Integer> e : deptCounts.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }

        // Optional: UG level distribution
        Map<Integer, Integer> levelCounts = new LinkedHashMap<>();
        for (Student s : students) {
            if (s instanceof UndergraduateStudent) {
                int lvl = ((UndergraduateStudent) s).getLevel();
                levelCounts.put(lvl, levelCounts.getOrDefault(lvl, 0) + 1);
            }
        }
        if (!levelCounts.isEmpty()) {
            sb.append("\n-- Undergraduate Level Distribution --\n");
            List<Integer> keys = new ArrayList<>(levelCounts.keySet());
            Collections.sort(keys);
            for (int lvl : keys) {
                sb.append("Level ").append(lvl).append(": ").append(levelCounts.get(lvl)).append("\n");
            }
        }

        return sb.toString();
    }

    // --- Background report generation ---
    public String generateAllReportsToFileAsync() {
        String ts = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        Path dir = Paths.get("reports");
        Path outFile = dir.resolve("report_" + ts + ".txt");

        reportExecutor.submit(() -> {
            try {
                Files.createDirectories(dir);
                String content = generateGpaReport()
                        + "\n"
                        + generateRankingReport()
                        + "\n"
                        + generateStatisticsReport();

                try (BufferedWriter bw = Files.newBufferedWriter(outFile)) {
                    bw.write(content);
                }
            } catch (Exception e) {
                System.err.println(">> Background report error: " + e.getMessage());
            }
        });

        return ">> Report generation started in background. Output: " + outFile.toString();
    }

    // --- Auto-save thread ---
    public String startAutoSave(int seconds) {
        if (seconds < 1) seconds = 1;

        if (isAutoSaveRunning()) {
            return ">> Auto-save is already running.";
        }

        if (autoSaveScheduler == null || autoSaveScheduler.isShutdown()) {
            autoSaveScheduler = Executors.newSingleThreadScheduledExecutor();
        }

        autoSaveTask = autoSaveScheduler.scheduleAtFixedRate(
                () -> saveToFile(true),
                seconds, seconds,
                TimeUnit.SECONDS
        );

        return ">> Auto-save started (every " + seconds + " seconds).";
    }

    public String stopAutoSave() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel(false);
            autoSaveTask = null;
        }
        if (autoSaveScheduler != null) {
            autoSaveScheduler.shutdownNow();
            autoSaveScheduler = null;
        }
        return ">> Auto-save stopped.";
    }

    public boolean isAutoSaveRunning() {
        return autoSaveTask != null && !autoSaveTask.isCancelled() && !autoSaveTask.isDone();
    }

    // Call this before exit
    public void shutdown() {
        try { stopAutoSave(); } catch (Exception ignored) {}
        reportExecutor.shutdownNow();
    }
}
