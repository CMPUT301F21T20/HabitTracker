package com.example.habittracker.classes;

import java.util.Date;

public class Habit {
    private String habitId;
    private String userId;
    private String title;
    private String reason;
    private Date dateCreated;
    private int frequency;
    private boolean isDone;

    public Habit(String habitId, String userId, String title, String reason, Date dateCreated, int frequency, boolean isDone) {
        this.habitId = habitId;
        this.userId = userId;
        this.title = title;
        this.reason = reason;
        this.dateCreated = dateCreated;
        this.frequency = frequency;
        this.isDone = isDone;
    }

    public String getHabitId() {
        return habitId;
    }

    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
