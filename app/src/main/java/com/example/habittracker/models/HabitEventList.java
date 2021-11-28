package com.example.habittracker.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Objects;

public class HabitEventList {

    private ArrayList<HabitEvent> habitEventList;

    public HabitEventList(ArrayList<HabitEvent> habitEventList) {
        this.habitEventList = habitEventList;
    }

    public HabitEventList() {
        this.habitEventList = new ArrayList<>();
    }

    public int getCount() {
        return habitEventList.size();
    }

    public void deleteHabitEvent(HabitEvent habitEvent) {
        habitEventList.remove(habitEvent);
    }

    public HabitEvent get(int index) {
        return habitEventList.get(index);
    }

    public void addHabitEvent(HabitEvent habitEvent) {
        this.habitEventList.add(habitEvent);
    }

    public void clearHabitEventList() {
        this.habitEventList.clear();
    }

    public Boolean hasHabitEvent(HabitEvent habitEvent) {
        return habitEventList.contains(habitEvent);
    }

    public void clear() {
        habitEventList.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitEventList that = (HabitEventList) o;
        return habitEventList.equals(that.habitEventList);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(habitEventList);
    }

    public ArrayList<HabitEvent> getHabitEventList() {
        return habitEventList;
    }

    public void setHabitEventList(ArrayList<HabitEvent> habitEventList) {
        this.habitEventList = habitEventList;
    }
}
