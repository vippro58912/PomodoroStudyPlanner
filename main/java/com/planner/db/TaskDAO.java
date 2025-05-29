package com.planner.db;

import com.planner.model.Task;
import java.sql.*;
import java.util.*;

public class TaskDAO {
    private final Connection conn;

    public TaskDAO(Connection conn) {
        this.conn = conn;
    }
// Add new task
    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, duration, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setInt(2, task.getDurationMinutes());
            stmt.setInt(3, task.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
// Show the task list
    public List<Task> getTasksByUserId(int userId) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getInt("user_id"),
                        rs.getInt("done") == 1
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
// Detele task
    public void deleteTask(int taskId) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    Tick task done
public void setTaskDone(int taskId, boolean done) {
    String sql = "UPDATE tasks SET done = ? WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, done ? 1 : 0);
        stmt.setInt(2, taskId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
