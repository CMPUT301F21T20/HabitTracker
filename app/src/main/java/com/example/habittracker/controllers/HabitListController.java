package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;
import com.example.habittracker.interfaces.OnHabitListRetrieved;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * HabitListController is for sync local habit with data in firestore
 */
public class HabitListController {

    private static class Loader {
        static volatile HabitListController INSTANCE = new HabitListController();
    }

    private HabitListController() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static HabitListController getInstance() {
        return Loader.INSTANCE;
    }

    //Firestore instance
    private final FirebaseFirestore db;

    /**
     * Loads all the habits from a specific Habits document in Firestore and returns an instance
     * of HabitList
     * @param uid The uid of the user whose habits to load
     * @return An instance of HabitList
     */
    public void loadHabitList(String uid, OnHabitListRetrieved listener) {
        // load user data from the user doc in firestore
        db.collection("Habits").document(uid)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HabitList habitList = new HabitList();
                HabitListController.convertToHabitList(task.getResult(), habitList);
                listener.onHabitListRetrieved(habitList);
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });
    }

    /**
     * This function takes a habit document and populates the given habit list. It is useful when
     * loading the habit document or when defining a snapshot listener
     * @param doc A Firestore DocumentSnapshot of a Habit document
     * @param habitList The HabitList object to populate
     */
    public static void convertToHabitList(DocumentSnapshot doc, HabitList habitList) {
        if (doc.exists()) {
            Map<String, Object> docData = doc.getData();
            if (docData != null) {
//                ArrayList<Habit> listOfHabits = new ArrayList<>();
                Log.d("FIRESTORE DATA DEBUG", String.valueOf(docData));
                for (Map.Entry<String, Object> entry : docData.entrySet()) {
                    Map<String, Object> habitData = (Map<String, Object>) entry.getValue();
                    Log.d("Yup", habitData.toString());
                    if (habitData.get("dateCreated") == null) {
                        continue;
                    }
                    Habit habit = new Habit(entry.getKey(), doc.getId(), (String) habitData.get("title"),
                            (String) habitData.get("reason"), ((Timestamp) habitData.get("dateCreated")).toDate(),
                            (ArrayList<Integer>) habitData.get("frequency"), (boolean) habitData.get("canShare"));
                    habitList.addHabit(habit);
                }

//                habitList.setHabitList(listOfHabits);
            }
            Log.d("Firestore", "Retrieved habit data");
        } else {
            Log.d("Firestore", "No such document");
        }
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
     *
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

}
