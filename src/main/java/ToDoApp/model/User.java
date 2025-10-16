package ToDoApp.model;

import ToDoApp.utils.Email;
import ToDoApp.utils.InvalidPasswordException;

public class User {
    private int id;
    private String name;
    private Email email;
    private String password;

    public User(int id, String name,  Email email, String password) {
        if(!password.startsWith("$2a$")){
            if(!password.matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
                throw new InvalidPasswordException();
            }
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, Email email, String password) {
        if(!password.matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new InvalidPasswordException();
        }
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setHashPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", hashPassword='" + password + '\'' +
                '}';
    }
}
