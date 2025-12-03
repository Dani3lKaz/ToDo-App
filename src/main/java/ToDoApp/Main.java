package ToDoApp;


import ToDoApp.db.DataBaseConnection;
import ToDoApp.model.User;
import ToDoApp.ui.AccountScreen;
import ToDoApp.ui.LoginScreen;
import ToDoApp.utils.Email;
import ToDoApp.utils.SessionManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.sql.Connection;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Connection connection = DataBaseConnection.getConnection();
        stage.setTitle("To-Do App");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        //SessionManager.setCurrentUser(new User(1,"Admin", new Email("admin@mail.com"), "Admin123"));
        //new AccountScreen(connection).show(stage);
        new LoginScreen(connection).show(stage);
    }
}
