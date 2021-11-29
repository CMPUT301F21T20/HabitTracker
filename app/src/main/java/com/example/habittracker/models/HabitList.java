package com.example.habittracker.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * List of all habits for a selected user
 */
public class HabitList implements Serializable {

    // Use ArrayList to match up the required uploading type of firestone
    private ArrayList<Habit> habitList;

    public HabitList(ArrayList<Habit> habitList) {
        this.habitList = habitList;
    }

    public HabitList() {
        this.habitList = new ArrayList<>();
    }

    /**
     * this function will get the number of Habits in the habitList
     * @return integer indicating number of Habits in the list
     */
    public int getCount() {
        return habitList.size();
    }

    public void addHabit(Habit habit) {
        habitList.add(habit);
    }

    public void deleteHabit(Habit habit) {
        habitList.remove(habit);
    }

    /**
     * This function will return the specific Habit at the specified index
     * @param index the index of the Habit
     * @return the habit at the certain index
     */
    public Habit getHabit(int index) {
        return habitList.get(index);
    }

    public Habit getHabit(String habitId) throws Exception {
        for (Habit habit : habitList) {
            if (habit.getHabitId().equals(habitId)) {
                return habit;
            }
        }
        throw new Exception("Habit does not exist");
    }

    /**
     * This function clears the habit list
     */
    public void clearHabitList() {
        this.habitList.clear();
    }

    /**
     * this function will return true if the habit is in the list and false otherwise
     * @param habit The habit to search for in the list
     */
    public Boolean hasHabit(Habit habit) {
        return habitList.contains(habit);
    }

    /**
     * Override the equal operation for better comparison
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitList that = (HabitList) o;
        return habitList.equals(that.habitList);
    }

    /**
     * Generate a unique number identifier
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(habitList);
    }

    public ArrayList<Habit> getHabitList() {
        return habitList;
    }
}
