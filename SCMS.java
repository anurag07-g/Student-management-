import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SCMS {
    public static void main(String[] args) {
        DataStore ds = DataStore.load();
        EnrollmentManager mgr = new EnrollmentManager(ds);
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Add student");
            System.out.println("2) Add course");
            System.out.println("3) List students");
            System.out.println("4) List courses");
            System.out.println("5) Enroll student in course");
            System.out.println("6) Unenroll student from course");
            System.out.println("7) Show student details");
            System.out.println("8) Show course details");
            System.out.println("9) Delete student");
            System.out.println("10) Delete course");
            System.out.println("11) Save datastore");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.print("Name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Email: ");
                    String email = sc.nextLine().trim();
                    Student s = mgr.createStudent(name, email);
                    System.out.println("Created: " + s);
                }
                case "2" -> {
                    System.out.print("Code: ");
                    String code = sc.nextLine().trim();
                    System.out.print("Title: ");
                    String title = sc.nextLine().trim();
                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();
                    Course c = mgr.createCourse(code, title, desc);
                    System.out.println("Created: " + c);
                }
                case "3" -> {
                    System.out.println("Students:");
                    for (Student s : mgr.listStudents()) System.out.println(s);
                }
                case "4" -> {
                    System.out.println("Courses:");
                    for (Course c : mgr.listCourses()) System.out.println(c);
                }
                case "5" -> {
                    System.out.print("Student ID: ");
                    long sid = parseLong(sc.nextLine());
                    System.out.print("Course ID: ");
                    long cid = parseLong(sc.nextLine());
                    boolean ok = mgr.enroll(sid, cid);
                    System.out.println(ok ? "Enrolled" : "Failed to enroll");
                }
                case "6" -> {
                    System.out.print("Student ID: ");
                    long sid = parseLong(sc.nextLine());
                    System.out.print("Course ID: ");
                    long cid = parseLong(sc.nextLine());
                    boolean ok = mgr.unenroll(sid, cid);
                    System.out.println(ok ? "Unenrolled" : "Failed to unenroll");
                }
                case "7" -> {
                    System.out.print("Student ID: ");
                    long sid = parseLong(sc.nextLine());
                    Optional<Student> os = mgr.findStudent(sid);
                    if (os.isEmpty()) { System.out.println("Not found"); break; }
                    Student s = os.get();
                    System.out.println(s);
                    if (s.getCourseIds().isEmpty()) System.out.println("No courses enrolled");
                    else {
                        System.out.println("Enrolled in:");
                        for (Long cid : s.getCourseIds()) {
                            Optional<Course> oc = mgr.findCourse(cid);
                            oc.ifPresent(course -> System.out.println(course));
                        }
                    }
                }
                case "8" -> {
                    System.out.print("Course ID: ");
                    long cid = parseLong(sc.nextLine());
                    Optional<Course> oc = mgr.findCourse(cid);
                    if (oc.isEmpty()) { System.out.println("Not found"); break; }
                    Course c = oc.get();
                    System.out.println(c);
                    if (c.getStudentIds().isEmpty()) System.out.println("No students enrolled");
                    else {
                        System.out.println("Students enrolled:");
                        for (Long sid : c.getStudentIds()) {
                            Optional<Student> os = mgr.findStudent(sid);
                            os.ifPresent(student -> System.out.println(student));
                        }
                    }
                }
                case "9" -> {
                    System.out.print("Student ID: ");
                    long sid = parseLong(sc.nextLine());
                    boolean ok = mgr.deleteStudent(sid);
                    System.out.println(ok ? "Deleted" : "Not found");
                }
                case "10" -> {
                    System.out.print("Course ID: ");
                    long cid = parseLong(sc.nextLine());
                    boolean ok = mgr.deleteCourse(cid);
                    System.out.println(ok ? "Deleted" : "Not found");
                }
                case "11" -> {
                    mgr.save();
                    System.out.println("Saved");
                }
                case "0" -> {
                    mgr.save();
                    running = false;
                }
                default -> System.out.println("Invalid choice");
            }
        }
        sc.close();
        System.out.println("Goodbye");
    }

    private static long parseLong(String s) {
        try { return Long.parseLong(s.trim()); } catch (Exception e) { return -1; }
    }

    static class Student implements Serializable {
        private static final long serialVersionUID = 1L;
        private final long id;
        private String name;
        private String email;
        private final Set<Long> courseIds = new HashSet<>();

        Student(long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        long getId() { return id; }
        String getName() { return name; }
        String getEmail() { return email; }
        Set<Long> getCourseIds() { return courseIds; }
        void setName(String name) { this.name = name; }
        void setEmail(String email) { this.email = email; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Student student = (Student) o;
            return id == student.id;
        }

        @Override
        public int hashCode() { return Objects.hash(id); }

        @Override
        public String toString() {
            return "Student{id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + '}';
        }
    }

    static class Course implements Serializable {
        private static final long serialVersionUID = 1L;
        private final long id;
        private String code;
        private String title;
        private String description;
        private final Set<Long> studentIds = new HashSet<>();

        Course(long id, String code, String title, String description) {
            this.id = id;
            this.code = code;
            this.title = title;
            this.description = description;
        }

        long getId() { return id; }
        String getCode() { return code; }
        String getTitle() { return title; }
        String getDescription() { return description; }
        Set<Long> getStudentIds() { return studentIds; }
        void setCode(String code) { this.code = code; }
        void setTitle(String title) { this.title = title; }
        void setDescription(String description) { this.description = description; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Course course = (Course) o;
            return id == course.id;
        }

        @Override
        public int hashCode() { return Objects.hash(id); }

        @Override
        public String toString() {
            return "Course{id=" + id + ", code='" + code + '\'' + ", title='" + title + '\'' + '}';
        }
    }

    static class DataStore implements Serializable {
        private static final long serialVersionUID = 1L;
        private static final String FILE = "scms.dat";
        private Map<Long, Student> students = new HashMap<>();
        private Map<Long, Course> courses = new HashMap<>();
        private long nextStudentId = 1;
        private long nextCourseId = 1;

        synchronized long nextStudentId() { return nextStudentId++; }
        synchronized long nextCourseId() { return nextCourseId++; }
        synchronized void addStudent(Student s) { students.put(s.getId(), s); }
        synchronized void addCourse(Course c) { courses.put(c.getId(), c); }
        synchronized Optional<Student> getStudent(long id) { return Optional.ofNullable(students.get(id)); }
        synchronized Optional<Course> getCourse(long id) { return Optional.ofNullable(courses.get(id)); }
        synchronized void removeStudent(long id) { students.remove(id); for (Course c : courses.values()) c.getStudentIds().remove(id); }
        synchronized void removeCourse(long id) { courses.remove(id); for (Student s : students.values()) s.getCourseIds().remove(id); }
        synchronized Collection<Student> allStudents() { return students.values(); }
        synchronized Collection<Course> allCourses() { return courses.values(); }

        static DataStore load() {
            File f = new File(FILE);
            if (!f.exists()) return new DataStore();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                Object o = ois.readObject();
                if (o instanceof DataStore) return (DataStore) o;
                return new DataStore();
            } catch (Exception e) {
                return new DataStore();
            }
        }

        synchronized void save() {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
                oos.writeObject(this);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save datastore", e);
            }
        }
    }

    static class EnrollmentManager {
        private final DataStore ds;

        EnrollmentManager(DataStore ds) {
            this.ds = ds;
        }

        Student createStudent(String name, String email) {
            long id = ds.nextStudentId();
            Student s = new Student(id, name, email);
            ds.addStudent(s);
            ds.save();
            return s;
        }

        Course createCourse(String code, String title, String description) {
            long id = ds.nextCourseId();
            Course c = new Course(id, code, title, description);
            ds.addCourse(c);
            ds.save();
            return c;
        }

        boolean deleteStudent(long id) {
            Optional<Student> s = ds.getStudent(id);
            if (s.isEmpty()) return false;
            ds.removeStudent(id);
            ds.save();
            return true;
        }

        boolean deleteCourse(long id) {
            Optional<Course> c = ds.getCourse(id);
            if (c.isEmpty()) return false;
            ds.removeCourse(id);
            ds.save();
            return true;
        }

        List<Student> listStudents() { return ds.allStudents().stream().sorted(Comparator.comparingLong(Student::getId)).collect(Collectors.toList()); }
        List<Course> listCourses() { return ds.allCourses().stream().sorted(Comparator.comparingLong(Course::getId)).collect(Collectors.toList()); }

        Optional<Student> findStudent(long id) { return ds.getStudent(id); }
        Optional<Course> findCourse(long id) { return ds.getCourse(id); }

        boolean enroll(long studentId, long courseId) {
            Optional<Student> s = ds.getStudent(studentId);
            Optional<Course> c = ds.getCourse(courseId);
            if (s.isEmpty() || c.isEmpty()) return false;
            Student student = s.get();
            Course course = c.get();
            if (student.getCourseIds().contains(courseId)) return false;
            student.getCourseIds().add(courseId);
            course.getStudentIds().add(studentId);
            ds.save();
            return true;
        }

        boolean unenroll(long studentId, long courseId) {
            Optional<Student> s = ds.getStudent(studentId);
            Optional<Course> c = ds.getCourse(courseId);
            if (s.isEmpty() || c.isEmpty()) return false;
            Student student = s.get();
            Course course = c.get();
            if (!student.getCourseIds().contains(courseId)) return false;
            student.getCourseIds().remove(courseId);
            course.getStudentIds().remove(studentId);
            ds.save();
            return true;
        }

        void save() { ds.save(); }
    }
}
