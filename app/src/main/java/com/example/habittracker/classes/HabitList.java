package com.example.habittracker.classes;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.habittracker.controllers.HabitController;

import java.util.ArrayList;
import java.util.Objects;

public class HabitList {

    private ArrayList<Habit> habitList;
    private HabitController habitController;

    public HabitList(ArrayList<Habit> habitList) {
        this.habitList = habitList;
        this.habitController = new HabitController();
    }

    public HabitList() {}

    /**
     * this function will get the number of Habits in the habitList
     * @return integer indicating number of Habits in the list
     */
    public int getCount() {
        return habitList.size();
    }

    /**
     * this function will add a habit object into the list
     * @param habit The habit to add to the list
     * @return True if successful false otherwise
     */
    public Boolean addHabit(Habit habit) {
        Boolean success = habitController.saveHabit(habit);
        if (success) habitList.add(habit);
        return success;
    }

    /**
     * this function will return true if the habit is in the list and false otherwise
     * @param habit The habit to search for in the list
     */
    public Boolean hasHabit(Habit habit) {
        return habitList.contains(habit);
    }

    /**
     * this function deletes a habit from the habitList
     * @param habit The habit to delete from the list
     * @return True if successful false otherwise
     */
    public Boolean deleteHabit(Habit habit) {
        Boolean success = habitController.deleteHabit(habit);
        if (success) habitList.remove(habit);
        return success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitList that = (HabitList) o;
        return habitList.equals(that.habitList);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(habitList);
    }
}
