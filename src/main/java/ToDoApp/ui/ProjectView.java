package ToDoApp.ui;

import ToDoApp.dao.ProjectDao;
import ToDoApp.dao.UserDao;
import ToDoApp.model.Project;
import ToDoApp.model.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Connection;

public class ProjectView {
    private final ProjectDao dao;
    private final UserDao userDao;
    private final ObservableList<Project> projectList;

    public ProjectView(Connection connection) {
        this.dao = new ProjectDao(connection);
        this.userDao = new UserDao(connection);
        this.projectList = FXCollections.observableArrayList(dao.getAllProjects());
    }

    public void show(Stage stage) {
        TableView<Project> table = new TableView<>(projectList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Project, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());

        TableColumn<Project, String> nameCol = new TableColumn<>("Project name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Project, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(data -> {
                int userId = data.getValue().getUserId();
                User u = userDao.getUserById(userId);
                return new SimpleStringProperty(u.getName());
        });

        TableColumn<Project, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("Delete");
            {
                delBtn.getStyleClass().add("delete-btn");
                delBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());
                    dao.deleteProject(p.getId());
                    projectList.setAll(dao.getAllProjects());
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
        nameCol.setPrefWidth(200);
        userCol.setPrefWidth(200);
        actionCol.setPrefWidth(100);
        table.getColumns().addAll(idCol, nameCol, userCol, actionCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Project name");
        nameField.setPrefWidth(300);

        ChoiceBox<User> userBox = new ChoiceBox<>();
        userBox.getItems().addAll(userDao.getAllUsers());
        userBox.setPrefWidth(200);
        userBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                if (user == null) return "";
                return user.getName();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("add-btn");
        addBtn.setOnAction(e -> {
            String name = nameField.getText();
            int userId = userBox.getValue().getId();

            if(name != null && name != "") {
                Project p = new Project(name, userId);
                dao.addProject(p);
                nameField.clear();
                projectList.setAll(dao.getAllProjects());
            }
        });

        Button usersBtn = new Button("Users");
        usersBtn.setOnAction(e -> {
            new UserView(dao.getConnection()).show(stage);
        });
        Button accountBtn = new Button("Your Account");

        BorderPane root = new BorderPane();
        BorderPane menu = new BorderPane();

        HBox form = new HBox(10, nameField, userBox, addBtn);
        form.setPadding(new Insets(15));
        root.setBottom(form);

        HBox menuBtns = new HBox(100, usersBtn, accountBtn);
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
        stage.setTitle("Projects");
        stage.show();

    }
}
