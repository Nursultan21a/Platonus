import java.util.*;
import java.io.*;
abstract class Person {
    private String id, name;
    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getName() { return name; }
    public abstract void showInfo();
}
class Student extends Person {
    private String studentID;
    private Group group;
    public Student(String id, String name, String studentID, Group group) {
        super(id, name);
        this.studentID = studentID;
        this.group = group;
    }
    public void showInfo() {
        System.out.println("Студент: " + getName());
        System.out.println("ID: " + studentID);
        System.out.println("Тобы: " + group.getName());
    }
}
class Teacher extends Person {
    private String department;
    public Teacher(String id, String name, String department) {
        super(id, name);
        this.department = department;
    }
    public void showInfo() {
        System.out.println("Оқытушы: " + getName());
        System.out.println("Кафедра: " + department);
    }
}
class Group {
    private String name;
    private ArrayList<Student> students = new ArrayList<>();
    public Group(String name) { this.name = name; }
    public String getName() { return name; }
    public void addStudent(Student s) { students.add(s); }
}
class Course {
    private String name;
    private int credits;
    public Course(String name, int credits) {
        this.name = name;
        this.credits = credits;
    }
    public String getName() { return name; }
    public int getCredits() { return credits; }
}
class Grade {
    private Course course;
    private int score;
    public Grade(Course course, int score) {
        this.course = course;
        this.score = score;
    }
    public Course getCourse() { return course; }
    public int getScore() { return score; }
}
class GradeException extends Exception {
    public GradeException(String message) {
        super(message);
    }
}
interface Saveable {
    void save(Transcript t) throws IOException;
}
class Transcript {
    private Student student;
    private ArrayList<Grade> grades = new ArrayList<>();
    public Transcript(Student student) {
        this.student = student;
    }
    public void addGrade(Grade g) {
        grades.add(g);
    }
    public ArrayList<Grade> getGrades() {
        return grades;
    }
}
class GPACalculator {
    public static double calculate(ArrayList<Grade> grades) {
        double total = 0;
        int credits = 0;
        for (Grade g : grades) {
            int score = g.getScore();
            double point;
            if (score >= 90) point = 4.0;
            else if (score >= 80) point = 3.0;
            else if (score >= 70) point = 2.0;
            else if (score >= 60) point = 1.0;
            else point = 0.0;
            total += point * g.getCourse().getCredits();
            credits += g.getCourse().getCredits();
        }
        return credits == 0 ? 0 : total / credits;
    }
}
class CsvStorage implements Saveable {
    public void save(Transcript t) throws IOException {
        try (PrintWriter out = new PrintWriter("grades.csv")) {
            out.println("Пән,Баға");
            for (Grade g : t.getGrades()) {
                out.println(g.getCourse().getName() + "," + g.getScore());
            }
        }
        System.out.println("Файл сақталды!");
    }
}
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Group group = new Group("IT-23");
        Student student = new Student("1", "Али", "S001", group);
        group.addStudent(student);
        Teacher teacher = new Teacher("2", "Айгүл", "IT");
        Course course = new Course("Java", 5);
        Transcript transcript = new Transcript(student);
        HashMap<String, Integer> courses = new HashMap<>();
        courses.put(course.getName(), course.getCredits());
        Person[] people = {student, teacher};
        while (true) {
            System.out.println("\n1. Студент");
            System.out.println("2. Оқытушы");
            System.out.println("3. Пән");
            System.out.println("4. Баға енгізу");
            System.out.println("5. GPA");
            System.out.println("6. CSV сақтау");
            System.out.println("0. Шығу");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    people[0].showInfo();
                    break;
                case 2:
                    people[1].showInfo();
                    break;
                case 3:
                    System.out.println("Пән: " + course.getName());
                    System.out.println("Кредит: " + courses.get(course.getName()));
                    break;
                case 4:
                    System.out.print("Бағаны енгіз (0-100): ");
                    int score = sc.nextInt();
                    try {
                        if (score < 0 || score > 100)
                            throw new GradeException("Қате баға!");
                        transcript.addGrade(new Grade(course, score));
                        System.out.println("Баға қосылды!");
                    } catch (GradeException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 5:
                    System.out.println("GPA: " +
                            GPACalculator.calculate(transcript.getGrades()));
                    break;
                case 6:
                    try {
                        new CsvStorage().save(transcript);
                    } catch (IOException e) {
                        System.out.println("Файл қатесі!");
                    }
                    break;
                case 0:
                    System.out.println("Бағдарлама аяқталды.");
                    return;
                default:
                    System.out.println("Қате таңдау!");
            }
        }
    }
}