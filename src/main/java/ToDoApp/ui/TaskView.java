package ToDoApp.ui;

import ToDoApp.dao.ProjectDao;
import ToDoApp.dao.TaskDao;
import ToDoApp.dao.UserDao;
import ToDoApp.model.Project;
import ToDoApp.model.Task;
import ToDoApp.model.User;
import ToDoApp.utils.EmailSender;
import ToDoApp.utils.ErrorDialog;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Optional;

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
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Top Menu
        HBox menu = new HBox(20);
        menu.getStyleClass().add("menu-bar");
        menu.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(project.getName()); // Show Project Name instead of generic Title
        title.getStyleClass().add("title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button projectsBtn = new Button("Projects");
        projectsBtn.setOnAction(e -> new ProjectView(projectDao.getConnection()).show(stage));

        Button accountBtn = new Button("Account");
        accountBtn.setOnAction(e -> new AccountScreen(projectDao.getConnection()).show(stage));

        menu.getChildren().addAll(title, spacer, projectsBtn, accountBtn);
        root.setTop(menu);

        // Center Table
        TableView<Task> table = new TableView<>(taskList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Task, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Task, Task.TaskStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            private final ChoiceBox<Task.TaskStatus> statusBox = new ChoiceBox<>();
            {
                statusBox.getItems().addAll(Task.TaskStatus.values());
                statusBox.getStyleClass().add("choice-box");
                statusBox.setOnAction(e -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        Task t = getTableView().getItems().get(getIndex());
                        Task.TaskStatus s = statusBox.getValue();
                        if (s != null && t.getStatus() != s) {
                            t.setStatus(s.name());
                            dao.updateTask(t);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Task.TaskStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    statusBox.setValue(status);
                    setGraphic(statusBox);
                }
            }
        });

        TableColumn<Task, LocalDate> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getDueDate()));

        TableColumn<Task, String> userCol = new TableColumn<>("Assigned to");
        userCol.setCellValueFactory(
                data -> new SimpleStringProperty(userDao.getUserById(data.getValue().getUserId()).getName()));

        TableColumn<Task, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("X");
            private final Button editBtn = new Button("Edit");
            private final HBox btnBox = new HBox(10, editBtn, delBtn);

            {
                delBtn.getStyleClass().add("delete-btn");
                delBtn.setOnAction(e -> {
                    Task t = getTableView().getItems().get(getIndex());
                    dao.deleteTask(t.getId());
                    taskList.setAll(dao.getTaskbyProjectId(project.getId()));
                });

                editBtn.setOnAction(e -> {
                    Task t = getTableView().getItems().get(getIndex());
                    showEditDialog(t);
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

        table.getColumns().addAll(idCol, titleCol, descriptionCol, statusCol, deadlineCol, userCol, actionCol);

        // Bottom Add Form
        HBox form = new HBox(15);
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER_LEFT);
        form.getStyleClass().add("card"); // Reuse card style for the form background

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField descField = new TextField();
        descField.setPromptText("Description");

        DatePicker deadlineField = new DatePicker();
        deadlineField.setPromptText("Deadline");

        ChoiceBox<User> userBox = new ChoiceBox<>();
        userBox.getItems().addAll(userDao.getAllUsers());
        userBox.setPrefWidth(150);
        userBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : user.getName();
            }

            @Override
            public User fromString(String s) {
                return null;
            }
        });

        Button addBtn = new Button("Add Task");
        addBtn.setOnAction(e -> {
            String tTitle = titleField.getText();
            String tDesc = descField.getText();
            LocalDate tDate = deadlineField.getValue();
            User tUser = userBox.getValue();

            if (tTitle.isEmpty() || tDesc.isEmpty() || tDate == null || tUser == null) {
                ErrorDialog.showError("Validation Error", "All fields are required.");
                return;
            }

            if (tDate.isBefore(LocalDate.now())) {
                ErrorDialog.showError("Validation Error", "Deadline cannot be in the past.");
                return;
            }

            Task t = new Task(tTitle, tDesc, tDate, project.getId(), tUser.getId());
            dao.addTask(t);
            taskList.setAll(dao.getTaskbyProjectId(project.getId()));
            titleField.clear();
            descField.clear();
            deadlineField.setValue(null);
            userBox.setValue(null);
            EmailSender es = new EmailSender(tUser.getEmail().toString(), tUser.getName(), tDate);
            new Thread(es).start();
        });

        form.getChildren().addAll(titleField, descField, deadlineField, userBox, addBtn);
        HBox.setHgrow(titleField, Priority.ALWAYS);
        HBox.setHgrow(descField, Priority.ALWAYS);

        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20));
        centerLayout.getChildren().addAll(table, form);
        root.setCenter(centerLayout);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Tasks - " + project.getName());
        stage.show();
    }

    private void showEditDialog(Task task) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Edit details for task: " + task.getTitle());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(task.getTitle());
        TextField descField = new TextField(task.getDescription());
        DatePicker datePicker = new DatePicker(task.getDueDate());

        ChoiceBox<User> userBox = new ChoiceBox<>();
        userBox.getItems().addAll(userDao.getAllUsers());
        // Select current user
        for (User u : userBox.getItems()) {
            if (u.getId() == task.getUserId()) {
                userBox.setValue(u);
                break;
            }
        }

        userBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : user.getName();
            }

            @Override
            public User fromString(String s) {
                return null;
            }
        });

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Deadline:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Assignee:"), 0, 3);
        grid.add(userBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Styling the dialog
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("card");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return saveButtonType;
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButtonType) {
            task.setTitle(titleField.getText());
            task.setDescription(descField.getText());
            task.setDueDate(datePicker.getValue());
            if (userBox.getValue() != null) {
                task.setUserId(userBox.getValue().getId());
            }
            dao.updateTask(task);
            taskList.setAll(dao.getTaskbyProjectId(project.getId()));
            EmailSender es = new EmailSender(userBox.getValue().getEmail().toString(), userBox.getValue().getName(), task.getDueDate());
            new Thread(es).start();
        }
    }
}
