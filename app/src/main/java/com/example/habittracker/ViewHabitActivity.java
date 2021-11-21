package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;
import com.example.habittracker.controllers.HabitListController;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;


/**
 * This activity is for viewing a habit
 */
public class ViewHabitActivity extends AppCompatActivity {
    private ImageView viewHabit_back_icon;
    private Habit habit;
    private FirebaseFirestore db;
    private TextView titleText;
    private TextView reasonText;
    private TextView startDateText;
    private TextView activeDaysText;
    private TextView viewSharedText;
    private Button editHabitBtn;
    private Button addHabitEventBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        // Must pass the Habit through the intent!
        // reference the following link if unsure on how to do this:
        // https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
        habit = (Habit) intent.getSerializableExtra("Habit");

        viewHabit_back_icon = findViewById(R.id.viewHabit_back_icon);
        titleText = findViewById(R.id.viewHabitTitle);
        reasonText = findViewById(R.id.viewHabitReason);
        startDateText = findViewById(R.id.viewHabitDateText);
        activeDaysText = findViewById(R.id.viewActiveDaysText);
        viewSharedText = findViewById(R.id.viewSharedText);
        editHabitBtn = findViewById(R.id.editHabitBtn);
        addHabitEventBtn = findViewById(R.id.addHabitEventBtn);

        titleText.setText(habit.getTitle());
        reasonText.setText(habit.getReason());
        startDateText.setText(getDateText(habit.getDateCreated()));
        activeDaysText.setText(getDaysText(habit.getFrequency()));
        if (habit.getCanShare()){
            viewSharedText.setText("SHARED");
        }else{
            viewSharedText.setText("NOT SHARED");
        }

        editHabitBtn.setOnClickListener(view -> openEditHabitActivity());

        viewHabit_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHabit_back_icon.setAlpha(0.5f);
                onSupportNavigateUp();
            }
        });

        editHabitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditHabitActivity();
            }
        });

        addHabitEventBtn.setOnClickListener(view -> openAddHabitEventActivity());

        db.collection("Habits").document(habit.getUserId()).addSnapshotListener((docSnapshot, e) -> {
            HabitList newHabitList = new HabitList();
            HabitListController.convertToHabitList(docSnapshot, newHabitList);
            updateAttributes(newHabitList.getHabit(habit.getHabitId()));
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

    /**
     * Handles openinng of addNewHabitEventActivtity
     */
    public void openAddHabitEventActivity(){
        Intent intent = new Intent(this, AddNewHabitEventActivity.class);
        intent.putExtra("Habit", habit);
        ViewHabitActivity.this.startActivity(intent);
    }

    /**
     * Handles openinng of editHabitActivtity
     */
    public void openEditHabitActivity(){
        Intent intent = new Intent(this, EditHabitActivity.class);
        intent.putExtra("Habit", habit);
        ViewHabitActivity.this.startActivity(intent);
    }

    /**
     * rewrite the back button on the action bar to make its functionality works better
     * @return false to return to the activity before
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    /**
     * Update all attributes for the view and update habit attribute
     * @param newHabit the habit to update attributes to
     */
    public void updateAttributes(Habit newHabit) {
        habit = newHabit;
        titleText.setText(habit.getTitle());
        reasonText.setText(habit.getReason());
        startDateText.setText(getDateText(habit.getDateCreated()));
        activeDaysText.setText(getDaysText(habit.getFrequency()));
        if (habit.getCanShare()){
            viewSharedText.setText("SHARED");
        } else {
            viewSharedText.setText("NOT SHARED");
        }
    }
}