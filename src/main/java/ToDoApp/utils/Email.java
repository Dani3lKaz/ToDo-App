package ToDoApp.utils;

public class Email {
    private String value;

    public Email(String value) {
        if(!value.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")){
            throw new IllegalArgumentException("Invalid email adress!");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
