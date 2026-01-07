package ToDoApp.ui;

import ToDoApp.dao.ProjectDao;
import ToDoApp.dao.UserDao;
import ToDoApp.model.User;
import ToDoApp.utils.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserView {
    private static final Logger logger = Logger.getLogger(UserView.class.getName());
    private final UserDao dao;
    private final ProjectDao projectDao;
    private final ObservableList<User> userList;

    public UserView(Connection connection) {
        this.dao = new UserDao(connection);
        this.projectDao = new ProjectDao(connection);
        this.userList = FXCollections.observableArrayList(dao.getAllUsers());
    }

    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Top Menu
        HBox menu = new HBox(20);
        menu.getStyleClass().add("menu-bar");
        menu.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Users");
        title.getStyleClass().add("title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button projectsBtn = new Button("Projects");
        projectsBtn.setOnAction(e -> new ProjectView(dao.getConnection()).show(stage));

        Button accountBtn = new Button("Account");
        accountBtn.setOnAction(e -> new AccountScreen(dao.getConnection()).show(stage));

        menu.getChildren().addAll(title, spacer, projectsBtn, accountBtn);
        root.setTop(menu);

        // Center Table
        TableView<User> table = new TableView<>(userList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail().toString()));

        if(SessionManager.getCurrentUser().getRole() == User.Role.ADMIN) {
            TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
            actionCol.setCellFactory(col -> new TableCell<>() {
                private final Button delBtn = new Button("X");
                private final HBox btnBox = new HBox(10, delBtn);

                {
                    delBtn.getStyleClass().add("delete-btn");
                    delBtn.setOnAction(e -> {
                        User u = getTableView().getItems().get(getIndex());
                        dao.deleteUser(u.getId());
                        logger.log(Level.INFO, "User deleted: userId={}", u.getId());
                        userList.setAll(dao.getAllUsers());
                    });
                    btnBox.setAlignment(Pos.CENTER);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnBox);
                    }
                }
            });
            table.getColumns().addAll(idCol, nameCol, emailCol, actionCol);
        }else{
            table.getColumns().addAll(idCol, nameCol, emailCol);
        }


        // Bottom Add Form
        HBox form = new HBox(15);
        if(SessionManager.getCurrentUser().getRole() == User.Role.ADMIN) {
            form.setPadding(new Insets(20));
            form.setAlignment(Pos.CENTER_LEFT);
            form.getStyleClass().add("card");

            TextField nameField = new TextField();
            nameField.setPromptText("Name");

            TextField emailField = new TextField();
            emailField.setPromptText("Email");

            TextField passwordField = new TextField();
            passwordField.setPromptText("Password");

            Button addBtn = new Button("Add User");
            addBtn.setOnAction(e -> {
                String name = nameField.getText();
                Email email = null;
                String password = passwordField.getText();

                try {
                    email = new Email(emailField.getText());
                } catch (InvalidEmailAdressException ee) {
                    ErrorDialog.showError("Invalid email adress!", "Email must be in format 'user@example.com'");
                    return;
                }

                if (name != null && password != null && email != null) {
                    try {
                        User u = new User(name, email, password);
                        dao.addUser(u);
                        logger.log(Level.INFO, "User added successfully: userId={}",
                                dao.getUserByEmail(u.getEmail().toString()).getId());
                        nameField.clear();
                        emailField.clear();
                        passwordField.clear();
                        userList.setAll(dao.getAllUsers());
                    } catch (InvalidPasswordException pe) {
                        ErrorDialog.showError("Invalid password!",
                                "Password must be at least 8 characters long, contain one uppercase letter and one digit");
                    }
                }
            });
            form.getChildren().addAll(nameField, emailField, passwordField, addBtn);
            HBox.setHgrow(nameField, Priority.ALWAYS);
            HBox.setHgrow(emailField, Priority.ALWAYS);
            HBox.setHgrow(passwordField, Priority.ALWAYS);
        }


        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20));
        centerLayout.getChildren().addAll(table, form);
        root.setCenter(centerLayout);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Users");
        stage.show();
    }
}
