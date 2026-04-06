import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StudentManager {

    ArrayList<Student> students = new ArrayList<>();

    public StudentManager() {
        loadFromFile();
    }

    // Add a student
    public void addStudent(Student s) {
        if (findStudent(s.id) != null) {
            System.out.println("Student ID already exists!");
            return;
        }
        students.add(s);
        System.out.println("Student added successfully!");
        saveToFile();
    }

    // View all students
    public void viewStudents() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
        } else {
            for (Student s : students) {
                s.display();
            }
        }
    }

    // Find student by ID
    public Student findStudent(int id) {
        for (Student s : students) {
            if (s.id == id) {
                return s;
            }
        }
        return null;
    }

    // Delete student
    public void deleteStudent(int id) {
        Student s = findStudent(id);
        if (s != null) {
            students.remove(s);
            System.out.println("Student deleted.");
            saveToFile();
        } else {
            System.out.println("Student not found.");
        }
    }

    // Update student
    public void updateStudent(int id, String name, int age, String course, int[] marks) {
        Student s = findStudent(id);
        if (s != null) {
            s.name = name;
            s.age = age;
            s.course = course;
            s.updateMarks(marks);
            System.out.println("Student updated successfully!");
            saveToFile();
        } else {
            System.out.println("Student not found.");
        }
    }

    // Save students to file
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("students.txt"))) {
            for (Student s : students) {
                writer.print(s.id + "," + s.name + "," + s.age + "," + s.course);
                for (int mark : s.marks) {
                    writer.print("," + mark);
                }
                writer.println();
            }
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load students from file
    private void loadFromFile() {
        students.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int age = Integer.parseInt(parts[2]);
                    String course = parts[3];
                    int[] marks = new int[5];
                    for (int i = 0; i < 5; i++) {
                        marks[i] = Integer.parseInt(parts[4 + i]);
                    }
                    students.add(new Student(id, name, age, course, marks));
                }
            }
        } catch (Exception e) {
            // File might not exist or old format, ignore
        }
    }

    // Get all students
    public ArrayList<Student> getStudents() {
        return students;
    }

    // Sort by ID
    public void sortById() {
        Collections.sort(students, Comparator.comparingInt(s -> s.id));
        saveToFile();
    }

    // Sort by Name
    public void sortByName() {
        Collections.sort(students, Comparator.comparing(s -> s.name));
        saveToFile();
    }

    // Filter by course
    public ArrayList<Student> filterByCourse(String course) {
        ArrayList<Student> filtered = new ArrayList<>();
        for (Student s : students) {
            if (s.course.equalsIgnoreCase(course)) {
                filtered.add(s);
            }
        }
        return filtered;
    }
}