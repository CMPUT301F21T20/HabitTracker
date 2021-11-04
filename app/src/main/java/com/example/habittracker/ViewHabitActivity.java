package com.example.habittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.controllers.HabitController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class ViewHabitActivity extends AppCompatActivity {
    private String habitId;
    private String userId;
    private FirebaseFirestore db;
    private Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);

        Bundle bundle = getIntent().getExtras();
        habitId = bundle.getString("habitId");
        userId = bundle.getString("userId");

        HabitController controller = new HabitController();
        habit = controller.getHabit(userId, habitId);

        HabitController c = new HabitController();
        c.getHabit(FirebaseAuth.getInstance().getCurrentUser().getUid(), "ce5df86c-9712-4bfc-8a2e-eebf9270d291");

        EditText editTitle = findViewById(R.id.viewHabitTitle);
        EditText editReason = findViewById(R.id.viewHabitReason);
        EditText startDateText = findViewById(R.id.viewHabitDateText);
        ToggleButton[] selectedDates = new ToggleButton[] {
                findViewById(R.id.viewHabitMon),
                findViewById(R.id.viewHabitTue),
                findViewById(R.id.viewHabitWed),
                findViewById(R.id.viewHabitThu),
                findViewById(R.id.viewHabitFri),
                findViewById(R.id.viewHabitSat),
                findViewById(R.id.viewHabitSun)
        };
        Switch locationSwitch = findViewById(R.id.viewHabitLocation);
        Switch denoteDone = findViewById(R.id.viewHabitDenoteDone);
        Switch canShare = findViewById(R.id.viewHabitCanShare);

        editTitle.setText(habit.getTitle());
        editReason.setText(habit.getReason());
    }
}