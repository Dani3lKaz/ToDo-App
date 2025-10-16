package ToDoApp.ui;

import ToDoApp.dao.UserDao;
import ToDoApp.model.User;
import ToDoApp.utils.Email;
import ToDoApp.utils.ErrorDialog;
import ToDoApp.utils.InvalidEmailAdressException;
import ToDoApp.utils.InvalidPasswordException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



import java.sql.Connection;

public class UserView {
    private final UserDao dao;
    private final ObservableList<User> userList;

    public UserView(Connection connection) {
        this.dao = new UserDao(connection);
        this.userList = FXCollections.observableArrayList(dao.getAllUsers());
    }

    public void show(Stage stage) {
        TableView<User> table = new TableView<>(userList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail().toString()));

        TableColumn<User, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("Delete");
            {
                delBtn.getStyleClass().add("delete-btn");
                delBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    dao.deleteUser(u.getId());
                    userList.setAll(dao.getAllUsers());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if(empty) {
                    setGraphic(null);
                }else{
                    setGraphic(delBtn);
                }
            }
        });
        idCol.setMinWidth(50);
        idCol.setMaxWidth(50);
        nameCol.setPrefWidth(150);
        emailCol.setPrefWidth(200);
        actionCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, nameCol, emailCol, actionCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            String name = nameField.getText();
            Email email = null;
            String password = passwordField.getText();

            try {
                email = new Email(emailField.getText());
            }catch (InvalidEmailAdressException ee) {
                ErrorDialog.showError("Invalid email adress!", "Email must be in format 'user@example.com'");
            }

            if(name != null && password != null && email != null) {
                try {
                    User u = new User(name, email, password);
                    dao.addUser(u);
                    nameField.clear();
                    emailField.clear();
                    passwordField.clear();
                }catch (InvalidPasswordException pe) {
                    ErrorDialog.showError("Invalid password!", "Password must be at least 8 characters long, contain one uppercase letter and one digit");
                }
                userList.setAll(dao.getAllUsers());
            }
        });
        addBtn.getStyleClass().add("add-btn");

        Button projectsBtn = new Button("Projects");
        projectsBtn.setOnAction(e -> {
            new ProjectView(dao.getConnection()).show(stage);
        });
        Button accountBtn = new Button("Your Account");
        accountBtn.setOnAction(e -> {
            new AccountScreen(dao.getConnection()).show(stage);
        });

        BorderPane root = new BorderPane();
        BorderPane menu = new BorderPane();

        HBox form = new HBox(10, nameField, emailField, passwordField, addBtn);
        form.setPadding(new Insets(15));
        root.setBottom(form);

        HBox menuBtns = new HBox(100, projectsBtn, accountBtn);
        menuBtns.setAlignment(Pos.CENTER);
        menuBtns.setPadding(new Insets(15));
        menu.setBottom(menuBtns);

        VBox tableBox = new VBox(10, table);
        tableBox.setPadding(new Insets(15));
        root.setCenter(table);

        Label title = new Label("To-Do App");
        title.getStyleClass().add("title");
        menu.setCenter(title);

        root.setTop(menu);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Users");
        stage.show();

    }


}
