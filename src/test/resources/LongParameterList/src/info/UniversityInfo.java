package info;

public class UniversityInfo {
    private String name;
    private String faculty;
    private String department;

    public UniversityInfo(String name, String faculty, String department) {
        this.name = name;
        this.faculty = faculty;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getDepartment() {
        return department;
    }
}
