package com.example.habittracker.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that represents a habit event, implements serilizable to allow to pass into activity thorugh intent
 */
public class HabitEvent implements Serializable {
    private Habit habit;
    private String habitEventId;
    private String userId;
    private boolean isCompleted;
    private String imageStorageNamePrefix;
    private String location;
    private String comment;
    private LocalDate createDate;
    private LocalDate completedDate;
    private String docId;

    /**
     * Habit Event Contructor
     * @param habit the habit associated with habit Event
     * @param habitEventId the id of the habit event
     * @param userId the id of the user
     * @param isCompleted whether we are denoting the habit event as completed
     * @param imageStorageNamePrefix the url of the image
     * @param location the location where the habit event was recorded
     * @param comment the comment of the habit event
     * @param createDate the date the habit event was created
     * @param completedDate teh date the habit event was completed
     */
    public HabitEvent(Habit habit, String habitEventId, String userId, boolean isCompleted,
        String imageStorageNamePrefix, String location, String comment, LocalDate createDate, LocalDate completedDate) {
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

    /**
     * NOTE: converts createdDate and completedDate to Date from LocalDateTime and LocalDate respectively.
     * This is because Firebase works with Date natively but not LocalDate or LocalDateTime
     * @return
     */
    public Map<String, Object> getHabitEventMap() {
        Map<String, Object> habitEvent = new HashMap<>();
        habitEvent.put("isCompleted", this.isCompleted);
        habitEvent.put("imageStorageNamePrefix", this.imageStorageNamePrefix);
        habitEvent.put("location", this.location);
        habitEvent.put("comment", this.comment);
        Date legacyDate = Date.from(this.createDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        habitEvent.put("createdDate", legacyDate);
        Date legacyDate2 = Date.from(this.completedDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        habitEvent.put("completedDate", legacyDate2);
        habitEvent.put("habitId", this.habit.getHabitId());
        return habitEvent;
    }

    /**
     * Getters and setters
     */

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

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
