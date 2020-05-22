import info.UniversityInfo;

public class Main {
    public static final String JAVA = "Java";

    public static void main(String[] args) {
        String name = "大岩根悠希";
        int age = 22;
        String address = "宮崎県";
        UniversityInfo universityInfo = new UniversityInfo("宮崎", "工", "情報システム工");

        String faculty = universityInfo.getFaculty();
        String favoriteLang = JAVA;

        introduceOneself(name, age, address, universityInfo.getName(), faculty, universityInfo.getDepartment(), favoriteLang);
    }

    public static void introduceOneself(String name, int age, String address, String university, String faculty, String department, String favoriteLang) {
        System.out.println("名前は" + name + "です。");
        System.out.println("年齢は" + age + "歳です。");
        System.out.println(address + "に住んでいます。");
        System.out.println(university + "大学に所属しています。");
        System.out.println(university + "大学では、" + faculty + "学部 " + department + "学科に所属しています。");
        System.out.println("好きな言語は" + favoriteLang + "です。");
    }
}
