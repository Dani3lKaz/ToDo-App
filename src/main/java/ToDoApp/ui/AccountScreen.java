package ToDoApp.ui;

import ToDoApp.dao.UserDao;
import ToDoApp.model.User;
import ToDoApp.utils.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;

public class AccountScreen {
    private Connection connection;

    public AccountScreen(Connection connection) {
        this.connection = connection;
    }

    public void show(Stage stage) {
        Label title = new Label("To-Do App");
        title.getStyleClass().add("title");

        Button usersBtn = new Button("Users");
        usersBtn.setOnAction(e -> {
            new UserView(connection).show(stage);
        });
        Button projectsBtn = new Button("Projects");
        projectsBtn.setOnAction(e -> {
            new ProjectView(connection).show(stage);
        });
        Button tasksBtn = new Button("Tasks");
        HBox menuButtons = new HBox(50, usersBtn, projectsBtn, tasksBtn);
        menuButtons.getStyleClass().add("menu");
        menuButtons.setAlignment(Pos.CENTER);

        Label setTitle = new Label("Account settings");
        setTitle.getStyleClass().addAll("title", "title-account");

        GridPane formPane = new GridPane();
        formPane.setAlignment(Pos.CENTER);
        formPane.setHgap(10);
        formPane.setVgap(15);

        Label nameLabel = new Label("Name: ");
        nameLabel.getStyleClass().add("login-label");
        TextField nameField = new TextField();
        nameField.setText(SessionManager.getCurrentUser().getName());
        nameField.setDisable(true);

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("login-label");
        emailLabel.setAlignment(Pos.CENTER_RIGHT);
        TextField emailField = new TextField();
        emailField.setText(SessionManager.getCurrentUser().getEmail().toString());
        emailField.setDisable(true);

        Label passLabel = new Label("Password:");
        passLabel.getStyleClass().add("login-label");
        passLabel.setAlignment(Pos.CENTER_RIGHT);
        PasswordField passField = new PasswordField();
        passField.setDisable(true);

        Label cPassLabel = new Label(" Confirm password:");
        cPassLabel.getStyleClass().add("login-label");
        PasswordField cPassField = new PasswordField();
        cPassField.setDisable(true);


        Button editBtn = new Button("Edit");
        Button saveBtn = new Button("Save");
        saveBtn.setDisable(true);

        final boolean editMode[] = {false};
        editBtn.setOnAction(e -> {
            if(!editMode[0]) {
                nameField.setDisable(false);
                emailField.setDisable(false);
                passField.setDisable(false);
                cPassField.setDisable(false);
                saveBtn.setDisable(false);
                editBtn.setText("Cancel");
                editMode[0] = true;
            }else{
                nameField.setDisable(true);
                nameField.setText(SessionManager.getCurrentUser().getName());
                emailField.setDisable(true);
                emailField.setText(SessionManager.getCurrentUser().getEmail().toString());
                passField.setDisable(true);
                passField.clear();
                cPassField.setDisable(true);
                cPassField.clear();
                saveBtn.setDisable(true);
                editBtn.setText("Edit");
                editMode[0] = false;
            }
        });

        saveBtn.setOnAction(e -> {
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
                User user = new User(SessionManager.getCurrentUser().getId(), nameField.getText(),
                            new Email(emailField.getText()), passField.getText());
                new UserDao(connection).updateUser(user);
                SessionManager.setCurrentUser(user);
                nameField.setDisable(true);
                nameField.setText(SessionManager.getCurrentUser().getName());
                emailField.setDisable(true);
                emailField.setText(SessionManager.getCurrentUser().getEmail().toString());
                passField.setDisable(true);
                passField.clear();
                cPassField.setDisable(true);
                cPassField.clear();
                saveBtn.setDisable(true);
                editBtn.setText("Edit");
                editMode[0] = false;
            }catch (InvalidEmailAdressException ee) {
                ErrorDialog.showError("Validation error", "Email must be in format 'user@example.com'");
            }catch (InvalidPasswordException pe) {
                ErrorDialog.showError("Invalid password!", "Password must be at least 8 characters long, contain one uppercase letter and one digit");
            }
        });

        HBox buttons = new HBox(15, editBtn, saveBtn);
        buttons.setAlignment(Pos.CENTER);

        formPane.add(nameLabel, 0, 0);
        formPane.add(nameField, 1, 0);
        formPane.add(emailLabel, 0, 1);
        formPane.add(emailField, 1, 1);
        formPane.add(passLabel, 0, 2);
        formPane.add(passField, 1, 2);
        formPane.add(cPassLabel, 0, 3);
        formPane.add(cPassField, 1, 3);
        formPane.add(buttons, 0, 4, 2, 1);


        VBox mainBox = new VBox(20,  title, menuButtons, setTitle, formPane);
        mainBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(mainBox);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("To-Do App");
        stage.show();
    }
}
