package com.planner.view;

import com.planner.db.TaskDAO;
import com.planner.model.Task;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.List;

public class MainView {
    private final VBox root;
    private final TaskDAO taskDAO;
    private final VBox taskList;
    private Timeline timer;
    private int remainingSeconds = 1500; // 25 minutes
    private Label timerLabel;

    public MainView() {
        taskDAO = new TaskDAO();
        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Title
        Text title = new Text("Pomodoro Study Planner");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Text subtitle = new Text("Personalized study plan");
        subtitle.setStyle("-fx-font-size: 14;");

        // Timer
        timerLabel = new Label(formatTime(remainingSeconds));
        timerLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold;");

        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button resetButton = new Button("Reset");

        startButton.setOnAction(e -> startTimer());
        stopButton.setOnAction(e -> stopTimer());
        resetButton.setOnAction(e -> resetTimer());

        HBox timerBox = new HBox(10, timerLabel, startButton, stopButton, resetButton);
        timerBox.setAlignment(Pos.CENTER);

        // Task List
        taskList = new VBox(15);
        taskList.setAlignment(Pos.CENTER);
        refreshTaskList();

        // Add Task Form
        TextField taskField = new TextField();
        taskField.setPromptText("Task name");
        taskField.setMaxWidth(300);

        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");
        durationField.setMaxWidth(300);

        Button addTaskButton = new Button("Add Task");
        addTaskButton.setOnAction(e -> {
            String titleText = taskField.getText();
            int minutes;
            try {
                minutes = Integer.parseInt(durationField.getText());
            } catch (NumberFormatException ex) {
                showAlert("Invalid duration. Please enter a number.");
                return;
            }
            Task task = new Task(0, titleText, minutes);
            taskDAO.addTask(task);
            taskField.clear();
            durationField.clear();
            refreshTaskList();
        });

        VBox inputBox = new VBox(10, taskField, durationField, addTaskButton);
        inputBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, subtitle, timerBox, taskList, inputBox);
    }

    public VBox getRoot() {
        return root;
    }

    private void refreshTaskList() {
        taskList.getChildren().clear();
        List<Task> tasks = taskDAO.getAllTasks();
        for (Task t : tasks) {
            HBox taskBox = new HBox(30);
            taskBox.setAlignment(Pos.CENTER);
            taskBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #fff;");

            Label name = new Label(t.getTitle());
            name.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

            Label duration = new Label(t.getDurationMinutes() + " min");
            duration.setStyle("-fx-font-size: 16;");

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                taskDAO.deleteTask(t.getId());
                refreshTaskList();
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            taskBox.getChildren().addAll(name, spacer, duration, deleteButton);
            taskList.getChildren().add(taskBox);
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void startTimer() {
        if (timer != null) timer.stop();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                timerLabel.setText(formatTime(remainingSeconds));
            } else {
                timer.stop();
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
        remainingSeconds = 1500;
        timerLabel.setText(formatTime(remainingSeconds));
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
