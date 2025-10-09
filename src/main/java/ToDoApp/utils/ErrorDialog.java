package ToDoApp.utils;

import javafx.scene.control.Alert;

public class ErrorDialog {
    public static void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning!");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
