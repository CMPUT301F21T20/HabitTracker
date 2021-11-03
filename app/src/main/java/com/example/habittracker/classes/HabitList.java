package com.example.habittracker.classes;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.habittracker.R;

import java.util.ArrayList;
import java.util.Objects;

public class HabitList extends ArrayAdapter<Habit> {

    private ArrayList<Habit> habitList;
    private Context context;

    public HabitList(Context context, ArrayList<Habit> habitList) {
        super(context, 0, habitList);
        this.context = context;
        this.habitList = habitList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

//
//        if(view == null){
//            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
//        }
//
//        Habit habit = habitList.get(position);
//
//        // TODO: textview and layout stuff

        return view;
    }

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
     */
    public void addHabit(Habit habit) {
        habitList.add(habit);
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
     */
    public void deleteHabit(Habit habit) {
        habitList.remove(habit);
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
