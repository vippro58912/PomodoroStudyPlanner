package com.planner.view;

import com.planner.model.User;
import com.planner.model.Task;
import com.planner.db.TaskDAO;
import com.planner.util.OllamaClient;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.Connection;
import java.util.List;

public class MainView {
    private final VBox root;
    private final VBox taskList;
    private final Connection conn;
    private final User user;
    private final TaskDAO taskDAO;
    private Timeline timer;
    private Label timerLabel;
    private int remainingSeconds = 25 * 60; // 25 phút

    public MainView(Connection conn, User user) {
        this.conn = conn;
        this.user = user;
        this.taskDAO = new TaskDAO(conn);
        this.root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Header
        Label welcome = new Label("👋 Hi " + user.getUsername());
        welcome.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Pomodoro timer
        timerLabel = new Label(formatTime(remainingSeconds));
        timerLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold;");
        Button start = new Button("Start");
        Button stop = new Button("Stop");
        Button reset = new Button("Reset");

        start.setOnAction(e -> startTimer());
        stop.setOnAction(e -> stopTimer());
        reset.setOnAction(e -> resetTimer());

        HBox timerBox = new HBox(10, timerLabel, start, stop, reset);
        timerBox.setAlignment(Pos.CENTER);

        // Task input
        TextField titleField = new TextField();
        titleField.setPromptText("Task title");

        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");

        Button addTask = new Button("Add Task");
        addTask.setOnAction(e -> {
            try {
                String title = titleField.getText();
                int mins = Integer.parseInt(durationField.getText());
                Task task = new Task(0, title, mins, user.getId());
                taskDAO.addTask(task);
                titleField.clear();
                durationField.clear();
                refreshTaskList();
            } catch (Exception ex) {
                showAlert("Invalid input.");
            }
        });

        VBox inputBox = new VBox(8, titleField, durationField, addTask);
        inputBox.setAlignment(Pos.CENTER);

        // Task list
        taskList = new VBox(10);
        taskList.setAlignment(Pos.CENTER_LEFT);
        refreshTaskList();

        // AI Study Tip
        Button tipButton = new Button("AI Study tips");
        tipButton.setOnAction(e -> {
            try {
                String tip = OllamaClient.getStudyTip();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, tip);
                alert.setTitle("Study tips");
                alert.setHeaderText(null);
                alert.show();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("AI error: " + ex.getMessage());
            }
        });


        root.getChildren().addAll(welcome, timerBox, inputBox, taskList, tipButton);
    }

    private void refreshTaskList() {
        taskList.getChildren().clear();
        List<Task> tasks = taskDAO.getTasksByUserId(user.getId());

        for (Task t : tasks) {
            CheckBox checkbox = new CheckBox(t.getTitle() + " – " + t.getDurationMinutes() + " minutes");
            checkbox.setSelected(t.isDone());

            checkbox.setOnAction(e -> {
                taskDAO.setTaskDone(t.getId(), checkbox.isSelected());
            });

            Button deleteBtn = new Button("Delete");
            deleteBtn.setOnAction(e -> {
                taskDAO.deleteTask(t.getId());
                refreshTaskList();
            });

            HBox row = new HBox(10, checkbox, deleteBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            taskList.getChildren().add(row);
        }

    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.show();
    }

    private void startTimer() {
        if (timer != null) timer.stop();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                timerLabel.setText(formatTime(remainingSeconds));
            } else {
                stopTimer();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void stopTimer() {
        if (timer != null) timer.stop();
    }

    private void resetTimer() {
        stopTimer();
        remainingSeconds = 25 * 60;
        timerLabel.setText(formatTime(remainingSeconds));
    }

    private String formatTime(int sec) {
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    public VBox getRoot() {
        return root;
    }

    public void show() {
        Stage stage = new Stage();
        Scene scene = new Scene(getRoot(), 600, 500);
        stage.setScene(scene);
        stage.setTitle("Pomodoro Study Planner");
        stage.show();
    }
}
