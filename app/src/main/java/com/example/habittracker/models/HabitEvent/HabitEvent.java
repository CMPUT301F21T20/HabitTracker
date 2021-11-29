package com.example.habittracker.models.HabitEvent;

import com.example.habittracker.models.Habit.Habit;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
    private LocalDate createDate;
    private LocalDate completedDate;
    private String docId;

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
