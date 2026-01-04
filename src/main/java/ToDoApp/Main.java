package ToDoApp;


import ToDoApp.db.DataBaseConnection;
import ToDoApp.ui.LoginScreen;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.sql.Connection;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Connection connection = DataBaseConnection.getConnection();
        stage.setTitle("To-Do App");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));
        //SessionManager.setCurrentUser(new User(1,"Admin", new Email("admin@mail.com"), "Admin123"));
        //new AccountScreen(connection).show(stage);
        new LoginScreen(connection).show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
