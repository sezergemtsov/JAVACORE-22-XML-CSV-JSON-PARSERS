public class Employee {
    public String id;
    public String firstName;
    public String lastName;
    public String country;
    public int age;

    public Employee() {
        // Пустой конструктор
    }

    public Employee(String id, String firstName, String lastName, String country, int age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Employee{id="+this.id + ','
                +" firstName="+ this.firstName + ','
                +" lastName="+ this.lastName + ','
                +" country="+ this.country + ','
                +" age="+ this.age+"}";
    }
}
