package com.example.habittracker.classes;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HabitEvent implements Serializable {
    private String habitEventId;
    private String userId;
    private boolean isCompleted;
    private Bitmap bitmap;
    private String location;
    private String comment;

    public HabitEvent(String habitEventId, String userId, boolean isCompleted,
                      Bitmap bitmap, String location, String comment) {
        this.habitEventId = habitEventId;
        this.userId = userId;
        this.isCompleted = isCompleted;
        this.bitmap = bitmap;
        this.location = location;
        this.comment = comment;
    }

    public HabitEvent() {}

    public Map<String, Object> getHabitEventMap() {
        Map<String, Object> habitEvent = new HashMap<>();
        habitEvent.put("isCompleted", this.isCompleted);
        habitEvent.put("bitmap", this.bitmap);
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
