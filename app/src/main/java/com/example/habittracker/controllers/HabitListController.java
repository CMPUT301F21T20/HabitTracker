package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;
import com.example.habittracker.interfaces.OnHabitListRetrieved;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * HabitListController is for sync local habit with data in firestore
 */
public class HabitListController {

    //Firestore instant
    private final FirebaseFirestore db;
    private final HabitController habitController;

    public HabitListController() {
        this.db = FirebaseFirestore.getInstance();
        this.habitController = new HabitController();
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
     * Loads all the habits from a specific Habits document in Firestore and returns an instance
     * of HabitList
     * @param uid The uid of the user whose habits to load
     * @return An instance of HabitList
     */
    public void loadHabitListAsync(String uid, OnHabitListRetrieved listener) {
        db.collection("Habits").document(uid)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> docData = document.getData();
                    if (docData != null) {
                        HabitList habitList = convertToHabitList(docData, uid);
                        listener.onHabitListRetrieved(habitList);
                    }
                    Log.d("Firestore", "Retrieved habit");
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
                listener.onError(task.getException());
            }
        });

    }

    /**
     * This function is used to convert the raw Firestore data into an actual HabitList object
     * @param docData The raw Firestore data from a users Habit document
     * @param uid The uid of the user whose habit data this function is processing
     * @return An instance of HabitList
     */
    public HabitList convertToHabitList(Map<String, Object> docData, String uid) {
        HabitList habitList = new HabitList();
        Log.d("FIRESTORE DATA DEBUG", String.valueOf(docData));
        for (Map.Entry<String, Object> entry : docData.entrySet()) {
            Map<String, Object> habitData = (Map<String, Object>) entry.getValue();
            Log.d("Yup", habitData.toString());
            if (habitData.get("dateCreated") == null) {
                continue;
            }
            Habit habit = new Habit(entry.getKey(), uid, (String) habitData.get("title"),
                    (String) habitData.get("reason"), ((Timestamp) habitData.get("dateCreated")).toDate(),
                    (ArrayList<Integer>) habitData.get("frequency"), (boolean) habitData.get("canShare"));
            habitList.addHabit(habit);
        }
        return habitList;
    }

    /**
     * this function will add a habit object into the habitList and save it to Firestore
     * @param habit The habit to add to the list
     * @return True if successful false otherwise
     */
    public Boolean saveHabit(HabitList habitList, Habit habit) {
        Boolean success = habitController.saveHabit(habit);
        if (success) habitList.addHabit(habit);
        return success;
    }

    /**
     * this function deletes a habit from the habitList and from Firestore
     * @param habit The habit to delete from the list
     * @return True if successful false otherwise
     */
    public Boolean deleteHabit(HabitList habitList, Habit habit) {
        Boolean success = habitController.deleteHabit(habit);
        if (success) habitList.deleteHabit(habit);
        return success;
    }
}
