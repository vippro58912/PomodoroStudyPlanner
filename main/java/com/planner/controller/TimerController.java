package com.planner.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TimerController {
    private Timeline timeline;
    private int secondsRemaining = 1500; // 25 mins

    public void startTimer(Label label) {
        stopTimer();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining--;
            int mins = secondsRemaining / 60;
            int secs = secondsRemaining % 60;
            label.setText(String.format("%02d:%02d", mins, secs));
            if (secondsRemaining <= 0) stopTimer();
        }));
        timeline.setCycleCount(secondsRemaining);
        timeline.play();
    }

    public void stopTimer() {
        if (timeline != null) timeline.stop();
        secondsRemaining = 1500;
    }
}
