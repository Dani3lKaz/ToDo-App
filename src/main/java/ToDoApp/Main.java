package ToDoApp;


import ToDoApp.db.DataBaseConnection;
import ToDoApp.ui.UserView;
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
        new UserView(connection).show(stage);
    }
}
