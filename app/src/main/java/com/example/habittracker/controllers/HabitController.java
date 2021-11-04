package com.example.habittracker.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.habittracker.classes.Habit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HabitController {
    private final FirebaseFirestore db;

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

    /**
     * Deletes a habit from the corresponding habit document in Firestore.
     * @param habit The habit to delete
     * @return True if the operation was successful and false otherwise
     */
    public Boolean deleteHabit(Habit habit) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put(habit.getHabitId(), FieldValue.delete());
        db.collection("Habits").document(habit.getUserId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
        return success.get();
    }

    /**
     * Provided a userId and HabitId, retrieve a habit
     * @param userId the userId to which the hjabti belongs
     * @param habitId the is of the habit
     * @return
     */
    public Habit getHabit(String userId, String habitId) {
        Habit habit = new Habit();
        habit.setTitle("-1");
        db.collection("Habits").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("FIREBASE", "DocumentSnapshot data: " + document.get(habitId));
                                HashMap<String, Object> data = (HashMap<String, Object>) document.get(habitId);
                                habit.setTitle((String) data.get("title"));
                                habit.setReason((String) data.get("reason"));
                                //TODO set the frequency

                            } else {
                                Log.d("FIREBASE", "No such document");
                            }
                        } else {
                            Log.d("FIREBASE", "get failed with ", task.getException());
                        }
                    }
        });
        while (habit.getTitle().equals("-1"));
        return habit;
    }
}
