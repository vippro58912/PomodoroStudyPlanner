package com.planner.model;

public class Task {
    private int id;
    private String title;
    private int durationMinutes;

    public Task(int id, String title, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.durationMinutes = durationMinutes;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getDurationMinutes() { return durationMinutes; }

    public void setTitle(String title) { this.title = title; }
    public void setDurationMinutes(int minutes) { this.durationMinutes = minutes; }
}
