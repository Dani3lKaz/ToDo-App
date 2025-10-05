package ToDoApp.dao;

import ToDoApp.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    private Connection connection;

    public TaskDao(Connection connection) {
        this.connection = connection;
    }

    public void addTask(Task task) {
        String sql = "INSERT INTO TASKS (title, description, status, due_date, project_id) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus());
            ps.setDate(4, Date.valueOf(task.getDueDate()));
            ps.setInt(5, task.getProjectId());
            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Task getTaskbyId(int id){
        String sql = "SELECT * FROM TASKS WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new Task(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getInt("project_id"),
                        Task.TaskStatus.valueOf(rs.getString("status").toUpperCase()));
            }else{
                return null;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Task> getAllTasks(){
        String sql = "SELECT * FROM TASKS";
        List<Task> taskList = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                taskList.add(new Task(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getInt("project_id"),
                        Task.TaskStatus.valueOf(rs.getString("status").toUpperCase())));
            }
            return taskList;
        }catch (SQLException e){
            e.printStackTrace();
            return taskList;
        }
    }

    public List<Task> getTaskbyProjectId(int projectId){
        String sql = "SELECT * FROM TASKS WHERE project_id = ?";
        List<Task> taskList = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                taskList.add(new Task(rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getInt("project_id"),
                        Task.TaskStatus.valueOf(rs.getString("status").toUpperCase())));
            }
            return taskList;
        }catch (SQLException e) {
            e.printStackTrace();
            return taskList;
        }
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM TASKS WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task){
        String sql = "UPDATE TASKS SET title = ?, description = ?, due_date = ?, project_id = ?, status = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setDate(3, Date.valueOf(task.getDueDate()));
            ps.setInt(4, task.getProjectId());
            ps.setString(5, task.getStatus());
            ps.setInt(6, task.getId());
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
