package com.example.habittracker.classes;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.habittracker.controllers.HabitController;

import java.util.ArrayList;
import java.util.Objects;

public class HabitList {

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
     * this function will return true if the habit is in the list and false otherwise
     * @param habit The habit to search for in the list
     */
    public Boolean hasHabit(Habit habit) {
        return habitList.contains(habit);
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
