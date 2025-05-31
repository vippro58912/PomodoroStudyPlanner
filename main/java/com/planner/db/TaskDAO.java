package com.planner.db;

import com.planner.model.Task;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public void setTaskDone(int taskId, boolean isDone) {
    try (PreparedStatement stmt = conn.prepareStatement(
            "UPDATE tasks SET done = ?, completed_at = ? WHERE id = ?")) {
        stmt.setInt(1, isDone ? 1 : 0);
        if (isDone) {
            String now = java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            stmt.setString(2, now);
        } else {
            stmt.setNull(2, java.sql.Types.VARCHAR);
        }
        stmt.setInt(3, taskId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Task complete saved
public void markTaskCompleted(int taskId) throws SQLException {
    String sql = "UPDATE tasks SET completed_at = ? WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        String now = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        stmt.setString(1, now);
        stmt.setInt(2, taskId);
        stmt.executeUpdate();
    }
}
// Task statistic
    public Map<String, Integer> getWeeklyCompletionStats(int userId) throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        List<String> days = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        for (String day : days) stats.put(day, 0);

        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        String startDate = monday.toString();
        String endDate = monday.plusDays(6).toString();

        String sql = "SELECT completed_at FROM tasks WHERE user_id = ? AND completed_at BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, startDate + " 00:00:00");
            stmt.setString(3, endDate + " 23:59:59");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String completedAt = rs.getString("completed_at");
                if (completedAt != null) {
                    LocalDate date = LocalDate.parse(completedAt.substring(0, 10));
                    int dayIndex = date.getDayOfWeek().getValue() - 1;
                    String day = days.get(dayIndex);
                    stats.put(day, stats.get(day) + 1);
                }
            }
        }

        return stats;
    }
}

