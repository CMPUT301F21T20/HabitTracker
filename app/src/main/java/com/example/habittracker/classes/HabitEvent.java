package com.example.habittracker.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HabitEvent implements Serializable {
    private String habitEventId;
    private String userId;
    private boolean isCompleted;
    private String imageUri;
    private String location;
    private String comment;
    private Date date;

    public HabitEvent(String habitEventId, String userId, boolean isCompleted,
                      String imageUri, String location, String comment, Date date) {
        this.habitEventId = habitEventId;
        this.userId = userId;
        this.isCompleted = isCompleted;
        this.imageUri = imageUri;
        this.location = location;
        this.comment = comment;
        this.date = date;
    }

    public HabitEvent() {}

    public Map<String, Object> getHabitEventMap() {
        Map<String, Object> habitEvent = new HashMap<>();
        habitEvent.put("isCompleted", this.isCompleted);
        habitEvent.put("imageUri", this.imageUri);
        habitEvent.put("location", this.location);
        habitEvent.put("comment", this.comment);
        return habitEvent;
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

    public String getImageUri() {
        return imageUri;
    }

    public void setBitmap(String imageUri) {
        this.imageUri = imageUri;
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
}
