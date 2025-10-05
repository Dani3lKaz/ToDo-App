package ToDoApp.dao;

import ToDoApp.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public void addUser(User user) {
        String sql = "INSERT INTO USERS (name, password) VALUES (?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public User getUserById(int id){
        String sql = "SELECT * FROM USERS WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("name"), rs.getString("password"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM USERS";
        List<User> userList = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userList.add(new User(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("password")));
            }
            return userList;
        }catch (SQLException e){
            e.printStackTrace();
            return userList;
        }
    }

    public void updateUser(User user) {
        String sql = "UPDATE USERS SET name = ?, password = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteUser(int id) {
        String sql = "DELETE FROM USERS WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
