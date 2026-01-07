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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginScreen {
    private static final Logger logger = Logger.getLogger(LoginScreen.class.getName());
    private final Connection connection;

    public LoginScreen(Connection connection) {
        this.connection = connection;
    }

    public void show(Stage stage) {

        Label title = new Label("To-Do App");
        title.getStyleClass().add("title");

        // Login Pane
        Label loginEmailLabel = new Label("Email:");
        loginEmailLabel.getStyleClass().add("login-label");
        TextField loginEmailField = new TextField();
        loginEmailField.setPromptText("Enter your email");

        Label loginPassLabel = new Label("Password:");
        loginPassLabel.getStyleClass().add("login-label");
        PasswordField loginPassField = new PasswordField();
        loginPassField.setPromptText("Enter your password");

        GridPane logInContent = new GridPane();
        logInContent.setAlignment(Pos.CENTER);
        logInContent.setHgap(10);
        logInContent.setVgap(15);
        logInContent.add(loginEmailLabel, 0, 0);
        logInContent.add(loginEmailField, 1, 0);
        logInContent.add(loginPassLabel, 0, 1);
        logInContent.add(loginPassField, 1, 1);

        // Sign Up Pane
        Label nameLabel = new Label("Name:");
        nameLabel.getStyleClass().add("login-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("login-label");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        Label passLabel = new Label("Password:");
        passLabel.getStyleClass().add("login-label");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Create a password");

        Label cPassLabel = new Label("Confirm:");
        cPassLabel.getStyleClass().add("login-label");
        PasswordField cPassField = new PasswordField();
        cPassField.setPromptText("Confirm your password");

        GridPane signUpContent = new GridPane();
        signUpContent.setAlignment(Pos.CENTER);
        signUpContent.setHgap(10);
        signUpContent.setVgap(15);
        signUpContent.add(nameLabel, 0, 0);
        signUpContent.add(nameField, 1, 0);
        signUpContent.add(emailLabel, 0, 1);
        signUpContent.add(emailField, 1, 1);
        signUpContent.add(passLabel, 0, 2);
        signUpContent.add(passField, 1, 2);
        signUpContent.add(cPassLabel, 0, 3);
        signUpContent.add(cPassField, 1, 3);

        // Buttons
        Button loginBtn = new Button("Log in");
        Button signUpBtn = new Button("Sign up");
        // Make buttons same width
        loginBtn.setPrefWidth(100);
        signUpBtn.setPrefWidth(100);

        HBox buttons = new HBox(20, loginBtn, signUpBtn);
        buttons.setAlignment(Pos.CENTER);

        // Card Container
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setMaxWidth(400);
        card.setAlignment(Pos.CENTER);

        // Initial State
        card.getChildren().addAll(title, logInContent, buttons);

        final boolean[] isSignUpMode = { false };

        loginBtn.setOnAction(_ -> {
            if (isSignUpMode[0]) {
                // Switch to Login Mode from Sign Up Mode
                isSignUpMode[0] = false;
                card.getChildren().clear();
                card.getChildren().addAll(title, logInContent, buttons);
                loginBtn.setText("Log in");
                signUpBtn.setText("Sign up");
            } else {
                // Perform Login
                if (validateLogin(loginEmailField.getText(), loginPassField.getText())) {
                    User u = new UserDao(connection).getUserByEmail(loginEmailField.getText());
                    logger.log(Level.INFO, "User logged in successfully: userId={0}", u.getId());
                    SessionManager.setCurrentUser(u);
                    new ProjectView(connection).show(stage); // Redirect to Project View initially
                } else {
                    ErrorDialog.showError("Login failed!", "Invalid email or password");
                    logger.log(Level.WARNING, "Failed login attempt: username={0}", loginEmailField.getText());
                    loginEmailField.clear();
                    loginPassField.clear();
                }
            }
        });

        signUpBtn.setOnAction(_ -> {
            if (!isSignUpMode[0]) {
                // Switch to Sign Up Mode
                isSignUpMode[0] = true;

                card.getChildren().clear();
                card.getChildren().addAll(title, signUpContent, buttons);

                loginBtn.setText("Back");
                signUpBtn.setText("Register");
            } else {
                // Perform Registration
                if (nameField.getText().isEmpty()) {
                    ErrorDialog.showError("Validation error", "Name cannot be empty!");
                    return;
                }
                if (emailField.getText().isEmpty()) {
                    ErrorDialog.showError("Validation error", "Email cannot be empty!");
                    return;
                }
                if (!passField.getText().equals(cPassField.getText())) {
                    ErrorDialog.showError("Validation error", "Passwords do not match!");
                    return;
                }
                try {
                    User user = new User(nameField.getText(),
                            new Email(emailField.getText()), passField.getText());
                    new UserDao(connection).addUser(user);
                    logger.log(Level.INFO, "User signed up successfully: userID={0}",
                            new UserDao(connection).getUserByEmail(emailField.getText()).getId());
                    SessionManager.setCurrentUser(user);
                    new ProjectView(connection).show(stage);
                } catch (InvalidEmailAdressException ee) {
                    ErrorDialog.showError("Validation error", "Email must be in format 'user@example.com'");
                } catch (InvalidPasswordException pe) {
                    ErrorDialog.showError("Invalid password!",
                            "Password must be at least 8 characters long, contain one uppercase letter and one digit");
                }
            }
        });

        StackPane root = new StackPane(card);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("To-Do App - Login");
        stage.show();
    }

    private boolean validateLogin(String email, String password) {
        String sql = "SELECT * FROM USERS WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbHash = rs.getString("password");
                    return BCrypt.checkpw(password, dbHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
