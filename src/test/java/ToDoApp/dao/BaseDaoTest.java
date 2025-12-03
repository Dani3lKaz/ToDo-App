package ToDoApp.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public abstract class BaseDaoTest {
    protected Connection connection;

    @BeforeEach
    void setupDatabase() throws Exception{
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;MODE=MySQL", "sa", "");
        try(Statement st = connection.createStatement()) {
            st.execute("""
                CREATE TABLE USERS (
                    id int AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50),
                    password VARCHAR(100),
                    email VARCHAR(100)
            );
            """);
        }
        try(Statement st = connection.createStatement()) {
            st.execute("""
                CREATE TABLE PROJECTS (
                    id int AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50),
                    description TEXT,
                    tasks int
            );
            """);
        }
    }

    @AfterEach
    void tearDown() throws Exception{
        connection.close();
    }
}
