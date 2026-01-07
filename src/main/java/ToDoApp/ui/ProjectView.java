package ToDoApp.ui;

import ToDoApp.dao.ProjectDao;
import ToDoApp.dao.UserDao;
import ToDoApp.model.Project;
import ToDoApp.model.User;
import ToDoApp.utils.SessionManager;
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

public class ProjectView {
    private static final Logger logger = Logger.getLogger(ProjectView.class.getName());
    private final ProjectDao dao;
    private final ObservableList<Project> projectList;

    public ProjectView(Connection connection) {
        this.dao = new ProjectDao(connection);
        this.projectList = FXCollections.observableArrayList(dao.getAllProjects());
    }

    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Top Menu
        HBox menu = new HBox(20);
        menu.getStyleClass().add("menu-bar");
        menu.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Projects");
        title.getStyleClass().add("title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button usersBtn = new Button("Users");
        usersBtn.setOnAction(e -> new UserView(dao.getConnection()).show(stage));

        Button accountBtn = new Button("Account");
        accountBtn.setOnAction(e -> new AccountScreen(dao.getConnection()).show(stage));

        menu.getChildren().addAll(title, spacer, usersBtn, accountBtn);
        root.setTop(menu);

        // Center Table
        TableView<Project> table = new TableView<>(projectList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Project, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<Project, String> nameCol = new TableColumn<>("Project Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Project, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Project, Integer> tasksCol = new TableColumn<>("Tasks");
        tasksCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTasksCount()).asObject());
        tasksCol.setPrefWidth(50);

        TableColumn<Project, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button tasksBtn = new Button("View Tasks");
            private final Button delBtn = new Button("X");
            private final HBox btnBox = new HBox(10, tasksBtn, delBtn);

            {
                btnBox.setAlignment(Pos.CENTER);
                delBtn.getStyleClass().add("delete-btn");

                delBtn.setOnAction(e -> {
                    Project p = getTableView().getItems().get(getIndex());
                    dao.deleteProject(p.getId());
                    logger.log(Level.INFO, "Project deleted: projectId={0}", p.getId());
                    projectList.setAll(dao.getAllProjects());
                });

                tasksBtn.setOnAction(e -> {
                    new TaskView(dao.getConnection(),
                            dao.getProjectById(getTableView().getItems().get(getIndex()).getId())).show(stage);
                });
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

        table.getColumns().addAll(idCol, nameCol, descCol, tasksCol, actionCol);

        HBox form = new HBox(15);
        if (SessionManager.getCurrentUser().getRole() == User.Role.ADMIN) {
            // Bottom Add Form
            form.setPadding(new Insets(20));
            form.setAlignment(Pos.CENTER_LEFT);
            form.getStyleClass().add("card");

            TextField nameField = new TextField();
            nameField.setPromptText("New Project Name");

            TextField descField = new TextField();
            descField.setPromptText("Description");

            Button addBtn = new Button("Create Project");
            addBtn.setOnAction(e -> {
                String name = nameField.getText();
                String desc = descField.getText();

                if (name != null && !name.trim().isEmpty()) {
                    Project p = new Project(name, desc);
                    dao.addProject(p);
                    logger.log(Level.INFO, "Project added successfully: projectName={0}", name);
                    nameField.clear();
                    descField.clear();
                    projectList.setAll(dao.getAllProjects());
                }
            });

            form.getChildren().addAll(nameField, descField, addBtn);
            HBox.setHgrow(nameField, Priority.ALWAYS);
            HBox.setHgrow(descField, Priority.ALWAYS);
        }


        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20));
        centerLayout.getChildren().addAll(table, form);
        root.setCenter(centerLayout);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Projects");
        stage.show();
    }
}
