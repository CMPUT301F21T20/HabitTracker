package com.example.habittracker.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.habittracker.classes.Habit;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HabitController {
    private final String uid;
    private FirebaseFirestore db;

    public HabitController(String uid) {
        this.uid = uid;
        this.db = FirebaseFirestore.getInstance();
    }

    public Boolean saveHabit(Habit habit) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> mapping = habit.getHabitMap();
        db.collection("Habits").document(uid)
                .set(mapping) //TODO: merge into document at correct place
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
        return success.get();
    }

    public void deleteHabit() {}
}
