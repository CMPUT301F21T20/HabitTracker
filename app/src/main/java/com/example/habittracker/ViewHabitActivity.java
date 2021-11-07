package com.example.habittracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.habittracker.classes.Habit;

import java.util.ArrayList;
import java.util.Date;

/**
 * This activity is for viewing a habit
 */
public class ViewHabitActivity extends AppCompatActivity {
    private Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);

        // Must pass the Habit through the intent!
        // reference the following link if unsure on how to do this:
        // https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
        habit = (Habit) getIntent().getSerializableExtra("Habit");

        TextView titleText = findViewById(R.id.viewHabitTitle);
        TextView reasonText = findViewById(R.id.viewHabitReason);
        TextView startDateText = findViewById(R.id.viewHabitDateText);
        TextView activeDaysText = findViewById(R.id.viewActiveDaysText);
        Button addHabitEventBtn = findViewById(R.id.addHabitEventBtn);

        titleText.setText(habit.getTitle());
        reasonText.setText(habit.getReason());
        startDateText.setText(getDateText(habit.getDateCreated()));
        activeDaysText.setText(getDaysText(habit.getFrequency()));

        addHabitEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddHabitEventActivity();
            }
        });
    }

    /**
     * Get the date in string format of yyyy-dd-mm
     * @param date the date to process
     * @return the formatted string
     */
    public String getDateText(Date date) {
        // Add 1900 to year, as the getYear function returns year - 1900
        return (date.getYear() + 1900) + "-" +
                (date.getMonth() + 1) + "-" + date.getDate();
    }

    /**
     * Get the string of active days
     * @param frequency the frequency list from habit class
     * @return the formatted string
     */
    public String getDaysText(ArrayList<Integer> frequency) {
        String out = "";

        // map days of week to frequency list
        String[] mapping = new String[] {"MON", "TUE", "WED", "THU",
                "FRI", "SAT", "SUN"};

        for (int i = 0; i < 7; i++) {
            // Was getting error when comparing integer values, cast to String as a work around
            boolean check = String.valueOf(frequency.get(i)).equals("1");

            if (check && out.length() == 0) {
                // If this is the first day we are adding, don't want comma in front
                out += mapping[i];
            } else if (check) {
                out += ", " + mapping[i];
            }
        }

        return out.length() == 0 ? "No active days selected" : out;
    }

    public void openAddHabitEventActivity(){
        Intent intent = new Intent(this, AddNewHabitEventActivity.class);
        intent.putExtra("Habit", habit);
        this.startActivity(intent);
    }
}