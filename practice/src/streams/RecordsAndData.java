package streams;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RecordsAndData {

    record Course(String name, String category, int durationHours) {
    }

    record Student(String name, List<Course> enrolledCourses) {
    }

    public static List<Course> courses = List.of(
            new Course("Java Basics", "Programming", 40),
            new Course("Spring Boot", "Programming", 50),
            new Course("Database Design", "Database", 35),
            new Course("SQL Advanced", "Database", 45),
            new Course("UI/UX Principles", "Design", 30),
            new Course("Figma for Beginners", "Design", 25)
    );

    public static List<Student> students = List.of(
            new Student("Alice", List.of(courses.get(0), courses.get(2), courses.get(4))),
            new Student("Bob", List.of(courses.get(1), courses.get(3))),
            new Student("Charlie", List.of(courses.get(0), courses.get(1), courses.get(5))),
            new Student("Diana", List.of(courses.get(2), courses.get(3), courses.get(4))),
            new Student("Eve", List.of(courses.get(0), courses.get(5)))
    );

    public static void main(String[] args) {
        // Q1. Get a List<String> of all course names that students are enrolled in (flattened).
        List<String> listOfUniqueCourses = students.stream()
                .flatMap(student -> student.enrolledCourses().stream())
                .map(Course::name)
                .distinct()
                .toList();
        System.out.println("Answer 1: " + listOfUniqueCourses);

        // Q2. Get a Set<String> of all unique course categories enrolled across all students.
        Set<String> uniqueCourseCategories = students.stream()
                .flatMap(student -> student.enrolledCourses().stream()
                        .map(Course::category))
                .collect(Collectors.toSet());
        System.out.println("Answer 2: " + uniqueCourseCategories);

        // Q3. Get a Map<String, Long> where key = course name, value = number of students enrolled in that course.
        Map<String, Long> courseNameAndEnrolledStudents = students.stream()
                .flatMap(student -> student.enrolledCourses()
                        .stream()
                        .map(Course::name))
                .collect(Collectors.groupingBy(
                        Function.identity(), // or student -> student (same thing)
                        Collectors.counting()
                ));
        System.out.println("Answer 3: " + courseNameAndEnrolledStudents);

        // Q4. Get a Map<String, List<String>> where key = student name, value = list of course categories they are enrolled in (flattened).
        Map<String, List<String>> q4 = students.stream()
                .collect(Collectors.toMap(
                        Student::name,
                        RecordsAndData::getList
                ));
        System.out.println("Answer 4: " + q4);

        // Q5. Get a Map<String, Integer> where key = category, value = total hours spent by all students on courses in that category.
        Map<String, Integer> q5 = students.stream()
                .flatMap(student -> student.enrolledCourses().stream())
                .collect(Collectors.groupingBy(
                        Course::category,
                        Collectors.summingInt(Course::durationHours)
                ));
        System.out.println("Answer 5: " + q5);

        // Q6. Get a Map<String, Double> where key = student name, value = average duration of courses they enrolled in.
        Map<String, Double> q6 = students.stream()
                .collect(Collectors.toMap(
                        Student::name,
                        student -> student.enrolledCourses().stream()
                                .collect(Collectors.averagingInt(Course::durationHours)
                                )
                ));
        System.out.println("Answer 6: " + q6);

        //Q7 (challenge). Get a Map<String, Long> where key = category, value = how many students enrolled in at least one course of that category (like the "tags" problem before).
//        Map<String, Long> q7 = students.stream()
//                // For each student, collect their unique categories
//                .map(student -> student.enrolledCourses().stream()
//                        .map(Course::category)
//                        .collect(Collectors.toSet()))
//                // Flatten across all students
//                .flatMap(Set::stream)
//                // Group by category and count how many students had it
//                .collect(Collectors.groupingBy(
//                        category -> category,
//                        Collectors.counting()
//                ));
//
//        System.out.println(q7);
        Map<String, Long> q7 = students.stream()
                .flatMap(student -> student.enrolledCourses().stream()
                        .map(Course::category)
                        .distinct()) // per student distinct
                .collect(Collectors.groupingBy(
                        category -> category,
                        Collectors.counting()
                ));


        System.out.println("Answer 7: " + q7);
    }

    private static List<String> getList(Student student) {
        return student.enrolledCourses().stream()
                .map(Course::category)   // map course → category
                .toList();
    }

}
