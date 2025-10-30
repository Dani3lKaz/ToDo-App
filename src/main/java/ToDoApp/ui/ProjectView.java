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

        TableColumn<Project, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Project, Integer> tasksCol = new TableColumn<>("Tasks");
        tasksCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTasksCount()).asObject());

        TableColumn<Project, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button tasksBtn = new Button("Tasks");
            private final Button delBtn = new Button("Delete");
            private final HBox btnBox = new HBox(10, tasksBtn, delBtn);
            {
                btnBox.setAlignment(Pos.CENTER);
                delBtn.getStyleClass().add("delete-btn");

                delBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());
                    dao.deleteProject(p.getId());
                    projectList.setAll(dao.getAllProjects());
                });

                tasksBtn.setOnAction(e -> {
                    new TaskView(dao.getConnection(), dao.getProjectById(getTableView().getItems().get(getIndex()).getId())).show(stage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                if(empty) {
                    setGraphic(null);
                }else{
                    setGraphic(btnBox);
                }
            }
        });

        idCol.setMinWidth(50);
        idCol.setMaxWidth(50);
        nameCol.setPrefWidth(200);
        descCol.setPrefWidth(300);
        tasksCol.setMinWidth(50);
        tasksCol.setMaxWidth(50);
        actionCol.setPrefWidth(200);
        table.getColumns().addAll(idCol, nameCol, descCol, tasksCol, actionCol);

        TextField nameField = new TextField();
        nameField.setPromptText("Project name");
        nameField.setPrefWidth(200);

        TextField descField = new TextField();
        descField.setPromptText("Project description");
        descField.setPrefWidth(400);

        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("add-btn");
        addBtn.setOnAction(e -> {
            String name = nameField.getText();
            String desc = descField.getText();

            if(name != null && name != "") {
                Project p = new Project(name, desc);
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

        HBox form = new HBox(10, nameField, descField, addBtn);
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
