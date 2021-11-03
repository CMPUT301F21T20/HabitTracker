package com.example.habittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewHabitActivity extends AppCompatActivity {
    private String habitId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);

        Bundle bundle = getIntent().getExtras();
        habitId = bundle.getString("habitId");

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


        db = FirebaseFirestore.getInstance();
        db.collection("Habits").document(habitId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIREBASE", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("FIREBASE", "No such document");
                    }
                } else {
                    Log.d("FIREBASE", "get failed with ", task.getException());
                }
            }
        });
    }
}