package com.example.habittracker.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HabitEvent implements Serializable {
    private Habit habit;
    private String habitEventId;
    private String userId;
    private boolean isCompleted;
    private String imageStorageNamePrefix;
    private String location;
    private String comment;
    private Date createDate;
    private Date completedDate;

    public HabitEvent(Habit habit, String habitEventId, String userId, boolean isCompleted,
                      String imageStorageNamePrefix, String location, String comment, Date createDate, Date completedDate) {
        this.habit = habit;
        this.habitEventId = habitEventId;
        this.userId = userId;
        this.isCompleted = isCompleted;
        this.imageStorageNamePrefix = imageStorageNamePrefix;
        this.location = location;
        this.comment = comment;
        this.createDate = createDate;
        this.completedDate = completedDate;
    }

    public HabitEvent() {}

    public Map<String, Object> getHabitEventMap() {
        Map<String, Object> habitEvent = new HashMap<>();
        habitEvent.put("isCompleted", this.isCompleted);
        habitEvent.put("imageStorageNamePrefix", this.imageStorageNamePrefix);
        habitEvent.put("location", this.location);
        habitEvent.put("comment", this.comment);
        habitEvent.put("createdDate", this.createDate);
        habitEvent.put("completedDate", this.completedDate);
        habitEvent.put("habitTitle", this.habit.getTitle());
        return habitEvent;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public String getHabitEventId() {
        return habitEventId;
    }

    public void setHabitEventId(String habitEventId) {
        this.habitEventId = habitEventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getImageStorageNamePrefix() {
        return imageStorageNamePrefix;
    }

    public void setImageStorageNamePrefix(String imageStorageNamePrefix) {
        this.imageStorageNamePrefix = imageStorageNamePrefix;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }
}
