package com.example.habittracker.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.habittracker.classes.HabitEvent;
import com.example.habittracker.classes.HabitEventList;
import com.example.habittracker.interfaces.OnHabitListRetrieved;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HabitEventsController {
    private final FirebaseFirestore DB;

    private static class Loader {
        static volatile HabitEventsController INSTANCE = new HabitEventsController();
    }

    private HabitEventsController() {
        this.DB = FirebaseFirestore.getInstance();
    }

    public static HabitEventsController getInstance() {
        return Loader.INSTANCE;
    }

    public void loadHabitEvents(String uid, int months, OnHabitListRetrieved listener) {
        DB.collection("Users").document(uid).collection("HabitEvents")
                .whereEqualTo("startTimestamp", 10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Firestore", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    public static void convertToHabitEventList(Task<QuerySnapshot> successfulTask, HabitEventList heList) {
        heList.clearHabitEventList();
        for (QueryDocumentSnapshot doc : successfulTask.getResult()) {
            Map<String, Object> docData = doc.getData();
            if (docData != null) {
                for (Map.Entry<String, Object> entry : docData.entrySet()) {
                    // skip the startTimestamp value which is just a value used to sort documents by month
                    if (entry.getValue().equals("startTimestamp")) continue;
                    Map<String, Object> habitEventData = (Map<String, Object>) entry.getValue();
                    HabitEvent habitEvent = new HabitEvent();
                    heList.addHabitEvent(habitEvent);
                }

            }
        }
    }

    /**
     * Saves a habit event to the corresponding HabitEvent document in Firestore. Will create a new document
     * if it doesn't exist and will always merge the HabitEvent (pre-existing document data won't be overwritten)
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
