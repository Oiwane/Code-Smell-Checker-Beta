import info.UniversityInfo;

public class Main {
    public static final String JAVA = "Java";

    public static void main(String[] args) {
        String name = "Oiwane";
        int age = 22;
        String address = "宮崎県";
        UniversityInfo universityInfo = new UniversityInfo("宮崎", "工", "情報システム工");

        String faculty = universityInfo.getFaculty();
        String favoriteLang = JAVA;

        introduceOneself(name, age, address, universityInfo.getName(), faculty, universityInfo.getDepartment(), favoriteLang);
    }

    public static void introduceOneself(String name, int age, String address, String university, String faculty, String department, String favoriteLang) {
        System.out.println("name : " + name);
        System.out.println("age : " + age);
        System.out.println("address : " + address);
        System.out.println("university : " + university);
        System.out.println("faculty : " + faculty);
        System.out.println("department : " + department);
        System.out.println("favorite Language : " + favoriteLang);
    }
}
