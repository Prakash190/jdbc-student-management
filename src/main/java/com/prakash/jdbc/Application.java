package com.prakash.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** Entry point for the Student Management System console application. */
public final class Application {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final StudentDao STUDENT_DAO = new StudentDao();

    private Application() {
    }

    public static void main(String[] args) {
        try {
            Database.initializeSchema();
            System.out.println("\nConnected to MySQL. Student Management System is ready.");
            runMenu();
        } catch (SQLException exception) {
            System.err.println("\nCould not connect to the database: " + exception.getMessage());
            System.err.println("Check that MySQL is running and JDBC_URL, DB_USER, and DB_PASSWORD are correct.");
        }
    }

    private static void runMenu() {
        boolean running = true;
        while (running) {
            printMenu();
            switch (readInt("Choose an option: ")) {
                case 1 -> addStudent();
                case 2 -> displayStudents(getAllStudents());
                case 3 -> findStudent();
                case 4 -> searchStudents();
                case 5 -> updateStudent();
                case 6 -> deleteStudent();
                case 0 -> running = false;
                default -> System.out.println("Please choose a number from 0 to 6.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("""

                ----------------------------------
                STUDENT MANAGEMENT SYSTEM
                ----------------------------------
                1. Add student
                2. List all students
                3. Find student by ID
                4. Search student by name
                5. Update student
                6. Delete student
                0. Exit
                """);
    }

    private static void addStudent() {
        Student student = readStudent(0);
        try {
            int id = STUDENT_DAO.add(student);
            System.out.println("Student added successfully with ID " + id + ".");
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private static void findStudent() {
        int id = readPositiveInt("Student ID: ");
        try {
            Optional<Student> student = STUDENT_DAO.findById(id);
            student.ifPresentOrElse(
                    Application::displayStudent,
                    () -> System.out.println("No student found with ID " + id + "."));
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private static void searchStudents() {
        String name = readRequired("Name to search: ");
        try {
            displayStudents(STUDENT_DAO.searchByName(name));
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private static void updateStudent() {
        int id = readPositiveInt("Student ID to update: ");
        try {
            if (STUDENT_DAO.findById(id).isEmpty()) {
                System.out.println("No student found with ID " + id + ".");
                return;
            }
            Student updatedStudent = readStudent(id);
            System.out.println(STUDENT_DAO.update(updatedStudent)
                    ? "Student updated successfully."
                    : "Student could not be updated.");
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private static void deleteStudent() {
        int id = readPositiveInt("Student ID to delete: ");
        try {
            System.out.println(STUDENT_DAO.delete(id)
                    ? "Student deleted successfully."
                    : "No student found with ID " + id + ".");
        } catch (SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private static List<Student> getAllStudents() {
        try {
            return STUDENT_DAO.findAll();
        } catch (SQLException exception) {
            showDatabaseError(exception);
            return List.of();
        }
    }

    private static Student readStudent(int id) {
        String name = readRequired("Name: ");
        String email;
        do {
            email = readRequired("Email: ");
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                System.out.println("Enter a valid email address.");
            }
        } while (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"));
        String course = readRequired("Course: ");
        int age;
        do {
            age = readPositiveInt("Age: ");
            if (age > 120) {
                System.out.println("Age must be 120 or below.");
            }
        } while (age > 120);
        return new Student(id, name, email, course, age);
    }

    private static String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Value must be greater than zero.");
        }
    }

    private static void displayStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.printf("%-5s %-22s %-30s %-18s %-4s%n", "ID", "Name", "Email", "Course", "Age");
        System.out.println("-----------------------------------------------------------------------------------");
        students.forEach(Application::displayStudent);
    }

    private static void displayStudent(Student student) {
        System.out.printf("%-5d %-22s %-30s %-18s %-4d%n",
                student.id(), student.name(), student.email(), student.course(), student.age());
    }

    private static void showDatabaseError(SQLException exception) {
        String message = exception.getMessage();
        if (message != null && message.contains("Duplicate entry")) {
            System.out.println("A student with that email already exists.");
        } else {
            System.out.println("Database error: " + message);
        }
    }
}
