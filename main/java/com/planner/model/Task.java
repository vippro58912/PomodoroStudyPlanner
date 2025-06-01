package com.planner.model;

public class Task {
    private int id;
    private String title;
    private int durationMinutes;
    private int userId;
    private boolean done;


    public Task(int id, String title, int durationMinutes, int userId, boolean done) {
        this.id = id;
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.userId = userId;
        this.done = done;
    }


    public Task(int id, String title, int durationMinutes, int userId) {
        this(id, title, durationMinutes, userId, false);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getUserId() { return userId; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
