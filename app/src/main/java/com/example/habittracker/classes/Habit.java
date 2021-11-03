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
    private boolean[] frequency;
    private boolean isDone;
    private boolean canShare;

    public Habit(String habitId, String userId, String title, String reason,
                 Date dateCreated, boolean[] frequency, boolean isDone, boolean canShare) {
        this.habitId = habitId;
        this.userId = userId;
        this.title = title;
        this.reason = reason;
        this.dateCreated = dateCreated;
        this.frequency = frequency;
        this.isDone = isDone;
        this.canShare = canShare;
    }

    /**
     * Return a Map of the habit class, useful for firestore methods
     * @return a HashMap representation of the Habit
     */
    public Map<String, Object> getHabitMap() {
        // firestore won't accept arrays. Therefore convert to a List
        // of Integers to upload to firestore
        ArrayList<Integer> frequencyList = new ArrayList<Integer>();
        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i]) frequencyList.add(1);
            else frequencyList.add(0);
        }

        Map<String, Object> habit = new HashMap<>();
        habit.put("habitId", this.habitId);
        habit.put("userId", this.userId);
        habit.put("title", this.title);
        habit.put("reason", this.reason);
        habit.put("dateCreated", this.dateCreated);
        habit.put("frequency", frequencyList);
        habit.put("isDone", this.isDone);
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

    public boolean[] getFrequency() {
        return frequency;
    }

    public void setFrequency(boolean[] frequency) {
        this.frequency = frequency;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isCanShare() {
        return canShare;
    }

    public void setCanShare(boolean canShare) {
        this.canShare = canShare;
    }
}
