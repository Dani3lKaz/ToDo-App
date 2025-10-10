package ToDoApp.dao;

import ToDoApp.model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao {
    private Connection connection;

    public ProjectDao(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void addProject(Project project) {
        String sql = "INSERT INTO PROJECTS (name, user_id) VALUES (?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getName());
            ps.setInt(2, project.getUserId());
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) {
                    project.setId(rs.getInt(1));
                }
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public Project getProjectById(int id) {
        String sql = "SELECT * FROM PROJECTS WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return new Project(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("user_id"));
                }else{
                    return null;
                }
            }
        }catch(SQLException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public List<Project> getAllProjects() {
        String sql = "SELECT * FROM PROJECTS";
        List<Project> projectList = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                projectList.add(new Project(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("user_id")));
            }
            return projectList;
        }catch(SQLException e) {
            e.printStackTrace();
            return  projectList;
        }
    }

    public void updateProject(Project project) {
        String sql = "UPDATE PROJECTS SET name = ?, user_id = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setInt(2, project.getUserId());
            ps.setInt(3, project.getId());
            ps.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProject(int id){
        String sql = "DELETE FROM PROJECTS WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
