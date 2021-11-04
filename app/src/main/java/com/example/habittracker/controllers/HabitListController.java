package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class HabitListController {
    private final FirebaseFirestore db;

    public HabitListController() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Loads all the habits from a specific Habits document in Firestore and returns an instance
     * of HabitList
     * @param uid The uid of the user whose habits to load
     * @return An instance of HabitList
     */
    public HabitList loadHabitList(String uid) {
        // load user data from the user doc in firestore
        AtomicReference<HabitList> habitList = new AtomicReference<HabitList>();
        db.collection("Habits").document(uid)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> docData = document.getData();
                    if (docData != null) {
                        habitList.set(convertToHabitList(docData, uid));
                    }
                    Log.d("Firestore", "Retrieved habit");
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
        return habitList.get();
    }

    /**
     * This function is used to convert the raw Firestore data into an actual HabitList object
     * @param docData The raw Firestore data from a Habits document
     * @param uid The uid of the user whose habit data this function is processing
     * @return An instance of HabitList
     */
    private HabitList convertToHabitList(Map<String, Object> docData, String uid) {
        HabitList habitList = new HabitList();
        Log.d("FIRESTORE DATA DEBUG", String.valueOf(docData));
        for (Map.Entry<String, Object> entry : docData.entrySet()) {
            Map<String, Object> habitData = (Map<String, Object>) entry.getValue();
            Log.d("Yup", habitData.toString());
            Habit habit = new Habit(entry.getKey(), uid, (String) habitData.get("title"),
                    (String) habitData.get("reason"), ((Timestamp) habitData.get("dateCreated")).toDate(),
                    (ArrayList<Integer>) habitData.get("frequency"), (boolean) habitData.get("canShare"));
            habitList.addHabit(habit);
        }
        return habitList;
    }
}
