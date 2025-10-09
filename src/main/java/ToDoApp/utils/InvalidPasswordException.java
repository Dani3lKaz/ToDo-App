package ToDoApp.utils;

import javafx.scene.control.Alert;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Invalid password! Password must be at least 8 characters long, contain one uppercase letter and one digit");
    }
}
