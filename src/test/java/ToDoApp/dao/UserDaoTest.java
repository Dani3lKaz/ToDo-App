package ToDoApp.dao;


import ToDoApp.model.User;
import ToDoApp.utils.Email;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest extends BaseDaoTest {

    @Test
    void testAddAndGetUser() throws Exception{
        UserDao udao = new UserDao(connection);
        User u1 = new User("user", new Email("user123@test.com"), "User1234");
        udao.addUser(u1);
        User u2 = udao.getUserByEmail("user123@test.com");
        assertNotNull(u2);
        assertEquals("user", u2.getName());
    }

    @Test
    void testDeleteUser() throws Exception {
        UserDao udao = new UserDao(connection);
        User u1 = new User("user", new Email("user123@test.com"), "User1234");
        udao.addUser(u1);
        u1 = udao.getUserByEmail("user123@test.com");
        assertNotNull(u1);
        udao.deleteUser(u1.getId());
        u1 = udao.getUserByEmail("user123@test.com");
        assertNull(u1);
    }
}