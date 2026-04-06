public class Student {

    int id;
    String name;
    int age;
    String course;
    int[] marks = new int[5];
    double percentage;

    public Student(int id, String name, int age, String course, int[] marks) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.marks = marks.clone();
        calculatePercentage();
    }

    private void calculatePercentage() {
        int total = 0;
        for (int mark : marks) {
            total += mark;
        }
        this.percentage = (double) total / 5;
    }

    public void updateMarks(int[] newMarks) {
        this.marks = newMarks.clone();
        calculatePercentage();
    }

    public void display() {
        System.out.println(
            "ID: " + id +
            ", Name: " + name +
            ", Age: " + age +
            ", Course: " + course +
            ", Marks: " + java.util.Arrays.toString(marks) +
            ", Percentage: " + String.format("%.2f", percentage) + "%"
        );
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Age: " + age + ", Course: " + course +
               ", Marks: " + java.util.Arrays.toString(marks) + ", Percentage: " + String.format("%.2f", percentage) + "%";
    }
}