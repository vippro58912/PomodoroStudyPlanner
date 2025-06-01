import com.planner.db.TaskDAO;
import com.planner.model.Task;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskDAOTest {
    private static TaskDAO dao;
    private static Connection conn;
    private static final int testUserId = 999;

    @BeforeAll
    public static void setup() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:"); // In-memory DB
        dao = new TaskDAO(conn);

        // Tạo bảng tạm trong DB test
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, duration INTEGER, user_id INTEGER, " +
                "done INTEGER DEFAULT 0, completed_at TEXT)");
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

    @Test
    @Order(3)
    public void testWeeklyCompletionStats() throws Exception {
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

        insertCompletedTask(monday, testUserId);               // Mon
        insertCompletedTask(monday.plusDays(2), testUserId);   // Wed
        insertCompletedTask(monday.plusDays(6), testUserId);   // Sun

        Map<String, Integer> stats = dao.getWeeklyCompletionStats(testUserId);

        Assertions.assertEquals(1, stats.get("Mon"));
        Assertions.assertEquals(1, stats.get("Wed"));
        Assertions.assertEquals(1, stats.get("Sun"));
        Assertions.assertEquals(0, stats.get("Tue"));
    }

    private void insertCompletedTask(LocalDate date, int userId) throws SQLException {
        String dateTime = date + " 09:00:00";
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tasks (title, duration, user_id, done, completed_at) VALUES (?, ?, ?, ?, ?)"
        );
        ps.setString(1, "Stat Test Task");
        ps.setInt(2, 25);
        ps.setInt(3, userId);
        ps.setInt(4, 1);
        ps.setString(5, dateTime);
        ps.executeUpdate();
    }
}
