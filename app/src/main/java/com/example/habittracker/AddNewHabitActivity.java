package com.example.habittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.controllers.HabitController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.Any;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TODO: Add Error Checking and Firestore integration

// This class hold functionality for when creating a New Habit
public class AddNewHabitActivity extends AppCompatActivity {
    private Date selectedDate;
    private FirebaseFirestore db;
    private String habitId;

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
        db = FirebaseFirestore.getInstance();

        EditText editTitle = findViewById(R.id.addHabitTitle);
        EditText editReason = findViewById(R.id.addHabitReason);
        EditText startDateText = findViewById(R.id.addHabitDateText);
        Button startDateButton = findViewById(R.id.addHabitDateButton);
        ToggleButton[] selectedDates = new ToggleButton[]{
                findViewById(R.id.addHabitMon),
                findViewById(R.id.addHabitTue),
                findViewById(R.id.addHabitWed),
                findViewById(R.id.addHabitThu),
                findViewById(R.id.addHabitFri),
                findViewById(R.id.addHabitSat),
                findViewById(R.id.addHabitSun)
        };
        Switch canShare = findViewById(R.id.addHabitCanShare);
        Button submitButton = findViewById(R.id.addHabitSubmitButton);

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(startDateText);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> mapping;
                FirebaseUser user;
                boolean[] frequency;
                String uid;
                habitId = UUID.randomUUID().toString();

                // TODO: display error message
                //if (errorCheck()) return;

                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    uid = user.getUid();
                } else {
                    // TODO: display error message
                    return;
                }

                // Create a boolean array that is compatible with Habit Class
                // This array maps to the days of the week
                frequency = new boolean[7];
                for (int i = 0; i < 7; i++) {
                    frequency[i] = selectedDates[i].isChecked();
                }

                Habit habit = new Habit(
                        habitId,
                        uid,
                        editTitle.getText().toString(),
                        editReason.getText().toString(),
                        selectedDate,
                        frequency,
                        canShare.isChecked()
                );

                HabitController controller = new HabitController();
                Boolean success = controller.saveHabit(habit);

                if (!success) {
                    // TODO: alert user to try again later;
                }

                finish();
            }
        });
    }

    /**
     * Opens date picker dialog, when date button is clicked
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
                        startDateText.setText((newDate.getYear() + 1900) + "-" +
                                (newDate.getMonth() + 1) + "-" + newDate.getDate());
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    // TODO: Error Checking
    public boolean errorCheck() {
        return true;
    }
}