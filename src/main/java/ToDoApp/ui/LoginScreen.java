package ToDoApp.ui;

import ToDoApp.dao.UserDao;
import ToDoApp.model.User;
import ToDoApp.utils.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen {
    private Connection connection;
    public LoginScreen(Connection connection){
        this.connection = connection;
    }

    public void show(Stage stage) {

        Label title = new Label("To-Do App");
        title.getStyleClass().add("title");

        Label loginEmailLabel = new Label("Email:");
        loginEmailLabel.getStyleClass().add("login-label");
        TextField loginEmailField = new TextField();

        Label loginPassLabel = new Label("Password:");
        loginPassLabel.getStyleClass().add("login-label");
        PasswordField loginPassField = new PasswordField();

        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("login-label");
        TextField nameField = new TextField();

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("login-label");
        TextField emailField = new TextField();

        Label passLabel = new Label("Password:");
        passLabel.getStyleClass().add("login-label");
        PasswordField passField = new PasswordField();

        Label cPassLabel = new Label("Confirm password:");
        cPassLabel.getStyleClass().add("login-label");
        PasswordField cPassField = new PasswordField();

        Button loginBtn = new Button("Log in");
        Button signUpBtn = new Button("Sign up");

        HBox buttons = new HBox(20, loginBtn, signUpBtn);
        buttons.setAlignment(Pos.CENTER);

        GridPane logInPane = new GridPane();
        logInPane.setAlignment(Pos.CENTER);
        logInPane.setHgap(10);
        logInPane.setVgap(15);
        logInPane.add(loginEmailLabel, 0, 0);
        logInPane.add(loginEmailField, 1, 0);
        logInPane.add(loginPassLabel, 0, 1);
        logInPane.add(loginPassField, 1, 1);

        GridPane signUpPane = new GridPane();
        signUpPane.setAlignment(Pos.CENTER);
        signUpPane.setHgap(10);
        signUpPane.setVgap(15);
        signUpPane.add(nameLabel, 0, 0);
        signUpPane.add(nameField, 1, 0);
        signUpPane.add(emailLabel, 0, 1);
        signUpPane.add(emailField, 1, 1);
        signUpPane.add(passLabel, 0, 2);
        signUpPane.add(passField, 1, 2);
        signUpPane.add(cPassLabel, 0, 3);
        signUpPane.add(cPassField, 1, 3);

        VBox form = new VBox(20, title, logInPane, signUpPane, buttons);
        signUpPane.setVisible(false);
        signUpPane.setManaged(false);
        form.setAlignment(Pos.CENTER);
        StackPane root = new StackPane(form);

        final boolean isSingUpMode[] = {false};
        loginBtn.setOnAction(e -> {
            if(isSingUpMode[0]){
                isSingUpMode[0] = false;
                signUpPane.setVisible(false);
                signUpPane.setManaged(false);
                logInPane.setVisible(true);
                logInPane.setManaged(true);
                loginBtn.setText("Log in");

            }else {
                if (validateLogin(loginEmailField.getText(), loginPassField.getText())) {
                    User u = new UserDao(connection).getUserByEmail(loginEmailField.getText());
                    SessionManager.setCurrentUser(u);
                    new UserView(connection).show(stage);
                } else {
                    ErrorDialog.showError("Login failed!", "Invalid email or password");
                    loginEmailField.clear();
                    loginPassField.clear();
                }
            }
        });

        signUpBtn.setOnAction(e -> {
            if(!isSingUpMode[0]){
                isSingUpMode[0] = true;
                logInPane.setVisible(false);
                logInPane.setManaged(false);
                signUpPane.setVisible(true);
                signUpPane.setManaged(true);
                loginBtn.setText("Back");
            }else{
                if(nameField.getText().isEmpty()) {
                    ErrorDialog.showError("Validation error", "Name cannot be empty!");
                    return;
                }
                if(emailField.getText().isEmpty()) {
                    ErrorDialog.showError("Validation error", "Email cannot be empty!");
                    return;
                }
                if(!passField.getText().equals(cPassField.getText())) {
                    ErrorDialog.showError("Validation error", "Passwords do not match!");
                    return;
                }
                try{
                    User user = new User(nameField.getText(),
                            new Email(emailField.getText()), passField.getText());
                    new UserDao(connection).addUser(user);
                    SessionManager.setCurrentUser(user);
                    new UserView(connection).show(stage);
                }catch (InvalidEmailAdressException ee) {
                    ErrorDialog.showError("Validation error", "Email must be in format 'user@example.com'");
                }catch (InvalidPasswordException pe) {
                    ErrorDialog.showError("Invalid password!", "Password must be at least 8 characters long, contain one uppercase letter and one digit");
                }
            }
        });

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("To-Do App");
        stage.show();
    }

    private boolean validateLogin(String email, String password) {
        String sql = "SELECT * FROM USERS WHERE email = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    String dbHash = rs.getString("password");
                    return BCrypt.checkpw(password, dbHash);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

