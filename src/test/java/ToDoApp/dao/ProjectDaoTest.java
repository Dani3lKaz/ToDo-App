package ToDoApp.dao;

import ToDoApp.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDaoTest extends BaseDaoTest{

    @Test
    void testAddAndGetProject() {
        ProjectDao dao = new ProjectDao(connection);
        Project p = new Project("Project", "Test project");
        dao.addProject(p);
        Project newP = dao.getProjectById(1);
        assertNotNull(newP);
        assertEquals(newP.getName(), p.getName());
    }

    @Test
    void testDeleteProject() {
        ProjectDao dao = new ProjectDao(connection);
        Project p = new Project("Project", "Test project");
        dao.addProject(p);
        Project addedP = dao.getProjectById(1);
        assertNotNull(addedP);
        dao.deleteProject(1);
        Project deletedP = dao.getProjectById(1);
        assertNull(deletedP);
    }

}