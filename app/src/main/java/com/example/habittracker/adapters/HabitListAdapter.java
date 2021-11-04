package com.example.habittracker.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.habittracker.classes.Habit;

public class HabitListAdapter extends ArrayAdapter<Habit> {
    public HabitListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
