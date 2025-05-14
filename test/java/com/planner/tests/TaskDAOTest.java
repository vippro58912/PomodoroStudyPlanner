
package com.planner.tests;

import com.planner.db.TaskDAO;
import com.planner.model.Task;
import org.junit.jupiter.api.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskDAOTest {
    private static TaskDAO dao;

    @BeforeAll
    public static void setup() {
        dao = new TaskDAO();
    }

    @Test
    @Order(1)
    public void testAddAndRetrieveTask() {
        Task testTask = new Task(0, "Unit Test Task", 25);
        dao.addTask(testTask);

        List<Task> tasks = dao.getAllTasks();
        Assertions.assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Unit Test Task")));
    }

    @Test
    @Order(2)
    public void testDeleteTask() {
        List<Task> tasks = dao.getAllTasks();
        Task toDelete = tasks.stream()
                .filter(t -> t.getTitle().equals("Unit Test Task"))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(toDelete);
        dao.deleteTask(toDelete.getId());

        List<Task> afterDelete = dao.getAllTasks();
        Assertions.assertFalse(afterDelete.stream().anyMatch(t -> t.getTitle().equals("Unit Test Task")));
    }
}
