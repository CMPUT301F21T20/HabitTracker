package com.example.habittracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;
import java.util.Calendar;
import java.util.Date;

public class AddNewHabitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_habit);

        Intent intent = getIntent();

        EditText editTitle = findViewById(R.id.addHabitTitle);
        EditText editReason = findViewById(R.id.addHabitReason);
        EditText startDateText = findViewById(R.id.addHabitDateText);
        Button startDateButton = findViewById(R.id.addHabitDateButton);
        ToggleButton[] selectedDates = new ToggleButton[] {
                findViewById(R.id.addHabitMon),
                findViewById(R.id.addHabitTue),
                findViewById(R.id.addHabitWed),
                findViewById(R.id.addHabitThu),
                findViewById(R.id.addHabitFri),
                findViewById(R.id.addHabitSat),
                findViewById(R.id.addHabitSun)
        };
        Switch locationSwitch = findViewById(R.id.addHabitLocation);
        Switch denoteDone = findViewById(R.id.addHabitDenoteDone);
        Switch canShare = findViewById(R.id.addHabitCanShare);

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(startDateText);
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
                        // Update date
                        newDate.setMonth(monthOfYear);
                        newDate.setYear(yearSelect - 1900);
                        newDate.setDate(dayOfMonth);
//                        setDate(newDate);

                        // Add 1900 to year, as the getYear function returns year - 1900
                        startDateText.setText((newDate.getYear() + 1900) + "-" + (newDate.getMonth() + 1) + "-" + newDate.getDate());
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}