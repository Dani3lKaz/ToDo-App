package ToDoApp.utils;

import javafx.scene.control.Alert;

public class InvalidEmailAdressException extends RuntimeException{
    public InvalidEmailAdressException() {
        super("Invalid email adress!");
    }
}
