package com.planner.view;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class ProgressChart {
    public static void showChart(Map<String, Integer> stats) {
        Stage stage = new Stage();
        stage.setTitle("Weekly Study Progress");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Day of Week");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Pomodoros Completed");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pomodoro Count");

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
        VBox root = new VBox(barChart);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}