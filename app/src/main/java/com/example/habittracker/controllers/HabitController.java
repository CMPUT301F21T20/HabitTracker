package com.example.habittracker.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Habit Controller is for creating, deleting and modifying habit
 */
public class HabitController {
    private final FirebaseFirestore DB;

    public HabitController() {
        this.DB = FirebaseFirestore.getInstance();
    }

    /**
     * Saves a habit to the corresponding habit document in Firestore. Will create a new document
     * if it doesn't exist and will always merge the habit (pre-existing document data won't be overwritten)
     *
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
        DB.collection("Habits").document(habit.getUserId())
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
     *
     * @param habit The habit to delete
     * @return True if the operation was successful and false otherwise
     */
    public Boolean deleteHabit(Habit habit) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> updates = new HashMap<>();
        updates.put(habit.getHabitId(), FieldValue.delete());
        DB.collection("Habits").document(habit.getUserId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
        return success.get();
    }

    public Boolean editHabit(Habit habit) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> habitMap = habit.getHabitMap();

        // create a mapping within a mapping so merging is possible
        Map<String, Object> updates = new HashMap<>();
        updates.put(habit.getHabitId(), habitMap);

        DB.collection("Habits").document(habit.getUserId())
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
     * @param userId the userId to which the habit belongs
     * @param habitId the is of the habit
     * @return
     */
    public Habit getHabit(String userId, String habitId) {
        AtomicReference<Habit> habit = new AtomicReference<Habit>();

        DB.collection("Habits").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("FIREBASE", "DocumentSnapshot data: " + document.get(habitId));
                                Map<String, Object> data = (HashMap<String, Object>) document.get(habitId);
                                habit.set(convertToHabit(data));

                            } else {
                                Log.d("FIREBASE", "No such document");
                            }
                        } else {
                            Log.d("FIREBASE", "get failed with ", task.getException());
                        }
                    }
        });
        return habit.get();
    }

    /**
     * This function is used to convert the raw Firestore data into an actual Habit object
     * @param docData The raw Firestore data from a Habits document
     * @return An instance of Habit
     */
    private Habit convertToHabit(Map<String, Object> docData) {
        Habit habit = new Habit();

        habit.setTitle((String) docData.get("title"));
        habit.setReason((String) docData.get("reason"));

        // Convert firestore timestamp to Date object
        Timestamp timestamp = (Timestamp) docData.get("dateCreated");
        habit.setDateCreated(timestamp.toDate());

        habit.setFrequency((ArrayList<Integer>) docData.get("frequency"));
        return habit;
    }
}
