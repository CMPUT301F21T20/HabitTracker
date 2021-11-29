package com.example.habittracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.habittracker.R;
import com.example.habittracker.controllers.HabitListController;
import com.example.habittracker.models.Habit.Habit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * This class hold functionality for when creating a New Habit
  */
public class AddNewHabitActivity extends AppCompatActivity {
    private ImageView addHabit_back_icon;
    private Date selectedDate;
    private String habitId;
    private Habit habit = null;
    private EditText editTitle;
    private EditText editReason;
    private EditText startDateText;
    private Button startDateButton;
    private ToggleButton[] selectedDates;
    private SwitchCompat canShare;
    private Button submitButton;
    private TextView streak;
    private TextView highestStreak;


    /**
     * This function is run when the activity is starting
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_habit);

        Intent intent = getIntent();

        // Retrieve all view elements
        addHabit_back_icon = findViewById(R.id.addHabit_back_icon);
        editTitle = findViewById(R.id.addHabitTitle);
        editReason = findViewById(R.id.addHabitReason);
        startDateText = findViewById(R.id.addHabitDateText);
        startDateButton = findViewById(R.id.addHabitDateButton);
        selectedDates = new ToggleButton[]{
                findViewById(R.id.addHabitMon),
                findViewById(R.id.addHabitTue),
                findViewById(R.id.addHabitWed),
                findViewById(R.id.addHabitThu),
                findViewById(R.id.addHabitFri),
                findViewById(R.id.addHabitSat),
                findViewById(R.id.addHabitSun)
        };
        canShare = findViewById(R.id.addHabitCanShare);
        submitButton = findViewById(R.id.addHabitSubmitButton);


        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(startDateText);
            }
        });

        addHabit_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHabit_back_icon.setAlpha(0.5f);
                onSupportNavigateUp();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> mapping;
                FirebaseUser user;
                ArrayList<Integer> frequency;
                String uid;
                habitId = UUID.randomUUID().toString();

                // Error message checking
                if (errorCheck(editTitle, editReason, startDateText)) {
                    return;
                }

                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    uid = user.getUid();
                } else {
                    return;
                }

                // Create a boolean array that is compatible with Habit Class
                // This array maps to the days of the week
                frequency = new ArrayList<>(7);
                for (int i = 0; i < 7; i++) {
                    int temp = 0;
                    if (selectedDates[i].isChecked()) temp = 1;
                    frequency.add(i, temp);
                }

                Habit habit = new Habit(
                        habitId,
                        uid,
                        editTitle.getText().toString(),
                        editReason.getText().toString(),
                        selectedDate,
                        frequency,
                        canShare.isChecked(),
                        0,
                        0

                );

                HabitListController controller = HabitListController.getInstance();
                Boolean success = controller.saveHabit(habit);


                finish();
            }
        });
    }

    /**
     * Opens date picker dialog, when date button is clicked
     * @param startDateText The text view to update
     */
    public void datePicker(EditText startDateText) {
        final Calendar c = Calendar.getInstance();
        Date newDate = new Date();

        // Initialize values as current date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearSelect, int monthOfYear, int dayOfMonth) {
                        // Update Selected Date
                        newDate.setMonth(monthOfYear);
                        newDate.setYear(yearSelect - 1900);
                        newDate.setDate(dayOfMonth);
                        selectedDate = new Date(String.valueOf(newDate));

                        // Add 1900 to year, as the getYear function returns year - 1900

                        if(newDate.getDate() >= 10) {
                            startDateText.setText((newDate.getYear() + 1900) + "-" +
                                    (newDate.getMonth() + 1) + "-" + newDate.getDate());
                        }else{
                            startDateText.setText((newDate.getYear() + 1900) + "-" +
                                    (newDate.getMonth() + 1) + "-0" + newDate.getDate());
                        }
                        startDateText.setError(null);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Checks for any input errors when adding a new habit
     * @param editTitle The title input
     * @param editReason The reason input
     * @param startDateText the start date input
     * @return true if there was an error, false if not
     */
    public boolean errorCheck(EditText editTitle, EditText editReason, EditText startDateText) {
        boolean titleError = false;
        boolean reasonError = false;
        boolean startDateError = false;

        if(editTitle.getText().toString().length() == 0){
            editTitle.setError("Title is required");
            titleError = true;
        }

        if(editTitle.getText().toString().length() > 20){
            editTitle.setError("Title exceeds 20 characters");
            titleError = true;
        }

        if(editReason.getText().toString().length() == 0){
            editReason.setError("Reason is required");
            reasonError = true;
        }

        if(editReason.getText().toString().length() > 30){
            editTitle.setError("Reason exceeds 30 characters");
            reasonError = true;
        }

        if (selectedDate == null) {
            startDateText.setError("No data was selected");
            startDateError = true;
        }

        return titleError || reasonError || startDateError;
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
}