package com.planner;

import com.planner.CurrentUser;
import com.planner.db.UserDAO;
import com.planner.view.MainView;
import com.planner.view.UserAuthView;
import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create file
        new java.io.File("data").mkdirs();

        // Connect to file
        Connection conn = DriverManager.getConnection("jdbc:sqlite:data/pomodoro.db");

        // Create user table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT UNIQUE NOT NULL, "
                    + "password TEXT NOT NULL)");
        }
        // Create complete_at table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE tasks ADD COLUMN completed_at TEXT");
        } catch (SQLException e) {
            System.out.println("Column 'completed_at' is existed or error: " + e.getMessage());
        }

        // Tick task done
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE tasks ADD COLUMN done INTEGER DEFAULT 0");
        } catch (SQLException e) {

            System.out.println("Table 'done' is existed or error???: " + e.getMessage());
        }
        UserDAO userDAO = new UserDAO(conn);

        UserAuthView auth = new UserAuthView(userDAO, user -> {
            CurrentUser.set(user);
            MainView mainView = new MainView(conn, user);
            mainView.show();
        });


        auth.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
