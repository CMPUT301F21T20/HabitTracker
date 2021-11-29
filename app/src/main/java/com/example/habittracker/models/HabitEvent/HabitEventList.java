package com.example.habittracker.models.HabitEvent;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A class that represents a habit event list
 */
public class HabitEventList {
    private ArrayList<HabitEvent> habitEventList;

    /**
     * Contructor for habit event class
     * @param habitEventList habit event list
     */
    public HabitEventList(ArrayList<HabitEvent> habitEventList) {
        this.habitEventList = habitEventList;
    }

    /**
     * Contructor for habit event class
     */
    public HabitEventList() {
        this.habitEventList = new ArrayList<>();
    }

    /**
     * Gets size of habit event list
     * @return length of habit event
     */
    public int getCount() {
        return habitEventList.size();
    }

    /**
     * Adds a habit event to habit event list
     * @param habitEvent the habit event to add
     */
    public void addHabitEvent(HabitEvent habitEvent) {
        habitEventList.add(habitEvent);
    }

    /**
     * deletes a habit event from habit event list
     * @param habitEvent the habit event to delete
     */
    public void deleteHabitEvent(HabitEvent habitEvent) {
        habitEventList.remove(habitEvent);
    }

    /**
     * Gets a habit event given a certain index
     * @param index the index of the habit event
     * @return the indexed habit
     */
    public HabitEvent get(int index) {
        return habitEventList.get(index);
    }

    /**
     * clears all the items from the habit event list
     */
    public void clearHabitEventList() {
        this.habitEventList.clear();
    }

    /**
     * check if a habit event exists in the list
     * @param habitEvent the habit event to check
     * @return true if it exists, false if not
     */
    public Boolean hasHabitEvent(HabitEvent habitEvent) {
        return habitEventList.contains(habitEvent);
    }

    /**
     * This function clears the habit event list
     */
    public void clear() {
        habitEventList.clear();
    }

    /**
     * Override the equal operation for better comparison
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitEventList that = (HabitEventList) o;
        return habitEventList.equals(that.habitEventList);
    }

    /**
     * Generate a unique number identifier
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(habitEventList);
    }

    /**
     * Getters and Setters
     */

    public ArrayList<HabitEvent> getHabitEventList() {
        return habitEventList;
    }

    public void setHabitEventList(ArrayList<HabitEvent> habitEventList) {
        this.habitEventList = habitEventList;
    }
}
