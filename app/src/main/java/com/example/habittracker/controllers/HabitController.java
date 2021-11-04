package com.example.habittracker.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.habittracker.classes.Habit;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HabitController {
    private FirebaseFirestore db;

    public HabitController() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Saves a habit to the corresponding habit document in Firestore. Will create a new document
     * if it doesn't exist and will always merge the habit (pre-existing document data won't be overwritten)
     * @param habit The habit to save in Firestore
     * @return True if the operation was successful and false otherwise
     */
    public Boolean saveHabit(Habit habit) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> habitMap = habit.getHabitMap();

        // create a mapping within a mapping so merging is possible
        Map<String, Map<String, Object>> mapping = new HashMap<>();
        mapping.put(habit.getHabitId(), habitMap);

        // save the new habit and create doc if it doesn't already exists
        db.collection("Habits").document(habit.getUserId())
                .set(mapping, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
        return success.get();
    }

    public void deleteHabit() {}
}
