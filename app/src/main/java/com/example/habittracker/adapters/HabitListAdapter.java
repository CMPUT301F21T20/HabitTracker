package com.example.habittracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habittracker.ViewHabitActivity;
import com.example.habittracker.classes.Habit;
import com.example.habittracker.R;
import com.example.habittracker.classes.HabitList;
import com.example.habittracker.controllers.HabitController;

/**
 * Adapter for HabitList, provide access to HabitList data, shows a list of habits
 */
public class HabitListAdapter extends ArrayAdapter<Habit> {
    private Context context;
    private HabitList habitList;

    public HabitListAdapter(@NonNull Context context, HabitList habitList) {
        super(context, 0, habitList.getHabitList());
        this.context = context;
        this.habitList = habitList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_habit_list, parent, false);
        }

        Habit habit = habitList.get(position);

        TextView habitListTitle = view.findViewById(R.id.habitListTitle);
        Button viewHabitButton = view.findViewById(R.id.habitListViewButton);
        Button deleteHabitButton = view.findViewById(R.id.habitListDeleteButton);

        habitListTitle.setText(habit.getTitle());

        viewHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewHabitActivity(habit);
            }
        });

        deleteHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHabit(habit);
            }
        });

        return view;
    }

    /**
     * Opens the view habit activity
     * @param habit the habit to display information about
     */
    public void openViewHabitActivity(Habit habit) {
        Intent i = new Intent(context, ViewHabitActivity.class);
        i.putExtra("Habit", habit);
        context.startActivity(i);
    }

    /**
     * Deletes a habit from firebase
     * @param habit the habit to delete
     */
    public void deleteHabit(Habit habit) {
        HabitController hc = new HabitController();
        hc.deleteHabit(habit);
    }
}
