package ToDoApp.ui;

import ToDoApp.dao.UserDao;
import ToDoApp.model.User;
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

        TableColumn<User, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPassword()));

        TableColumn<User, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("Delete");
            private final Button editBtn = new Button("Edit");
            {
                delBtn.getStyleClass().add("delete-btn");
                delBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    dao.deleteUser(u.getId());
                    userList.setAll(dao.getAllUsers());
                });
            }
            {
                editBtn.getStyleClass().add("edit-btn");
                editBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());

                    TextInputDialog dialog = new TextInputDialog(u.getName());
                    dialog.setHeaderText("Edit username");
                    dialog.setContentText("New name: ");
                    dialog.showAndWait().ifPresent(newName -> {
                        u.setName(newName);
                        dao.updateUser(u);
                        userList.setAll(dao.getAllUsers());
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if(empty) {
                    setGraphic(null);
                }else{
                    HBox box = new HBox(30, delBtn, editBtn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
        idCol.setMinWidth(50);
        idCol.setMaxWidth(50);
        nameCol.setPrefWidth(150);
        passwordCol.setPrefWidth(200);
        actionCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, nameCol, passwordCol, actionCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            String name = nameField.getText();
            String password = passwordField.getText();

            if(name != null && password != null) {
                User u = new User(name, password);
                dao.addUser(u);
                userList.setAll(dao.getAllUsers());
                nameField.clear();
                passwordField.clear();
            }
        });
        addBtn.getStyleClass().add("add-btn");

        Button projectsBtn = new Button("Projects");
        Button accountBtn = new Button("Your Account");

        BorderPane root = new BorderPane();
        BorderPane menu = new BorderPane();

        HBox form = new HBox(10, nameField, passwordField, addBtn);
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
