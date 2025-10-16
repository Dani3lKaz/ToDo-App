package ToDoApp;


import ToDoApp.db.DataBaseConnection;
import ToDoApp.model.User;
import ToDoApp.ui.AccountScreen;
import ToDoApp.ui.LoginScreen;
import ToDoApp.utils.Email;
import ToDoApp.utils.SessionManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Connection connection = DataBaseConnection.getConnection();
        //SessionManager.setCurrentUser(new User(1,"test", new Email("testemail@gmail.com"), "Password123"));
        //new AccountScreen(connection).show(stage);
        new LoginScreen(connection).show(stage);
    }
}
