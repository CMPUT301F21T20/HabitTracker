package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.classes.HabitEvent;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HabitEventController {
    private final FirebaseFirestore DB;

    public HabitEventController() {
        this.DB = FirebaseFirestore.getInstance();
    }

    /**
     * Saves a habitevent to the corresponding habitEvent document in Firestore. Will create a new document
     * if it doesn't exist and will always merge the habitEvent (pre-existing document data won't be overwritten)
     *
     * @param habitEvent The habitEvent to save in Firestore
     * @return True if the operation was successful and false otherwise
     */
    public Boolean saveHabitEvent(HabitEvent habitEvent) {
        AtomicBoolean success = new AtomicBoolean(false);
        Map<String, Object> habitEventMap = habitEvent.getHabitEventMap();

        // create a mapping within a mapping so merging is possible
        Map<String, Map<String, Object>> mapping = new HashMap<>();
        mapping.put(habitEvent.getHabitEventId(), habitEventMap);

        // save the new habitEvent and create doc if it doesn't already exists
        DB.collection("HabitEvents").document(habitEvent.getUserId())
                .set(mapping, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    success.set(true);
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
        return success.get();
    }
}
