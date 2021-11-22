package com.example.habittracker.controllers;

import android.util.Log;

import com.example.habittracker.classes.HabitEvent;
import com.example.habittracker.classes.HabitEventList;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class HabitEventListController {

    //Firestore instant
    private final FirebaseFirestore db;
    private final HabitEventController habitEventController;

    public HabitEventListController() {
        this.db = FirebaseFirestore.getInstance();
        this.habitEventController = new HabitEventController();
    }

    /**
     * This function is used to convert the raw Firestore data into an actual HabitEventList object
     * @param docData The raw Firestore data from a users HabitEvent document
     * @param uid The uid of the user whose habitEvent data this function is processing
     * @return An instance of HabitEventList
     */
    public HabitEventList convertToHabitEventList(Map<String, Object> docData, String uid) {
        HabitEventList habitEventList = new HabitEventList();
        Log.d("FIRESTORE DATA DEBUG", String.valueOf(docData));
        for (Map.Entry<String, Object> entry : docData.entrySet()) {
            Map<String, Object> habitEventData = (Map<String, Object>) entry.getValue();
            Log.d("Yup", habitEventData.toString());
            if (habitEventData.get("createdDate") == null) {
                continue;
            }
            HabitEvent habitEvent;
            if (habitEventData.get("completedDate") != null) {
                habitEvent = new HabitEvent(entry.getKey(), uid, (boolean) habitEventData.get("isCompleted"),
                        (String) habitEventData.get("imageStorageNamePrefix"), (String) habitEventData.get("location"),
                        (String) habitEventData.get("comment"), ((Timestamp) habitEventData.get("createdDate")).toDate(),
                        ((Timestamp) habitEventData.get("completedDate")).toDate());
            }else{
                habitEvent = new HabitEvent(entry.getKey(), uid, (boolean) habitEventData.get("isCompleted"),
                        (String) habitEventData.get("imageStorageNamePrefix"), (String) habitEventData.get("location"),
                        (String) habitEventData.get("comment"), ((Timestamp) habitEventData.get("createdDate")).toDate(),
                        null);
            }
            habitEventList.addHabitEvent(habitEvent);
        }
        return habitEventList;
    }
}
