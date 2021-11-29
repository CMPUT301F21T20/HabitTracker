package com.example.habittracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habittracker.R;
import com.example.habittracker.models.Habit;
import com.example.habittracker.models.HabitList;

/**
 * Adapter for user habit list, handles functionality for user habit list items
 */
public class UserHabitListAdapter extends ArrayAdapter<Habit> {
    private Context context;
    private HabitList habitList;

    /**
     * Constructor for User Habit List Adapter class
     * @param context the context of the app
     * @param habitList the list of habits to show
     */
    public UserHabitListAdapter(@NonNull Context context, HabitList habitList) {
        super(context, 0, habitList.getHabitList());
        this.context = context;
        this.habitList = habitList;
    }

    /**
     * Configures view for user habit list item
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_habits_list, parent, false);
        }

        Habit habit = habitList.getHabit(position);
        TextView habitListTitle = view.findViewById(R.id.habit_text);
        habitListTitle.setText(habit.getTitle());

        return view;
    }

}
