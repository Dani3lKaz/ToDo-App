package ToDoApp.db;

import ToDoApp.dao.UserDao;
import ToDoApp.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConnection {
    private static String url;
    private static String user;
    private static String password;

    static {
        Properties prop = new Properties();
        try (InputStream in = DataBaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if(in != null) {
                prop.load(in);
                url = prop.getProperty("url");
                user = prop.getProperty("user");
                password = prop.getProperty("password");
            }else{
                System.out.println("[!] db.properties not found");
            }

        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        UserDao ud = new UserDao(getConnection());
        User u1 = new User("TestUserA", "12345");

        ud.addUser(u1);

        System.out.println(ud.getUserById(10));
    }
}
