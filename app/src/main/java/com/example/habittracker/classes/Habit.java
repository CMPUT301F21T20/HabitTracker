package com.example.habittracker.classes;

import com.google.protobuf.Any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A class that represents a habit
public class Habit {
    private String habitId;
    private String userId;
    private String title;
    private String reason;
    private Date dateCreated;
    private ArrayList<Integer> frequency;
    private boolean canShare;

    public Habit(String habitId, String userId, String title, String reason,
                 Date dateCreated, ArrayList<Integer> frequency, boolean canShare) {
        this.habitId = habitId;
        this.userId = userId;
        this.title = title;
        this.reason = reason;
        this.dateCreated = dateCreated;
        this.frequency = frequency;
        this.canShare = canShare;
    }

    public Habit() {}

    /**
     * Return a Map of the habit class, useful for firestore methods.
     * WARNING: Map does not include User ID or Habit ID since User ID is the document name and
     * Habit ID is the key of the mapping in which the habit data is stored.
     * @return a HashMap representation of the Habit
     */
    public Map<String, Object> getHabitMap() {
        Map<String, Object> habit = new HashMap<>();
        habit.put("title", this.title);
        habit.put("reason", this.reason);
        habit.put("dateCreated", this.dateCreated);
        habit.put("frequency", this.frequency);
        habit.put("canShare", this.canShare);
        return habit;
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

    public ArrayList<Integer> getFrequency() {
        return this.frequency;
    }

    public void setFrequency(ArrayList<Integer> frequency) {
        this.frequency = frequency;
    }

    public boolean getCanShare() {
        return canShare;
    }

    public void setCanShare(boolean canShare) {
        this.canShare = canShare;
    }
}
