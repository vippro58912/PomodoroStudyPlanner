package com.planner.view;

import com.planner.model.Task;
import com.planner.model.User;
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
    private final VBox root = new VBox(20);
    private final VBox taskList = new VBox(10);
    private final Connection conn;
    private final User user;
    private final TaskDAO taskDAO;
    private Timeline timer;
    private Label timerLabel;
    private int remainingSeconds;
    private int studyMinutes = 25;

    public MainView(Connection conn, User user) {
        this.conn = conn;
        this.user = user;
        this.taskDAO = new TaskDAO(conn);

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("Pomodoro Study Planner");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        Label subtitle = new Label("Personalized study plan");

        // Timer Label
        timerLabel = new Label(formatTime(studyMinutes * 60));
        timerLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold;");

        // + / - Buttons
        Button plusBtn = new Button("+");
        Button minusBtn = new Button("−");

        plusBtn.setOnAction(e -> {
            if (studyMinutes < 120) {
                studyMinutes++;
                updateTimerLabel();
            }
        });

        minusBtn.setOnAction(e -> {
            if (studyMinutes > 1) {
                studyMinutes--;
                updateTimerLabel();
            }
        });

        HBox adjustBox = new HBox(10, minusBtn, timerLabel, plusBtn);
        adjustBox.setAlignment(Pos.CENTER);

        // Timer controls
        Button start = new Button("Start");
        Button stop = new Button("Stop");
        Button reset = new Button("Reset");

        start.setOnAction(e -> startTimer());
        stop.setOnAction(e -> stopTimer());
        reset.setOnAction(e -> resetTimer());

        HBox timerControl = new HBox(10, start, stop, reset);
        timerControl.setAlignment(Pos.CENTER);

        // Task input
        TextField titleField = new TextField();
        titleField.setPromptText("Task name");
        titleField.setMaxWidth(250);

        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");
        durationField.setMaxWidth(250);

        Button addTask = new Button("Add Task");
        addTask.setOnAction(e -> {
            try {
                String taskTitle = titleField.getText().trim();
                int mins = Integer.parseInt(durationField.getText().trim());
                if (taskTitle.isEmpty()) {
                    showAlert("Task name cannot be empty.");
                    return;
                }
                Task task = new Task(0, taskTitle, mins, user.getId());
                taskDAO.addTask(task);
                titleField.clear();
                durationField.clear();
                refreshTaskList();
            } catch (Exception ex) {
                showAlert("Invalid duration. Enter a number.");
            }
        });

        VBox inputBox = new VBox(10, titleField, durationField, addTask);
        inputBox.setAlignment(Pos.CENTER);

        // Task List
        taskList.setAlignment(Pos.CENTER);
        refreshTaskList();

        // AI Study Tips
        Button tipButton = new Button("AI Study tips");
        tipButton.setOnAction(e -> {
            try {
                String tip = OllamaClient.getStudyTip();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, tip);
                alert.setTitle("Gợi ý học tập");
                alert.setHeaderText(null);
                alert.show();
            } catch (Exception ex) {
                showAlert("Lỗi khi gọi AI: " + ex.getMessage());
            }
        });

        // Combine layout
        root.getChildren().addAll(title, subtitle, adjustBox, timerControl, taskList, inputBox, tipButton);
    }

    private void updateTimerLabel() {
        timerLabel.setText(formatTime(studyMinutes * 60));
    }

    private void refreshTaskList() {
        taskList.getChildren().clear();
        List<Task> tasks = taskDAO.getTasksByUserId(user.getId());

        for (Task t : tasks) {
            Label titleLabel = new Label(t.getTitle());
            titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

            Label durationLabel = new Label(t.getDurationMinutes() + " min");
            durationLabel.setStyle("-fx-font-size: 14;");

            Button deleteBtn = new Button("Delete");
            deleteBtn.setOnAction(e -> {
                taskDAO.deleteTask(t.getId());
                refreshTaskList();
            });

            CheckBox doneBox = new CheckBox();
            doneBox.setSelected(t.isDone());
            doneBox.setOnAction(e -> taskDAO.setTaskDone(t.getId(), doneBox.isSelected()));

            HBox content = new HBox(10, doneBox, titleLabel);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox row = new HBox(10, content, spacer, durationLabel, deleteBtn);
            row.setPadding(new Insets(8));
            row.setStyle("-fx-border-color: #ccc; -fx-border-radius: 4; -fx-background-radius: 4;");
            row.setAlignment(Pos.CENTER_LEFT);

            taskList.getChildren().add(row);
        }
    }

    private void startTimer() {
        stopTimer();
        remainingSeconds = studyMinutes * 60;
        timerLabel.setText(formatTime(remainingSeconds));

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                timerLabel.setText(formatTime(remainingSeconds));
            } else {
                stopTimer();
                showAlert("⏰ Time's up! Take a break.");
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
        remainingSeconds = studyMinutes * 60;
        timerLabel.setText(formatTime(remainingSeconds));
    }

    private String formatTime(int sec) {
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    public void show() {
        Stage stage = new Stage();
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Pomodoro Study Planner");
        stage.show();
    }

    public VBox getRoot() {
        return root;
    }
}
