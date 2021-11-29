package com.example.habittracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habittracker.R;
import com.example.habittracker.controllers.HabitListController;
import com.example.habittracker.models.Habit.Habit;
import com.example.habittracker.models.Habit.HabitList;

/**
 * Adapter for HabitList, provide access to HabitList data, shows a list of habits
 */
public class HabitListAdapter extends ArrayAdapter<Habit> {
    private Context context;
    private HabitList habitList;

    /**
     * Contructor for Habit List Adapter
     * @param context the context of the app
     * @param habitList the list of habits to show
     */
    public HabitListAdapter(@NonNull Context context, HabitList habitList) {
        super(context, 0, habitList.getHabitList());
        this.context = context;
        this.habitList = habitList;
    }

    /**
     * Configures view of list item
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_habit_list, parent, false);
        }

        Habit habit = habitList.getHabit(position);

        TextView habitListTitle = view.findViewById(R.id.habitListTitle);
        ImageView deleteHabitButton = view.findViewById(R.id.habitListDeleteButton);

        habitListTitle.setText(habit.getTitle());

        deleteHabitButton.setOnClickListener(v -> {
            HabitListController controller = HabitListController.getInstance();
            controller.deleteHabit(habit);
        });

        return view;
    }


}
