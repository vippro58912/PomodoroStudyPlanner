import com.planner.db.TaskDAO;
import com.planner.model.Task;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskDAOTest {
    private static TaskDAO dao;
    private static final int testUserId = 999;

    @BeforeAll
    public static void setup() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:data/pomodoro.db");
        dao = new TaskDAO(conn);
    }

    @Test
    @Order(1)
    public void testAddAndRetrieveTask() {
        Task testTask = new Task(0, "Unit Test Task", 25, testUserId);
        dao.addTask(testTask);

        List<Task> tasks = dao.getTasksByUserId(testUserId);
        Assertions.assertTrue(tasks.stream().anyMatch(
                t -> t.getTitle().equals("Unit Test Task")
        ));
    }

    @Test
    @Order(2)
    public void testDeleteTask() {
        List<Task> tasks = dao.getTasksByUserId(testUserId);
        if (!tasks.isEmpty()) {
            dao.deleteTask(tasks.get(0).getId());
        }

        List<Task> updated = dao.getTasksByUserId(testUserId);
        Assertions.assertFalse(updated.stream().anyMatch(
                t -> t.getTitle().equals("Unit Test Task")
        ));
    }
}
