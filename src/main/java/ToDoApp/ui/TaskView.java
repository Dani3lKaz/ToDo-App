package ToDoApp.ui;

import ToDoApp.dao.ProjectDao;
import ToDoApp.dao.TaskDao;
import ToDoApp.dao.UserDao;
import ToDoApp.model.Project;
import ToDoApp.model.Task;
import ToDoApp.model.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.time.LocalDate;

public class TaskView {
    private final TaskDao dao;
    private final ProjectDao projectDao;
    private final UserDao userDao;
    private final ObservableList<Task> taskList;
    private final Project project;

    public TaskView(Connection connection, Project project) {
        this.project = project;
        this.dao = new TaskDao(connection);
        this.projectDao = new ProjectDao(connection);
        this.userDao = new UserDao(connection);
        this.taskList = FXCollections.observableArrayList(dao.getTaskbyProjectId(project.getId()));
    }

    public void show(Stage stage) {
        TableView<Task> table= new TableView<>(taskList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());

        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Task, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Task, Task.TaskStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<>(){
            private final ChoiceBox<Task.TaskStatus> statusBox = new ChoiceBox();
            {
                statusBox.getItems().addAll(Task.TaskStatus.values());

                statusBox.setOnAction(e -> {
                    Task t = getTableView().getItems().get(getIndex());
                    Task.TaskStatus s = statusBox.getValue();
                    t.setStatus(s.name());
                    dao.updateTask(t);
                });
            }


            @Override
            protected void updateItem(Task.TaskStatus status, boolean empty){
                super.updateItem(status, empty);
                if(empty || status == null) {
                    setGraphic(null);
                }else{
                    statusBox.setValue(status);
                    setGraphic(statusBox);
                }
            }
        });

        TableColumn<Task, LocalDate> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getDueDate()));

        TableColumn<Task, String> projectCol = new TableColumn<>("Project");
        projectCol.setCellValueFactory(data -> new SimpleStringProperty(projectDao.getProjectById(data.getValue().getProjectId()).getName()));

        TableColumn<Task, String> userCol = new TableColumn<>("Assigned to");
        userCol.setCellValueFactory(data -> new SimpleStringProperty(userDao.getUserById(data.getValue().getUserId()).getName()));

        TableColumn<Task, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("Delete");
            private final Button editBtn = new Button("Edit");
            private final HBox btnBox = new HBox(10, delBtn, editBtn);
            {
                delBtn.getStyleClass().add("delete-btn");
                delBtn.setOnAction(e -> {
                    Task t = getTableView().getItems().get(getIndex());
                    dao.deleteTask(t.getId());
                    taskList.setAll(dao.getTaskbyProjectId(project.getId()));
                });

                editBtn.setOnAction( e -> {

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
        titleCol.setPrefWidth(100);
        descriptionCol.setPrefWidth(200);
        statusCol.setPrefWidth(100);
        deadlineCol.setPrefWidth(100);
        projectCol.setPrefWidth(100);
        userCol.setPrefWidth(100);
        actionCol.setPrefWidth(100);
        table.getColumns().addAll(idCol, titleCol, descriptionCol, statusCol, deadlineCol, projectCol, userCol, actionCol);

        Button projectsBtn = new Button("Projects");
        projectsBtn.setOnAction(e -> {
            new ProjectView(projectDao.getConnection()).show(stage);
        });
        Button accountBtn = new Button("Your Account");
        accountBtn.setOnAction(e -> {
            new AccountScreen(projectDao.getConnection()).show(stage);
        });

        BorderPane root = new BorderPane();
        BorderPane menu = new BorderPane();

        HBox menuBtns = new HBox(100, projectsBtn, accountBtn);
        menuBtns.setAlignment(Pos.CENTER);
        menuBtns.setPadding(new Insets(15));
        menu.setBottom(menuBtns);

        VBox tableBox = new VBox(10, table);
        tableBox.setPadding(new Insets(15));
        root.setCenter(table);

        TextField titleField =  new TextField();
        titleField.setPromptText("Title");

        TextField descField =  new TextField();
        descField.setPromptText("Description");

        ChoiceBox<User> userBox = new ChoiceBox<>();
        userBox.getItems().addAll(userDao.getAllUsers());
        userBox.setPrefWidth(100);
        userBox.setConverter(new StringConverter<>() {

            @Override
            public String toString(User user) {
                if(user == null) return "";
                return user.getName();
            }

            @Override
            public User fromString(String s) {
                return null;
            }
        });

        DatePicker deadlineField = new DatePicker();
        deadlineField.setPromptText("Deadline");

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            String title = titleField.getText();
            String description = descField.getText();
            LocalDate dueDate = deadlineField.getValue();
            int projectId = project.getId();
            int user_id = userBox.getValue().getId();

            if(!title.isEmpty() &&  !description.isEmpty() && dueDate.isAfter(LocalDate.now())) {
                Task t = new Task(title, description, dueDate, projectId, user_id);
                dao.addTask(t);
                taskList.setAll(dao.getTaskbyProjectId(projectId));
                titleField.clear();
                descField.clear();
            }

        });

        HBox form = new HBox(10, titleField, descField, deadlineField, userBox, addBtn);
        form.setPadding(new Insets(15));
        root.setBottom(form);

        Label title = new Label("To-Do App");
        title.getStyleClass().add("title");
        menu.setCenter(title);

        root.setTop(menu);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Tasks");
        stage.show();

    }
}
